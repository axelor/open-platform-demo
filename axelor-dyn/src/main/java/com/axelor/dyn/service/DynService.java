package com.axelor.dyn.service;

import com.axelor.auth.AuthUtils;
import com.axelor.dyn.db.DynMessage;
import com.google.inject.persist.Transactional;

public class DynService {

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
			DynMessage parent = DynMessage.find(context.getId());
			if (parent.getParent() != null) {
				parent = parent.getParent();
			}
			if (parent != null) {
				reply.setParent(parent);
			}
		}
		return reply.save();
	}
}
