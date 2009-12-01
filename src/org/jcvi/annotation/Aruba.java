package org.jcvi.annotation;
import java.io.File;
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
import org.jcvi.annotation.dao.BlastResultFileDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.rulesengine.RulesEngine;

public class Aruba {

    private static final String sampleCommand = "braingrab -D <database> [-r <rule_file|directory>] blastfile.out|directory ...";
    private static final String rulesFile = "/org/jcvi/annotation/rules/BrainGrabChangeSet.xml";
    private static final ResourceType rulesType = ResourceType.CHANGE_SET;
    private static Boolean debug = false;
    private ArrayList<Feature> features = new ArrayList<Feature>();
    
    private RulesEngine engine;
    
    public Aruba() {
        super();
        this.engine = new RulesEngine();
        // Add Rules Resources
        engine.addResource(this.getClass().getResource(rulesFile).toString(), rulesType);
    }

    public Aruba(List<String> ruleFiles) {
    	super();
    	this.engine = new RulesEngine();
    	for (String file : ruleFiles) {
    		engine.addResource(file, ResourceType.DRL);
    	}
    }
    
    private void addSmallGenome(String dbName) {
    	System.out.println("Loading facts from Small Genome database " + dbName + "...");
    	SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(dbName);
    	
        // Add Genome Features
        int count = engine.addFacts(sgFactory.getFeatureDAO());
    	System.out.println("  " + count + " features");
    	        
    	// Add annotations
    	count = engine.addFacts(sgFactory.getAnnotationDAO());
    	System.out.println("  " + count + " annotations");
    	
    	// Add Genome HMMs
    	count = engine.addFacts(sgFactory.getHmmHitDAO());
        System.out.println("  " + count + " HMM hits");
    	
        // Add Genome Properties
        count = engine.addFacts(sgFactory.getGenomePropertyDAO("gb6"));
        System.out.println("  " + count + " genome properties");
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
        
        String[] ruleArgs = cmd.getOptionValues("rule");
        Aruba rb;
        
        // Give a rules file
        if (ruleArgs.length > 0) {
        	List<String> ruleFiles = filesFromPaths(ruleArgs);
        	for (String f : ruleFiles) {
        		System.out.println("rule " + f.toString());
        	}
        	rb = new Aruba(ruleFiles);
        }
        else {
            rb = new Aruba();
        }

        // String[] blastArgs = cmd.getOptionValues("blast");

        rb.addSmallGenome(dbName);
        rb.addSearchFiles(cmd.getArgs());

        // Fire rules
        rb.run();
        
        for (Feature f : rb.features) {
            String annotation = getFeatureAnnotationText(f);
            if ( ! annotation.equals("") ) {
                System.out.print(annotation);
            }
        }
        
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
