/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2012-2014 Axelor (<http://axelor.com>).
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
package com.axelor.dyn.web;

import java.util.List;

import com.axelor.dyn.db.DynMessage;
import com.axelor.dyn.db.repo.DynMessageRepository;
import com.axelor.dyn.service.DynService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.rpc.Response;
import com.google.inject.Inject;

public final class DynController {

	@Inject
	private DynService service;
	
	@Inject
	private DynMessageRepository messages;

	public void showReply(ActionRequest request, ActionResponse response) {
		
		Context ctx = request.getContext();
		boolean canReply = "btnReply".equals(ctx.get("_signal"));

		response.setValue("canReply", canReply);
	}

	public void sendReply(ActionRequest request, ActionResponse response) {
		
		DynMessage ctx = request.getContext().asType(DynMessage.class);

		DynMessage reply = new DynMessage();
		reply.setSubject(ctx.getSubject());
		reply.setBody((String) request.getContext().get("reply"));
		
		reply = service.sendMessage(ctx, reply);

		// send a view specific signal
		response.setSignal("trail:record", reply);

		response.setValue("reply", null); 		// clear reply field
		response.setValue("canReply", false);	// close reply field
		
		response.setStatus(ActionResponse.STATUS_SUCCESS);
	}
	
	public void showReplies(ActionRequest request, ActionResponse response) {

		Context ctx = request.getContext();

		response.setSignal("trail:expand", ctx.get("id"));
		response.setStatus(ActionResponse.STATUS_SUCCESS);
	}

	public void findAll(ActionRequest request, ActionResponse response) {
		List<?> all = messages.all().order("createdOn").fetch();
		
		response.setData(all);
		
		response.setOffset(0);
		response.setTotal(all.size());
		response.setStatus(Response.STATUS_SUCCESS);
	}
}
