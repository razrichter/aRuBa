package org.jcvi.annotation;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.jcvi.annotation.dao.BlastResultFileDAO;
import org.jcvi.annotation.dao.GenbankFeatureDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.rulesengine.RulesEngine;

public class Aruba {

    private static final String sampleCommand = "aruba -D <database> [-r <rule_file|directory>] [-l <log_file>] blastfile.out|directory ...";
	private static final String rulesChangeSet = "/org/jcvi/annotation/rules/BraingrabChangeSet.xml";
    private static Boolean debug = false;
    private ArrayList<Feature> features = new ArrayList<Feature>();
    private RulesEngine engine;
    
    public Aruba() {
        super();
        this.engine = new RulesEngine();
        //this.addDefaultRules();
  }
	public Aruba(String rulesFile) {
		this();
		engine.addResource(rulesFile, ResourceType.DRL);
	}
    public Aruba(List<String> ruleFiles) {
    	this();
    	for (String file : ruleFiles) {
    		engine.addResource(file, ResourceType.DRL);
    	}
    }
  
    public RulesEngine getEngine() {
		return engine;
	}
	public static String getRulesChangeSet() {
		return rulesChangeSet;
	}

	// Adding Rules
	public boolean addDefaultRules() {
		return engine.addResource(this.getClass().getResource(rulesChangeSet), 
				ResourceType.CHANGE_SET);
 	}
	public boolean addRuleByFile(String file, ResourceType type) {
    	return this.engine.addResource(file, type);
	}
	public boolean addRuleByFile(String file) {
    	return engine.addResource(file, ResourceType.DRL);
	}
	public boolean addRuleByString(String ruleStr) {
		Resource res = ResourceFactory.newReaderResource(new StringReader(ruleStr));
		return engine.addResource(res, ResourceType.DRL);
	}
	
	
	// Adding Facts
	private int addBlast(String fileOrDir) {
		return engine.addFacts(new BlastResultFileDAO(fileOrDir));
	}

	/* TODO: Alex, HmmResultFileDAO needs to be written
	private int addHmm(String fileOrDir) {
		return engine.addFacts(new HmmResultFileDAO(fileOrDir));
	}
	*/
	
	private int addGenbank(String file) {
		InputStreamReader gbReader = new InputStreamReader(this.getClass().getResourceAsStream("CP000855.gb"));       
		return engine.addFacts(new GenbankFeatureDAO(gbReader));
	}
	private int addSmallGenome(String dbName) {
		
    	System.out.println("Loading facts from Small Genome database " + dbName + "...");
    	SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(dbName);
    	
    	int total = 0;
    	
        // Add Genome Features
        int count = total = engine.addFacts(sgFactory.getFeatureDAO());
    	System.out.println("  " + count + " features");
    	        
    	// Add annotations
    	total += count = engine.addFacts(sgFactory.getAnnotationDAO());
    	System.out.println("  " + count + " annotations");
    	
    	// Add Genome HMMs
    	total += count = engine.addFacts(sgFactory.getHmmHitDAO());
        System.out.println("  " + count + " HMM hits");
    	
        // Add Genome Properties
        total += count = engine.addFacts(sgFactory.getGenomePropertyDAO(dbName));
        System.out.println("  " + count + " genome properties");
        
        return total;
    }
    
    
    private void addSearchFiles(String[] filesOrDirs) {
        List<String> files = filesFromPaths(filesOrDirs);
        for (String file : files) {
            BlastResultFileDAO blast = new BlastResultFileDAO(file);
            engine.addFacts(blast);
        }
    }
    
    private void run() {
        engine.fireAllRules();
    }
    private void shutdown() {
    	engine.shutdown();
    }
    public static List<String> filesFromPaths(String[] filesOrDirs) {
    	
    	ArrayList<String> files = new ArrayList<String>();
    	for (String fileOrDir : filesOrDirs) {
    		File stat = new File(fileOrDir);
    		if ( ! stat.exists() ) {
    			System.err.println("Error: File '"+ fileOrDir + "' does not exist. Skipping.");
    		}
    		else if ( ! stat.canRead() ) {
    			System.err.println("Error: File '" + fileOrDir + "' cannot be read. Skipping.");
    		}
    		else if (stat.isFile() && stat.length() > 0) {
    			files.add(fileOrDir);
    		}
    		else if (stat.isDirectory()) {
    			for (File f: stat.listFiles()) {
    				files.add(f.getPath());
    			}
    		}
    	}
    	return files;
    }
    
