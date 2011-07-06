package com.kasabi.grefine.commands;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.refine.commands.Command;
import com.google.refine.model.Project;
import com.google.refine.rdf.RdfSchema;
import com.google.refine.rdf.Util;
import com.google.refine.rdf.vocab.VocabularyIndexException;
import com.kasabi.grefine.model.KasabiProject;

public class ConfigureDatasetCommand extends Command {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            Project project = getProject(request);
            String apikey = request.getParameter("apiKey");
            String base = request.getParameter("datasetURI");
            URI datasetUri;
            try{
            	datasetUri = Util.buildURI(base);
            }catch(RuntimeException re){
            	respondException(response, re);
            	return;
            }
            KasabiProject.getProject(project).setDatasetURI(datasetUri);
            KasabiProject.getProject(project).setApiKey(apikey);
            
            //FIXME this won't apply base if we haven't done any schema alignment
			RdfSchema rdfSchema = (RdfSchema) project.overlayModels.get("rdfSchema");
			if (rdfSchema != null) {
				rdfSchema.setBaseUri( Util.buildURI(base + "/") );
			}
			
            project.getMetadata().updateModified();
            
            respond(response,"OK","Dataset URI saved");
            
        }catch(Exception e){
            respondException(response, e);
        }
		
	}

	
}
