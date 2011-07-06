function KasabiConfigureDatasetDialog() {
    this._createDialog();	
}

KasabiConfigureDatasetDialog.prototype._createDialog = function() {
    var self = this;    
    var dialog = $( DOM.loadHTML("kasabi-extension", "scripts/dialogs/configure-dataset-dialog.html") );
        
    self._schema = theProject.overlayModels.rdfSchema;
    self._kasabiProject = theProject.overlayModels.kasabiProject;
    
    self._elmts = DOM.bind(dialog);

    if ( !self._kasabiProject ) {
    	self._kasabiProject = {};
    	theProject.overlayModels.kasabiProject = self._kasabiProject;    	
    }
    
    if ( self._kasabiProject.datasetUri ) {  
    	self._elmts.dataset.empty().val( self._kasabiProject.datasetUri );
    }
    if ( self._kasabiProject.apiKey ) {  
    	self._elmts.apikey.empty().val( self._kasabiProject.apiKey );
    }
    
    
    self._elmts.cancelButton.click(function() { self._dismiss(); });
    self._elmts.saveButton.click( function() { self._saveURI(); } );
    
	self._level = DialogSystem.showDialog(dialog);
}

KasabiConfigureDatasetDialog.prototype._saveURI = function() {
	dataset = $("#kasabi-dataset").val();	
	apikey = $("#kasabi-apikey").val();
	
	var self = this;
	
	self._kasabiProject.datasetUri = dataset;
	self._kasabiProject.apiKey = apikey;
	
    if ( self._schema ) {
    	self._schema.baseUri = dataset + "/";
    }
    
	$.post("/command/kasabi-extension/kasabi-configure-dataset?" + $.param({project: theProject.id }),
			{
				datasetURI: dataset,
				apiKey: apikey
			},
			function(data){
		if (data.code === "error"){
			alert('Error:' + data.message)
		}else{
			theProject.overlayModels.kasabiProject = self._kasabiProject;
			
			DatasetManager.registerReconciliationService(dataset, apikey);
			self._dismiss();
		}
	},"json");
};

KasabiConfigureDatasetDialog.prototype._dismiss = function() {
    DialogSystem.dismissUntil(this._level - 1);
};
