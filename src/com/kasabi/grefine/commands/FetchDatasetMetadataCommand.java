package com.kasabi.grefine.commands;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.refine.commands.Command;
import com.google.refine.model.Project;
import com.kasabi.grefine.model.KasabiProject;

public class FetchDatasetMetadataCommand extends Command {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Project project = getProject(request);
			String datasetUri = request.getParameter("dataset");
			String apikey = KasabiProject.getProject(project).getApiKey();
			
			HttpResponse resp = getData(datasetUri, apikey);
	        response.setCharacterEncoding("UTF-8");			
			response.setHeader("Content-Type", "application/json");
			if ( resp.getStatusLine().getStatusCode() == 200 ) {
				resp.getEntity().writeTo( response.getOutputStream() );
			} else {
				response.sendError(resp.getStatusLine().getStatusCode(), 
						resp.getStatusLine().getReasonPhrase());
			}
			
		} catch (Exception e) {
			respondException(response, e);			
		}
	}
	
	private HttpResponse getData(String uri, String apikey) 
		throws ClientProtocolException, IOException {
		System.out.println(uri);
    	HttpClient client = new DefaultHttpClient(); 
    	HttpGet get = new HttpGet(uri);
        get.addHeader("Accept", "application/json");    	
        get.addHeader("X_KASABI_APIKEY", apikey);
        
        return client.execute(get);
	}
}
