package com.axelor.dyn.service;

import com.axelor.dyn.db.DynMessage;
import com.google.inject.persist.Transactional;

public class DynService {

	@Transactional
	public DynMessage sendMessage(DynMessage context, DynMessage reply) {

		if (reply.getSubject() == null) {
			reply.setSubject("No Subject");
		}
		if (context.getRecipients() != null) {
			reply.setRecipients(context.getRecipients());
		}

		DynMessage message = reply;
		if (context.getId() != null) {
			message = DynMessage.find(context.getId());
			if (reply != null) {
				message.addReply(reply);
			}
		}
		return message.save();
	}
}
