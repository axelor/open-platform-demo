/**
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
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
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 */
package com.axelor.dyn.web;

import java.util.List;

import com.axelor.dyn.db.DynMessage;
import com.axelor.dyn.service.DynService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.rpc.Response;
import com.google.inject.Inject;

public final class DynController {

	@Inject
	private DynService service;
	
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
		List<?> all = DynMessage.all().order("createdOn").fetch();
		
		response.setData(all);
		
		response.setOffset(0);
		response.setTotal(all.size());
		response.setStatus(Response.STATUS_SUCCESS);
	}
}
