package com.axelor.dyn.web;

import com.axelor.dyn.db.DynMessage;
import com.axelor.dyn.service.DynService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

public final class DynController {

	@Inject
	private DynService service;
	
	public void showReply(ActionRequest request, ActionResponse response) {
		
		Context ctx = request.getContext();
		System.err.println("XXXX: " + ctx);
		boolean canReply = "btnReply".equals(ctx.get("_signal"));
		
		response.setValues(ImmutableMap.of("canReply", canReply));
	}

	public void sendReply(ActionRequest request, ActionResponse response) {
		
		DynMessage ctx = request.getContext().asType(DynMessage.class);

		DynMessage reply = new DynMessage();
		reply.setSubject(ctx.getSubject());
		reply.setBody((String) request.getContext().get("reply"));
		
		try {
			service.sendMessage(ctx, reply);
			response.setReload(true);
		} catch (Exception e) {
			response.setException(e);
		}
	}
}
