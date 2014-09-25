/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2014 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.dyn.service;

import javax.inject.Inject;

import com.axelor.auth.AuthUtils;
import com.axelor.dyn.db.DynMessage;
import com.axelor.dyn.db.repo.DynMessageRepository;
import com.google.inject.persist.Transactional;

public class DynService {

	@Inject
	private DynMessageRepository messages;

	@Transactional
	public DynMessage sendMessage(DynMessage context, DynMessage reply) {

		if (reply.getSubject() == null) {
			reply.setSubject(context.getSubject());
		}
		if (context.getRecipients() != null) {
			reply.setRecipients(context.getRecipients());
		}
		if (reply.getAuthor() == null) {
			reply.setAuthor(AuthUtils.getUser());
		}

		if (context.getId() != null) {
			DynMessage parent = messages.find(context.getId());
			if (parent.getParent() != null) {
				parent = parent.getParent();
			}
			if (parent != null) {
				reply.setParent(parent);
			}
		}
		return messages.save(reply);
	}
}
