A simple plugin for [Google Refine][0] that provides support for loading data 
into a [Kasabi][1] Dataset.

The plugin builds on the [RDF Extension for Google Refine][2] which must 
also be installed. 

The RDF extension provides the functionality for mapping data to RDF and 
the basic export functionality. This extension provides additional functionality 
for uploading data via the Kasabi APIs.

AUTHOR
------

Leigh Dodds (leigh.dodds@talis.com)

INSTALLATION
------------

You must first follow the instructions to download and install the [RDF Extension for 
Google Refine][2]. 

Once that extension has been successfully installed, download the code for this 
project and unpack it into your Google Refine extension directory alongside 
the RDF extensions.

E.g. on Ubuntu the RDF extension will be installed into:

 ~/.local/share/google/refine/extensions/grefine-rdf-extension
 
So this extension should be installed into:

 ~/.local/share/google/refine/extensions/grefine-kasabi

If the module has been successfully installed then you should have a 
new Kasabi menu item on the Extensions menu inside your Google Refine 
project.

USAGE
-----

Currently the extension provides two menu options:

The "Configure Dataset and API Key" menu allows you to configure the URI 
of the Kasabi dataset into which your data is to be uploaded. You can also 
enter your API key that will authorize you to submit data to that dataset.

The "Upload Data" menu item provides you with a quick preview of your RDF 
data, before allowing you to submit it for loading via the Kasabi API. Large 
datasets will be automatically chunked into batches for loading.

CAVEATS
-------

This extension is currently a work in progress and as such has a number of 
limitations and caveats.

There is currently no workflow support for testing out uploads, or support for 
editing data to submit only changes. This means if you find data errors you 
are better off resetting your dataset and reloading the data.

It is also possible for only some of your data to be successfully loaded. E.g. 
because of transient communication errors, or due to some invalid RDF, e.g. 
base URIs. Error reporting is currently sketchy and there is no support for 
carrying out partial uploads.

Further improvements to the extension are planned to help overcome these problems.
However as it stands, it still provides a relatively simple and easy way to upload 
data to Kasabi.

SOURCE CODE
-----------

The source for the project is maintained in github at:

http://github.com/kasabi/grefine-kasabi

Patches and bug reports gratefully received!
  
LICENSE
-------

Copyright 2011 Talis Systems Ltd 
 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
  
You may obtain a copy of the License at 
  
http://www.apache.org/licenses/LICENSE-2.0 
  
Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
  
See the License for the specific language governing permissions and limitations 
under the License.

[0]: [https://code.google.com/p/google-refine/]
[1]: [http://beta.kasabi.com]
[2]: [http://lab.linkeddata.deri.ie/2010/grefine-rdf-extension/] 