package opentree.taxomachine;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;

import opentree.tnrs.*;
import opentree.tnrs.adapters.iplant.TNRSAdapteriPlant;

//import org.apache.log4j.Logger;

public class MainRunner {
    public void taxonomyLoadParser(String[] args) {

        if (args.length < 4) {
            System.out.println("arguments should be: filename sourcename graphdbfolder");
            return;
        }

        String filename = args[1];
        String sourcename = args[2];
        String graphname = args[3];
        TaxonomyLoader tl = new TaxonomyLoader(graphname);

        if (args[0].compareTo("inittax") == 0) {
            System.out.println("initializing taxonomy from " + filename + " to " + graphname);
            tl.addInitialTaxonomyTableIntoGraph(filename, sourcename);

        } else if (args[0].compareTo("addtax") == 0) {
            if (args.length != 4) {
                System.out.println("arguments should be: filename sourcename graphdbfolder");
                return;
            }
            System.out.println("adding taxonomy from " + filename + " to " + graphname);
            tl.addAdditionalTaxonomyTableIntoGraph(filename, sourcename);

        } else {
            System.err.println("ERROR: not a known command");
            tl.shutdownDB();
            printHelp();
            System.exit(1);
        }
        tl.shutdownDB();
    }

    public void taxonomyQueryParser(String[] args) {
        if (args[0].equals("checktree")) {
            if (args.length != 4) {
                System.out.println("arguments should be: treefile focalgroup graphdbfolder");
                return;
            }

        } else if (args[0].equals("comptaxgraph")) {
            if (args.length != 4) {
                System.out.println("arguments should be: comptaxgraph query graphdbfolder outfile");
                return;
            }

        } else if (args.length != 3) {
            System.out.println("arguments should be: query graphdbfolder");
            return;
        }

        TaxonomyExplorer te = null;

        if (args[0].compareTo("comptaxtree") == 0) {
            String query = args[1];
            String graphname = args[2];
            te = new TaxonomyExplorer(graphname);
            System.out.println("constructing a comprehensive tax tree of " + query);
            te.buildTaxonomyTree(query);

        } else if (args[0].compareTo("comptaxgraph") == 0) {
            String query = args[1];
            String graphname = args[2];
            String outname = args[3];
            te = new TaxonomyExplorer(graphname);
            te.exportGraphForClade(query, outname);

        } else if (args[0].compareTo("findcycles") == 0) {
            String query = args[1];
            String graphname = args[2];
            te = new TaxonomyExplorer(graphname);
            System.out.println("finding taxonomic cycles for " + query);
            te.findTaxonomyCycles(query);

        } else if (args[0].compareTo("jsgraph") == 0) {
            String query = args[1];
            String graphname = args[2];
            te = new TaxonomyExplorer(graphname);
            System.out.println("constructing json graph data for " + query);
            te.constructJSONGraph(query);

        } else if (args[0].compareTo("checktree") == 0) {
            String query = args[1];
            String focalgroup = args[2];
            String graphname = args[3];
            te = new TaxonomyExplorer(graphname);
            System.out.println("checking the names of " + query + " against the taxonomy graph");
            te.checkNamesInTree(query, focalgroup);

        } else {
            System.err.println("ERROR: not a known command");
            te.shutdownDB();
            printHelp();
            System.exit(1);
        }
        te.shutdownDB();
    }

    public void parseTNRSRequest(String args[]) {
        String graphName = args[2];
        TNRSQuery tnrs = new TNRSQuery(graphName);
        TNRSAdapteriPlant iplant = new TNRSAdapteriPlant();
        TNRSMatchSet results = null;
        
        if (args[0].compareTo("tnrsbasic") == 0) {
            
            // TODO: use MRCA of all provided names as query context
            String[] searchStrings = args[1].split("\\s*\\,\\s*");
            results = tnrs.getMatches(searchStrings, iplant);
            
        } else if (args[0].compareTo("tnrstree") == 0) {

            // read in the treefile
            final File treefile = new File(args[1]);
            PhylogenyParser parser = null;
            try {
                parser = ParserUtils.createParserDependingOnFileType(treefile, true);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            Phylogeny[] phys = null;
            try {
                phys = PhylogenyMethods.readPhylogenies(parser, treefile);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            // TODO: use MRCA of tree as query context
            // TODO: use tree structure to help differentiate homonyms
            
            // search for the names
            String[] treeTipNames = phys[0].getAllExternalNodeNames();
            results = tnrs.getMatches(treeTipNames, iplant);
            
        }
        
        for (TNRSMatch m : results) {
            System.out.println(m.toString());
        }

        System.out.println("\nNames that could not be matched:");
        for (String name : tnrs.getUnmatchedNames()) {
            System.out.println(name);
        }

    }

    public static void printHelp() {
        System.out.println("==========================");
        System.out.println("usage: taxomachine command options");
        System.out.println("");
        System.out.println("commands");
        System.out.println("---taxonomy---");
        System.out.println("\tinittax <filename> <sourcename> <graphdbfolder> (initializes the tax graph with a tax list)");
        System.out.println("\taddtax <filename> <sourcename> <graphdbfolder> (adds a tax list into the tax graph)");
        System.out.println("\tupdatetax <filename> <sourcename> <graphdbfolder> (updates a specific source taxonomy)");
        System.out.println("---taxquery---");
        System.out.println("\tcomptaxtree <name> <graphdbfolder> (construct a comprehensive tax newick)");
        System.out.println("\tcomptaxgraph <name> <graphdbfolder> <outdotfile> (construct a comprehensive taxonomy in dot)");
        System.out.println("\tfindcycles <name> <graphdbfolder> (find cycles in tax graph)");
        System.out.println("\tjsgraph <name> <graphdbfolder> (constructs a json file from tax graph)");
        System.out.println("\tchecktree <filename> <focalgroup> <graphdbfolder> (checks names in tree against tax graph)");
        System.out.println("---taxonomic name resolution services---");
        System.out.println("\ttnrsbasic <querynames> <graphdbfolder> (check if the taxonomy graph contains comma-delimited names)");
        System.out.println("\ttnrstree <treefile> <graphdbfolder> (check if the taxonomy graph contains names in treefile)");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure(System.getProperties());
        System.out.println("taxomachine version alpha.alpha.prealpha");

        if (args.length < 2) {
            printHelp();
            System.exit(1);

        } else if (args[0] == "help") {
            printHelp();
            System.exit(0);

        } else {
            System.out.println("things will happen here");
            MainRunner mr = new MainRunner();
            if (args.length < 2) {
                System.err.println("ERROR: not the right arguments");
                printHelp();
            }
            if (args[0].compareTo("inittax") == 0 || args[0].compareTo("addtax") == 0) {
                mr.taxonomyLoadParser(args);
            } else if (args[0].compareTo("comptaxtree") == 0 || args[0].compareTo("comptaxgraph") == 0 || args[0].compareTo("findcycles") == 0
                    || args[0].compareTo("jsgraph") == 0 || args[0].compareTo("checktree") == 0) {
                mr.taxonomyQueryParser(args);
            } else if (args[0].compareTo("tnrsbasic") == 0 || args[0].compareTo("tnrstree") == 0) {
                mr.parseTNRSRequest(args);
            } else {
                System.err.println("Unrecognized command \"" + args[0] + "\"");
                printHelp();
                System.exit(1);
            }
        }
    }
}