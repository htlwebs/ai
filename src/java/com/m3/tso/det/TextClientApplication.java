package com.m3.tso.det;
import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class TextClientApplication {

	
	
	public static AIResponse getData(String line) {

		AIConfiguration configuration = new AIConfiguration(
				"27ae0bf0bf0a4270bba678eb098460d3");

		AIDataService dataService = new AIDataService(configuration);

		try {
			AIRequest request = new AIRequest(line);

			AIResponse response = dataService.request(request);
//
//			if (response.getStatus().getCode() == 200) {
//				System.out.println(response.getResult().getFulfillment()
//						.getSpeech());
//			} else {
//				System.err.println(response.getStatus().getErrorDetails());
//			}
			return response;
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return null;
		}

		
	}


}