    private static <T> String join(Collection<T> l, String delim) {
        StringBuilder b = new StringBuilder();
        Iterator<T> i = l.iterator();
        while(i.hasNext()) {
            T s = i.next();
            if (s != null) {
                b.append(s);
            }
            if(i.hasNext()) {
                b.append(delim);
            }
        }
        return b.toString();
    }
    
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        
        // add database option
        options.addOption("h","help",false, "Print this message");
        options.addOption("D", "database", true, "Small Genome Database id (required)");
        options.addOption("debug",false,"Debug output");
        options.addOption("l","log", false, "Log file");
        OptionBuilder.withLongOpt("rule");
		OptionBuilder.hasArgs();
		OptionBuilder.withDescription("override rules to run");
		options.addOption(OptionBuilder.create("r"));
        // options.addOption(OptionBuilder.withLongOpt("blast").hasArgs().withDescription("add Blast Result File or Directory").create('b'));  
		
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options,args);
        }
        catch (ParseException e) {
            e.printStackTrace();
            HelpFormatter f = new HelpFormatter();
            f.printHelp(sampleCommand, options);
            System.exit(1);
        }
        
        if (cmd.hasOption("help")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp(sampleCommand, options);
            System.exit(0);
        }
        
        if (cmd.hasOption("debug")) {
            debug = true;
        }
        String dbName = cmd.getOptionValue("database");
        if (dbName == null) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp(sampleCommand, options);
            System.exit(1);
        }
        
        // Create an instance of our Aruba engine
        Aruba aruba = new Aruba();

        // Log to file or console if requested
        String logFile = cmd.getOptionValue("log");
        if (logFile != null) {
        	aruba.engine.setFileLogger(logFile);
        } else if (debug) {
        	aruba.engine.setConsoleLogger();
        }
       
        // Add rules
       String[] ruleArgs = cmd.getOptionValues("rule");
       if (ruleArgs == null || ruleArgs.length ==0) {
        	aruba.addDefaultRules();
        } else {
        	List<String> ruleFiles = filesFromPaths(ruleArgs);
        	for (String f : ruleFiles) {
           		System.out.println("Adding rule file... " + f.toString());
           		aruba.addRuleByFile(f);
         	}
        }

        // Add Small Genome database
        aruba.addSmallGenome(dbName);
        
        // Add facts from search results (Blast, HMM)
        aruba.addSearchFiles(cmd.getArgs());

        // Fire rules
        aruba.run();
        
        for (Feature f : aruba.features) {
            String annotation = getFeatureAnnotationText(f);
            if ( ! annotation.equals("") ) {
            	System.out.print(annotation);
            }
        }
        
        // End the StatefulKnowledgeSession
        aruba.shutdown();
        
    }

    private static String getFeatureAnnotationText(Feature f) {
        StringBuilder output = new StringBuilder();
        List<Annotation> assertedAnnotations = f.getAssertedAnnotations();
        if (assertedAnnotations.size() > 0 || debug) {
            output.append(f.getName());
            output.append(":");
            output.append("\n");
            for (Annotation a : assertedAnnotations) {
                output.append("\tAnnotation (");
                output.append(a.getAssertionType());
                output.append(") Confidence: ");
                output.append(a.getConfidence());
                output.append(" Specificity: ");
                output.append(a.getSpecificity());
                output.append("\n");

                // generate annotation string
                ArrayList<String> annotations = new ArrayList<String>();
                annotations.addAll(Arrays.asList(
                        a.getCommonName(), a.getGeneSymbol(),
                        "EC Numbers (" + join(a.getEcNumbers(), ",") + ")",
                        "GO IDs (" + join(a.getGoIds(), ",") + ")",
                        "Role IDs (" + join(a.getRoleIds(),",") + ")"
                    ));
                output.append("\t\t");
                output.append(join(annotations,"; "));
                output.append("\n");
            }

        }
        return output.toString();
    }

}
