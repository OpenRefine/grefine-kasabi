var html = "text/html";
var encoding = "UTF-8";

importPackage(com.kasabi.grefine.commands);
importPackage(com.kasabi.grefine.model);

/*
 * Function invoked to initialize the extension.
 */
function init() {

	/*
	 * Commands
	 */
	var RefineServlet = Packages.com.google.refine.RefineServlet;

	var uploadCommand = new Packages.com.kasabi.grefine.commands.UploadDataCommand();
    RefineServlet.registerCommand( module, "kasabi-dataset-upload", uploadCommand );
    RefineServlet.registerCommand(module, "kasabi-configure-dataset", new Packages.com.kasabi.grefine.commands.ConfigureDatasetCommand() );
    RefineServlet.registerCommand(module, "kasabi-fetch-dataset", new Packages.com.kasabi.grefine.commands.FetchDatasetMetadataCommand() );
    
    /*
     *  Attach a kasabi project model to each project.
     */
    Packages.com.google.refine.model.Project.registerOverlayModel(
        "kasabiProject",
        Packages.com.kasabi.grefine.model.KasabiProject);
        
    /*
     *  Client-side Resources
     */    
	var ClientSideResourceManager = Packages.com.google.refine.ClientSideResourceManager;
    
    // Script files to inject into /project page
    ClientSideResourceManager.addPaths(
        "project/scripts",
        module,
        [         
            "scripts/dialogs/dataset.js",         
            "scripts/dialogs/loading-dialog.js",
            "scripts/dialogs/configure-dataset-dialog.js",
            "scripts/menu-bar-extensions.js"            
        ]
    );
    
    // Style files to inject into /project page
    ClientSideResourceManager.addPaths(
        "project/styles",
        module,
        [
            "styles/kasabi-extension.css"
        ]
    );
}
