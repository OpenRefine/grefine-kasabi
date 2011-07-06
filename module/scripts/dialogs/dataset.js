var DatasetManager = {};

DatasetManager.fetch = function(dataset, callback) {
	$.get("/command/kasabi-extension/kasabi-fetch-dataset?" + $.param({project: theProject.id }),
			{
				dataset: dataset
			},
			function(data){
				if ( data.code == "error" ) {
					alert("Error: unable to fetch dataset metadata: " + dataset);
				} else {
					if ( callback ) {
						callback(data);
					}
				}
			}
			,"json");	
}

DatasetManager.registerReconciliationService = function(dataset, apikey, callback) {
	DatasetManager.fetch(dataset, function(data) {
		reconServices = data[dataset]["http://labs.kasabi.com/ns/services#reconciliationEndpoint"] ;
		if ( reconServices ) {
			recon = reconServices[0]["value"];
			service_url = recon + "?apikey=" + apikey;
			
			if(!ReconciliationManager.getServiceFromUrl(service_url)){
				ReconciliationManager.registerStandardService( service_url , callback);
				ReconciliationManager.save();
			}			
		}
	});
}