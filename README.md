opentree-taxomachine
====================
Taxomachine is a tool for merging, exploring, and exporting taxonomies for use with the Open Tree of Life project. 

Installation
---------------
taxomachine is managed by Maven v. 3 (including the dependencies). In order to compile and build treemachine, it is easiest to let Maven do the hard work.

On Ubuntu you can install Maven with:

	sudo apt-get install maven

It is also necessary to install the forester library, which must be installed into a local repo. To install forester, run the script:

	sh mvn_install_forester.sh

This will install forester into a local maven repo inside of the ~/.m2 directory. This only needs to be done once unless you clear out your ~./m2 directory.

Compiling
--------------

Once Maven is installed and the local forester repo has been successfully added, you can compile the standalone program with:

	sh mvn_cmdline.sh

Or the server plugin with:

	sh mvn_serverplugins.sh

Installing the server plugin
--------------

To expose the functionality of the server plugin, you must place it into your neo4j plugins directory, and restart the neo4j server. For example:

	mv target/opentree-neo4j-plugins-0.0.1-SNAPSHOT.jar /opt/neo4j-community-1.8/plugins
	/opt/neo4j-community-1.8/bin/neo4j restart

To allow inter-domain access to the server plugin (e.g. to call the RESTful services from another domain) it may be necessary to set the Access-Control-Allow-Origin setting on the Neo4j server. Currently, the default setting for this property seems to be no restrictions (Access-Control-Allow-Origin : *) when the server is running with RESTful features. See http://components.neo4j.org/neo4j-server/1.7/apidocs/org/neo4j/server/rest/web/AllowAjaxFilter.html for more information.

To make the Neo4j webserver accept connections from any machine, set the host address to 0.0.0.0 as described at http://docs.neo4j.org/chunked/stable/security-server.html.

Usage
--------------
For the standalone version, view the help message by running:

	java -jar target/taxomachine-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Bootstrapping from OTT
----------------------
If you have ott2.0 store in /data/ott2.0
and you want to create a new db at taxomachine.db
then you can use:

	java -Xmx10g -XX:-UseConcMarkSweepGC -jar target/taxomachine-0.0.1-SNAPSHOT-jar-with-dependencies.jar loadtaxsyn ott /data/ott2.0/taxonomy /data/ott2.0/synonyms taxomachine.db

System Properties
-----------------
Using:

	java ... -Dopentree.taxomachine.num.transactions=1000 ...  -jar target/taxomachine-0.0.1-SNAPSHOT-jar-with-dependencies.jar ...

in your invocation of taxomachine will sets the maximum # of transactions that will be buffered to 1000.
The default # of transactions is 10000. Higher numbers lead to faster importing of a new taxonomy, 
but using lower numbers will mean that taxomachine will require less memory.

