package com.axelor.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ImportUtils {

  public ImportUtils() {}

  public static Path findByFileName(Path path, String fileName) throws IOException {
    Path result = null;
    try (Stream<Path> pathStream =
        Files.find(
            path,
            Integer.MAX_VALUE,
            (p, basicFileAttributes) -> p.getFileName().toString().startsWith(fileName))) {
      result = pathStream.findFirst().orElse(null);
    }
    return result;
  }
}
