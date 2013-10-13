/**
 * Copyright (c) 2012-2013 Axelor. All Rights Reserved.
 *
 * The contents of this file are subject to the Common Public
 * Attribution License Version 1.0 (the “License”); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://license.axelor.com/.
 *
 * The License is based on the Mozilla Public License Version 1.1 but
 * Sections 14 and 15 have been added to cover use of software over a
 * computer network and provide for limited attribution for the
 * Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 *
 * Software distributed under the License is distributed on an “AS IS”
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is part of "Axelor Business Suite", developed by
 * Axelor exclusively.
 *
 * The Original Developer is the Initial Developer. The Initial Developer of
 * the Original Code is Axelor.
 *
 * All portions of the code written by Axelor are
 * Copyright (c) 2012-2013 Axelor. All Rights Reserved.
 */
import groovy.io.FileType

class EhCacheHelper {

	private static final List<String> CORE = [
		"com.axelor.auth.db",
		"com.axelor.meta.db",
		"com.axelor.wkf.db",
	]

	// add preferred cache strategy modes (by package, by class/collection)
	// - package = applies to all the classes/collections
	// - class name = applies to the class and all it's collections
	// - collection name = applies to the collection
	private static final Map<String, String> MODES = [
		"com.axelor.auth": "nonstrict-read-write",
		//"com.axelor.auth.db.User": "read-write",
		//"com.axelor.auth.db.User.roles": "nonstrict-read-write",
		"com.axelor.meta": "nonstrict-read-write",
		"com.axelor.wkf": "read-write"
	]
	
	static String bigName
	
	static List<String> regions = []

	static class PropertyGenerator {

		private final File searchPath
		private final File outputPath
	
		private List<String> coreNames = []
		private List<String> appNames = []
		
		public PropertyGenerator(File searchPath, File outputPath) {
			this.searchPath = searchPath
			this.outputPath = outputPath
		}
		
		private void add(String pkg, String name) {
			if (CORE.contains(pkg)) {
				coreNames.add(name)
			} else {
				appNames.add(name)
			}
		}
	
		void process(File input) {
	
			def records = new XmlSlurper().parse(input)
			def mod = records.module.'@name' as String
			def pkg = records.module.'@package' as String
			
			records.entity.each { entity ->
				def obj = pkg + "." + entity.'@name' as String
				add(pkg, obj)
				entity."*".each { field ->
					if (!field.name().endsWith("-to-many")) return
					def fname = obj + "." + field.'@name' as String
					add(pkg, fname)
				}
			}
		}
	
		private static String HEADER = """# Configure cache for individual object and collection fields
#
# <class.name>[.collection] = <cache-mode>[,cache-region]
#
# The `cache-region` is optional and in that case the default cache is used.
# Please see ehcache and hibernate documentation for more details.
#
# Configure each cache region accordingly. If you are using BigMemory Go with
# 32GB or more memory allocated, you can use single big cache region for all
# objects or allocate enough memory to each cache region
#
# The cache mode determines cache concurrency strategy, can be:
#    read-only               = if objects are not updated (immutable objects),
#                              almost all objects are mutable so not useful
#                              in most cases
#    read-write              = if objects are updated frequently, hibernate
#                              uses lock on cache access to ensure cache
#                              synchronization
#    nonstrict-read-write    = if objects are rarely updated, hibernate does not
#                              use lock so cache may be unsynchronized at a time
#
#
# IMPORTANT:
#    Listed classes & collection fields must exist else hibernate will throw
#    exception during initialization.
#
#    Also, sometime you may need to clear the cache directory when configuration
#    changes are not compatible with previous cache files.
#"""
	
		private void doPrint(String module, List<String[]> vals, OutputStream out) {
			out.println """
#
# $module
#
"""
			int max = 0
			vals.each {String[] parts ->
				String first = parts[0]
				if (first.length() > max) max = first.length()
			}
	
			vals.each {String[] parts ->
				String first = parts[0]
				String last = parts[1]
				out.println String.format("%-${max}s = $last", first)
			}
		}
	
