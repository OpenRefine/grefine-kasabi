KasabiMenuBar = {};

KasabiMenuBar.configureDataset = function() {
	new KasabiConfigureDatasetDialog();
};

KasabiMenuBar.uploadData = function() {
    if (!theProject.overlayModels.rdfSchema) {
        alert(
            "You haven't done any RDF schema alignment yet!"
        );
        return;
    }
    if (!theProject.overlayModels.kasabiProject) {
    	alert(
    		"You must first configure your dataset and API key"
    	);
    } else {    	
    	new KasabiLoadingDialog();
    }
};

//extend the column header menu
$(function(){
    
	ExtensionBar.MenuItems.push(
			{
				"id":"kasabi",
				"label": "Kasabi",
				"submenu" : [
					{
						"id": "kasabi/connect-dataset",
						label: "Configure Dataset & API Key",
						click: function() { KasabiMenuBar.configureDataset(); }
					},				             
					{
						"id": "kasabi/upload-data",
						label: "Upload Data",
						click: function() { KasabiMenuBar.uploadData(); }
					},
			    ]
			}
	);
});