function KasabiLoadingDialog() {
    this._createDialog();	
}

KasabiLoadingDialog.prototype._createDialog = function() {
    var self = this;    
    var dialog = $( DOM.loadHTML("kasabi-extension", "scripts/dialogs/loading-dialog.html") );
        
    self._elmts = DOM.bind(dialog);
    
    self._elmts.cancelButton.click(function() { self._dismiss(); });
    self._elmts.loadButton.click( function() { self._upload(); } );
    
	self._elmts.previewPane.empty().html('<img src="images/large-spinner.gif" title="loading..."/>');
	
    schema = theProject.overlayModels.rdfSchema;
    
	$.post(
		    "/command/rdf-extension/preview-rdf?" + $.param({ project: theProject.id }),
	        { schema: JSON.stringify(schema), engine: JSON.stringify(ui.browsingEngine.getJSON()) },
	        function(data) {
	        	self._elmts.previewPane.empty();
	        	self._elmts.previewPane.text(data.v);
	        	self._elmts.loadButton.removeAttr("disabled").button("refresh");
		    },
		    "json"
		);
    
	self._level = DialogSystem.showDialog(dialog);
}

KasabiLoadingDialog.prototype._dismiss = function() {
    DialogSystem.dismissUntil(this._level - 1);
};

KasabiLoadingDialog.prototype._upload = function() {
	dataset = theProject.overlayModels.kasabiProject.updateUri;
	apikey = theProject.overlayModels.kasabiProject.apiKey;
	
	var self = this;
	self._elmts.loadButton.attr("disabled", "disabled");
	
	var dismissBusy = DialogSystem.showBusy();
	
	$.post(
		    "/command/kasabi-extension/kasabi-dataset-upload",
	        { 
		    	project: theProject.id,		    	
		    	engine: JSON.stringify(ui.browsingEngine.getJSON()),
		    	dataset: dataset,
		    	apikey: apikey,
		    	schema: JSON.stringify(schema)		    	
		    },
	        function(data) {
		    	dismissBusy();
		    	
                var body = self._elmts.dialogBody;
                if ("status" in data && data.status == 202) {
                    body.html(
                        '<div class="kasabi-loading-tripleloader-message">' +
                            '<h2>' + data.triplesUploaded + ' triples successfully loaded</h2>' + 
                            '<h2>' + data.triplesFailed + ' triples failed to upload</h2>' +
                        '</div>'
                    );
                    self._end();
                } else {
                    body.html(
                            '<div class="kasabi-loading-tripleloader-message">' +
                                '<h2>Error loading data</h2>' + 
                            '</div>'
                        );
                        self._end();
                }
		    	
		    },
		    "json"
		);	
};