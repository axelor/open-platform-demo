package com.axelor.dyn.service;

import java.util.Set;

import com.axelor.contact.db.Contact;
import com.axelor.dyn.db.DynMessage;
import com.google.common.collect.Sets;
import com.google.inject.persist.Transactional;

public class DynService {

	@Transactional
	public DynMessage sendMessage(DynMessage context, DynMessage reply) {

		if (context.getRecipients() != null) {
			Set<Contact> all = Sets.newHashSet();
			for(Contact c : context.getRecipients()) {
				all.add(Contact.find(c.getId()));
			}
			reply.setRecipients(all);
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
