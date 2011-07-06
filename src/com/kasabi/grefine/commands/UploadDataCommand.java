package com.kasabi.grefine.commands;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.openrdf.model.BNode;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

import com.google.refine.ProjectManager;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.commands.Command;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import com.google.refine.rdf.Node;
import com.google.refine.rdf.RdfSchema;
import com.google.refine.rdf.vocab.Vocabulary;
import com.google.refine.util.ParsingUtilities;

public class UploadDataCommand extends Command {

	public static final int BATCH_SIZE = 10000;
	
	private RdfSchema schema;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    	ProjectManager.singleton.setBusy(true);
    	try {
    		schema = getSchema(request);
    		String dataset = request.getParameter("dataset");
    		String apikey = request.getParameter("apikey");

    		System.out.println(dataset);
    		System.out.println(apikey);
    		
    		List<Repository> models = populateModels(request);
    		
            int status = 200;
            String message = "";
            int triplesUploaded = 0;
            int triplesFailed = 0;
    		
			for (int i=0; i < models.size(); i++) {
				
				boolean success = true;
				Repository toUpload = models.get(i);

				//try to upload that Model
            	try {    		
            		System.out.println("Model " + i + " of " + models.size() );
            		System.out.println("Contains " + toUpload.getConnection().size() + " triples");

            		String turtle = serializeModel(toUpload);            		
            		status = submitData( turtle, dataset, apikey );
            		message = "TODO";
            		
            		System.out.println("Upload completed");  
            		
            	} catch (Exception e) {
            		if (e instanceof ServletException) {
            		//	throw (ServletException)e;
            		} else {
            		//	throw new ServletException(e);
            		}
            		success = false;
            		message = e.getMessage();
            	}		
            	
            	if (success) {
            		triplesUploaded += toUpload.getConnection().size();
            	} else {
            		triplesFailed += toUpload.getConnection().size();
            	}
			}
    		    		                        
        	try {
    	        JSONWriter writer = new JSONWriter(response.getWriter());
    	        writer.object();
    	        writer.key("status");
    	        writer.value( status );
    	        writer.key("message");
    	        writer.value( message );
    	        writer.key("triplesUploaded");
    	        writer.value( triplesUploaded );
    	        writer.key("triplesFailed");
    	        writer.value( triplesFailed );    	            	        
    	        writer.endObject();
        	} catch (JSONException e) {
        		throw new ServletException(e);
        	}
        	
    	} catch (Exception e) {
            respondException(response, e);
    	} finally {
    		ProjectManager.singleton.setBusy(false);
    	}
    }
		
    private List<Repository> populateModels(HttpServletRequest request) 
	throws Exception {
        Project project = getProject(request);
        Engine engine = getEngine(request, project);
    
        RdfRowVisitor visitor = new RdfRowVisitor(schema);
                
        FilteredRows filteredRows = engine.getAllFilteredRows();
        filteredRows.accept(project, visitor);
        return visitor.getModels();
    }

    private RdfSchema getSchema(HttpServletRequest request) throws Exception {       
        String jsonString = request.getParameter("schema");
        JSONObject json = ParsingUtilities.evaluateJsonStringToObject(jsonString);
        return RdfSchema.reconstruct(json);    	
    }
    
    protected static class RdfRowVisitor implements RowVisitor {
    	
        private RdfSchema schema;    	
    	private List<Repository> models;
        private java.net.URI baseUri;
        protected List<Node> roots;
        private BNode[] blanks;
        private Repository currentModel;
        private RepositoryConnection con;
        private int rowCount;
        
        private ValueFactory factory;
        
        public RdfRowVisitor(RdfSchema schema) {    
        	this.schema = schema;
        	this.baseUri = schema.getBaseUri();
            this.roots = schema.getRoots();
        	
        	this.models = new ArrayList<Repository>();
            this.currentModel = new SailRepository(new MemoryStore());
            this.models.add(this.currentModel);
            
            try{
            	currentModel.initialize();
            	this.con = currentModel.getConnection();
            	try{
            		factory = con.getValueFactory();
            		this.blanks = new BNode[schema.get_blanks().size()];
            		for (int i = 0; i < blanks.length; i++) {
            			blanks[i] = factory.createBNode();
            		}
            	}finally{
            		con.close();
            	}
            }catch(RepositoryException ex){
            	throw new RuntimeException(ex);
            }

            this.rowCount = 0;
        }
        
        public void end(Project project) {
            // do nothing            
        }

        public void start(Project project) {
            // do nothing            
        }

        public boolean visit(Project project, int rowIndex, Row row) {        	
        	rowCount++;
        	if (rowCount <= BATCH_SIZE) {
                try{
                	con = currentModel.getConnection();
                	try{
        				for(Node root:roots){
        	        		root.createNode(baseUri, factory, con, project, row, rowIndex,blanks);
        				}    	                        		
                	}finally{
                		con.close();
                	}
                }catch(RepositoryException ex){
                	throw new RuntimeException(ex);
                }
    	        
        	} else {

        		//start populating new model
                this.currentModel = new SailRepository(new MemoryStore());
                this.models.add(this.currentModel);
                
                try{
                	currentModel.initialize();
                	con = currentModel.getConnection();
                	try{
                		ValueFactory factory = con.getValueFactory();
                		this.blanks = new BNode[schema.get_blanks().size()];
                		for (int i = 0; i < blanks.length; i++) {
                			blanks[i] = factory.createBNode();
                		}
                	}finally{
                		con.close();
                	}
                }catch(RepositoryException ex){
                	throw new RuntimeException(ex);
                }
        		        		
    	        //reset count
    	        this.rowCount = 1;
    	        //add the row to model
				for(Node root:roots){
	        		root.createNode(baseUri, factory, con, project, row, rowIndex,blanks);
				}    	        

        	}
            return false;
        }
        
        public List<Repository> getModels() {
        	return models;
        }
    }
    
    private String serializeModel(Repository model) {
        StringWriter sw = new StringWriter();
        try{
        	RepositoryConnection con = model.getConnection();
        	try{
        		RDFWriter w = Rio.createWriter(RDFFormat.TURTLE, sw);
        		for(Vocabulary v:schema.getPrefixesMap().values()){
        			w.handleNamespace(v.getName(), v.getUri());
        		}
        		con.export(w);
			}finally{
        		con.close();
        	}
        }catch(RepositoryException ex){
        	throw new RuntimeException(ex);
        }catch(RDFHandlerException ex){
        	throw new RuntimeException(ex);
        }
        return sw.toString();
    }
    
    private int submitData(String data, String dataset, String apikey) 
    	throws ClientProtocolException, IOException {
    	HttpClient client = new DefaultHttpClient(); 
    	HttpPost post = new HttpPost(dataset + "/store");
    	post.setEntity( new StringEntity(data) );
        post.addHeader("Content-Type", "text/turtle");    	
        post.addHeader("X_KASABI_APIKEY", apikey);
        
        HttpResponse response = client.execute(post);
        int status = response.getStatusLine().getStatusCode();
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getFirstHeader("Location"));
        return status;
    }
}
