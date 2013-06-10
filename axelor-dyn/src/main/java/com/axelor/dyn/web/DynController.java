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