		void generate() {
	
			searchPath.eachFileRecurse FileType.FILES, { File file ->
				if (file.directory || !file.name.endsWith(".xml")) return
				if (file.path.contains("src/main/resources/domains/")) {
					process file
				}
			}
	
			OutputStream out = System.out
			if (outputPath) {
				out = new PrintStream(outputPath)
			}
	
			out.println HEADER
	
			coreNames.sort()
			appNames.sort()
	
			String last = null
			List<String[]> vals = []
			
			if (bigName) regions.add(bigName)
				
			(coreNames + appNames).each { String name ->
				def pat = ~/(((.*?)\.([^.]+))\.db\.([^.]+?))(\.[^.]+)?/
				def mat = pat.matcher(name)
				
				if (mat.matches()) {
					def current = mat.group(4)
					if (last != current) {
						if (last != null) {
							doPrint(last, vals, out)
						}
						last = current
						vals = []
					}
					def entity = mat.group(1)
					def pkg = mat.group(2)
					def mode = MODES[name]
					def region = last
					if (!mode) mode = MODES[entity]
					if (!mode) mode = MODES[pkg]
					if (!mode) mode = "read-write"
					if (bigName) region = bigName
					vals += [[name, "$mode,$region"] as String[]]
				}
			}

			if (last != null) {
				doPrint(last, vals, out)
			}
	
			if (outputPath) {
				out.flush()
				out.close()
			}
		}
		
		void generateConfig(String configPath) {
			def output = new File(configPath)
			def gen = new ConfigGenerator(output)
			gen.generate()
		}
	}

	static class ConfigGenerator {
		
		private static final String HEAD = """<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
  name="ehcache" updateCheck="false">
  
  <!-- use unique non-tmpdir in production mode -->
  <!-- avoid disk store if you don't use localRestartable persistence -->
  <!-- <diskStore path="java.io.tmpdir/my-cache"/>  -->

  <!-- cache monitoring (don't use in production)-->
  <!-- TMC  -->
  <!-- <managementRESTService enabled="true" bind="0.0.0.0:9888" /> -->
  <!-- OR
  <cacheManagerPeerListenerFactory
    class="org.terracotta.ehcachedx.monitor.probe.ProbePeerListenerFactory"
    properties="monitorAddress=localhost, monitorPort=9889, memoryMeasurement=true"/>
  -->

  <defaultCache
    statistics="false"
    maxEntriesLocalHeap="10000"
    eternal="false"
    timeToIdleSeconds="120"
    timeToLiveSeconds="120"
    maxEntriesLocalDisk="10000000"
    diskExpiryThreadIntervalSeconds="120"
    memoryStoreEvictionPolicy="LRU">
    <persistence strategy="localTempSwap"/>
  </defaultCache>

  <cache
    name="org.hibernate.cache.internal.StandardQueryCache"
    maxEntriesLocalHeap="100"
    eternal="false"
    timeToLiveSeconds="120">
    <persistence strategy="localTempSwap"/>
  </cache>

  <cache
    name="org.hibernate.cache.spi.UpdateTimestampsCache"
    maxEntriesLocalHeap="5000"
    eternal="true">
    <persistence strategy="localTempSwap"/>
  </cache>"""

  		private static final String FOOT = """
</ehcache>"""
		  
		private static final String REGION = """
  <cache
    name="%s"
    maxBytesLocalHeap="128M"
    maxMemoryOffHeap="32G"
    eternal="true">
    <persistence strategy="localTempSwap"/>
  </cache>"""

  		private File output
		
		public ConfigGenerator(File output) {
			this.output = output
		}

		public void generate() {
			PrintStream out = new PrintStream(output)
			try {
				out.println HEAD
				regions.each { String name ->
					out.println String.format(REGION, name)	
				}
				out.println FOOT
			} finally {
				out.close()
			}
		}
	}
	
	static main(String[] args) {

		def cli = new CliBuilder(usage: 'EhCacheBuilder [options]')

		cli.h("show this help")
		cli.s(args: 1, argName: "path", "search path for domain xml (default current dir)")
		cli.o(args: 1, argName: "file", "output file for object config (default to stdout)")
		cli.O(args: 1, argName: "file", "output file for ehcache config (bigmemory compatible)")
		cli.r(args: 1, argName: "name", "use the given cache region (if using bigmemory)")

		def opts = cli.parse(args);
		if (opts == null) {
			System.exit(-1);
		}
		if (opts.h) {
			cli.usage()
			System.exit(-1);
		}

		String sp = opts.s ? opts.s : "."
		String op = opts.o ? opts.o : null

		File searchPath = new File(sp)
		File outputPath = null
		if (op) {
			outputPath = new File(op)
		}

		if (!searchPath.exists()) cli.usage()
		
		PropertyGenerator gen = new PropertyGenerator(searchPath, outputPath)
		if (opts.r) bigName = opts.r
		try {
			gen.generate()
			if (opts.O) {
				gen.generateConfig(opts.O)
			}
		} catch (Exception e) {
			//e.printStackTrace()
			cli.usage()
		}
	}
}
