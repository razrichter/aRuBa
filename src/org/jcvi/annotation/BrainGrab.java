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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.BlastResultFileDAO;
import org.jcvi.annotation.dao.FeatureDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.rulesengine.RulesEngine;

public class BrainGrab {

    private static final String sampleCommand = "braingrab -D <database> blastfile.out|directory ...";
    private static final String rulesFile = "/org/jcvi/annotation/rules/BrainGrabChangeSet.xml";
    private static final ResourceType rulesType = ResourceType.ChangeSet;
    private static Boolean debug = false;
    
    private RulesEngine engine;
    
    

    public BrainGrab() {
        super();
        this.engine = new RulesEngine();
        // Add Rules Resources
        engine.addResource(this.getClass().getResource(rulesFile).toString(), rulesType);
    }
    
    private void addFeatures(Iterable<Feature> features) {
        engine.addFacts(features);
    }
    
    private void addSearchFiles(String[] filesOrDirs) {
        ArrayList<String> files = new ArrayList<String>();
        
        for (String blastName : filesOrDirs) {
            File stat = new File(blastName);
            if ( ! stat.exists() ) {
                System.err.println("Error: File '"+ blastName + "' does not exist. Skipping.");
            }
            else if (! stat.canRead() ) {
                System.err.println("Error: File '" + blastName + "' cannot be read. Skipping.");
            }
            else if (stat.isFile() && stat.length() > 0) {
                files.add(blastName);
            }
            else if (stat.isDirectory()) {
                for (File f : stat.listFiles()) {
                    files.add(f.getPath());
                }
            }
        }
        for (String file : files) {
            BlastResultFileDAO blast = new BlastResultFileDAO(file);
            engine.addFacts(blast);
        }
    }
    
    private void run() {
        engine.fireAllRules();
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
        ArrayList<Feature> features = new ArrayList<Feature>();
        
        // add database option
        options.addOption("h","help",false, "Print this message");
        options.addOption("D", "database", true, "Small Genome Database id (required)");
        options.addOption("debug",false,"Debug output");
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
        
        // String[] blastArgs = cmd.getOptionValues("blast");
        
        
        // Add Genome Features
        SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(dbName);
        FeatureDAO featureLoader = sgFactory.getFeatureDAO();
        // for (Feature feat : featureLoader.getFeatures() ) {
        //     engine.addFact(feat);
        // }
        for (Feature f : featureLoader) {
            features.add(f);
        }
        
        BrainGrab bg = new BrainGrab();
        bg.addFeatures(features);
        bg.addSearchFiles(cmd.getArgs());
        bg.run();
        
        for (Feature f : features) {
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
                        a.getEcNumber(), 
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
