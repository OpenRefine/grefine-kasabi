package com.kasabi.grefine.model;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.model.OverlayModel;
import com.google.refine.model.Project;
import com.google.refine.rdf.Util;
import com.google.refine.rdf.vocab.VocabularyIndexException;

public class KasabiProject implements OverlayModel {

	final static Logger logger = LoggerFactory.getLogger("RdfSchema");
	
	private URI datasetURI;
	private String apiKey;
	
	public static KasabiProject getProject(Project project) throws VocabularyIndexException, IOException {
		synchronized (project) {
			KasabiProject kasabiProject = (KasabiProject) project.overlayModels
					.get("kasabiProject");
			if (kasabiProject == null) {
				kasabiProject = new KasabiProject();

				project.overlayModels.put("kasabiProject", kasabiProject);
				project.getMetadata().updateModified();
			}
			return kasabiProject;
		}
	}
	
	public KasabiProject() {
		System.out.println("Created Kasabi Project");
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void dispose(Project project) {
	}

	@Override
	public void onAfterSave(Project project) {
	}

	@Override
	public void onBeforeSave(Project project) {
	}

	public void setDatasetURI(URI dataset) {
		this.datasetURI = dataset;
	}
	
	public URI getDatasetURI() {
		return datasetURI;
	}
	
	@Override
	public void write(JSONWriter writer, Properties options)
			throws JSONException {
        writer.object();
        writer.key("datasetUri"); 
        writer.value(datasetURI);
        writer.key("apiKey");
        writer.value(apiKey);
        writer.endObject();
	}
	
	public static KasabiProject reconstruct(JSONObject o) throws JSONException {
		KasabiProject project = new KasabiProject();
		project.setDatasetURI( Util.buildURI(o.getString("datasetUri")) );
		project.setApiKey( o.getString("apiKey") );
		return project;
	}
	
    public static KasabiProject load(Project project, JSONObject obj) throws Exception {
        return reconstruct(obj);
    }
	
}
