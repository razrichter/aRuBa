package org.jcvi.annotation;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
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
import org.jcvi.annotation.dao.HMMResultFileDAO;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.EngineReady;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.rulesengine.RulesEngine;

public class Aruba {

	private static final String sampleCommand = "aruba [ -b <blast_file|dir> -h <hmm_file|dir> -g <genbank_file|dir> -r <rdf_file|dir> -n <n3_file|dir> -r <drools_file|directory> -D <database> -l <log_file> ]  ...";
	private static final String braingrabRules = "/org/jcvi/annotation/rules/braingrab/BraingrabChangeSet.xml";
	private static final String genomePropertiesRules = "/org/jcvi/annotation/rules/genomeproperties/GenomePropertiesChangeSet.xml";
	private static Boolean debug = false;
	private String DEFAULT_OUTPUT = "annotations";
	private ArrayList<Feature> features = new ArrayList<Feature>();
	private RulesEngine engine;

	public Aruba() {
		super();
		this.engine = new RulesEngine();
	}

	public RulesEngine getEngine() {
		return engine;
	}
	public static String getRulesChangeSet() {
		return braingrabRules;
	}

	/*
	private void testLog() {
		WorkingMemoryEventListener listener = new WorkingMemoryEventListener() {
			public void objectInserted(ObjectInsertedEvent event) {
				System.out.println("OBJECT INSERTED " + event.getObject().toString());
			}
		};
		engine.getKsession().addEventListener(listener);
	}
	 */

	public static void main(String[] args) throws Exception {
		Options options = new Options();

		// add database option
		options.addOption("h","help",false, "Print this message");
		options.addOption("D", "database", true, "Small Genome Database id (required)");
		options.addOption("b", "blast", false, "Path to Blast file or directory");
		options.addOption("h", "hmm", false, "Path to HMM file or directory");
		options.addOption("r", "rdf", false, "Path to RDF file or directory");
		options.addOption("g", "genbank", false, "Path to Genbank file or directory");
		options.addOption("braingrab",false,"Load BrainGrab rules");
		options.addOption("genomeproperties",false,"Load facts and rules for Genome Properties");
		options.addOption("debug",false,"Debug output");
		options.addOption("l","log", false, "Log file");
		options.addOption("o", "output", false, "Output formats (annotations, rules)");
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

		// Create an instance of our Aruba engine
		Aruba aruba = new Aruba();

		String output = cmd.getOptionValue("output");
		if (output == null) {
			output = aruba.DEFAULT_OUTPUT;
		}

		// Log to file or console if requested
		String logFile = cmd.getOptionValue("log");
		if (logFile != null) {
			aruba.log(logFile);
		} else if (debug) {
			aruba.log();
		}

		// Add rules
		aruba.addDroolsFiles(cmd.getOptionValues("rule"));

		// Add facts and rules for Genome Properties
		if (cmd.hasOption("genomeproperties")) {
			aruba.addGenomeProperties();
		}

		// Add default braingrab rules
		if (cmd.hasOption("braingrab")) {
			aruba.addDefaultRules();
		}

		// Add Facts from Blast, HMM, Genbank or RDF files
		aruba.addBlastFiles(cmd.getOptionValues("blast"));
		aruba.addHmmFiles(cmd.getOptionValues("hmm"));
		aruba.addGenbankFiles(cmd.getOptionValues("genbank"));
		aruba.addRdfFiles(cmd.getOptionValues("rdf"));

		// Add Small Genome database
		aruba.addSmallGenome(dbName);

		// Fire rules
		aruba.run();

		// show outputs
		aruba.printOutputReports(output);

		// End the StatefulKnowledgeSession
		aruba.shutdown();

	}

	private void printOutputReports(String outputChars) {
		for (String output :  outputChars.split(",")) {
			output.trim();
			if (output == "annotations" || output == "annot") {
				annotationsReport();
			} else if (output == "rules") {
				hitsReport();
				//} else if (output == "gff") {
				//	gffReport();

			} else {
				// should throw an error here
				System.err.println("Error: Unknown output format (" + output + "). Skipping.");
			}

		}

	}

	// Adding Rules
	public boolean addDefaultRules() {
		return engine.addResource(this.getClass().getResource(braingrabRules), 
				ResourceType.CHANGE_SET);
	}
	public boolean addGenomePropertiesRules() {
		return engine.addResource(this.getClass().getResource(genomePropertiesRules), 
				ResourceType.CHANGE_SET);
	}
	public boolean addRule(String file, ResourceType type) {
		return this.engine.addResource(file, type);
	}
	public boolean addDrools(String file) {
		return engine.addResource(file, ResourceType.DRL);
	}
	public boolean addDroolsExpression(String ruleStr) {
		Resource res = ResourceFactory.newReaderResource(new StringReader(ruleStr));
		return engine.addResource(res, ResourceType.DRL);
	}

	// Adding Facts
	private int addBlast(String file) {
		return engine.addFacts(new BlastResultFileDAO(file));
	}

	private int addHmm(String file) {
		return engine.addFacts(new HMMResultFileDAO(file));
	}

	private int addGenomeProperties() {
		
		// Add rules associated with Genome Properties
		System.out.println("Adding Genome Properties rules...");
		addGenomePropertiesRules();
		System.out.println("complete.");

		System.out.println("Adding Genome Properties facts...");
		// Add facts about Genome Properties from Notation3 file
		URL n3Url = this.getClass().getResource("dao/data/genomeproperties.n3");
		RdfFactDAO dao = new RdfFactDAO(n3Url, "N3");
		int numFacts = addRdf(dao);
		System.out.println("complete.");
		
		System.out.println("Adding our ReadyEngine fact instance.");
		engine.addFact(new EngineReady());
		System.out.println("complete.");
		
		return numFacts;
}

	private int addGenomePropertiesFactsFirst() {
		
		System.out.println("Adding Genome Properties facts before rules...");
		// Add facts about Genome Properties from Notation3 file
		URL n3Url = this.getClass().getResource("dao/data/genomeproperties.n3");
		RdfFactDAO dao = new RdfFactDAO(n3Url, "N3");
		int numFacts = addRdf(dao);

		// Add rules associated with Genome Properties
		System.out.println("Adding Genome Properties rules...");
		addGenomePropertiesRules();
		System.out.println("complete.");
		
		return numFacts;
}
	private int addRdf(RdfFactDAO dao) {
		engine.addFacts(dao);
		return dao.getTotalFacts();
	}
	private int addRdf(String file) {
		return addRdf(new RdfFactDAO(file));
	}
	private int addN3(String file) {
		return addRdf(new RdfFactDAO(file, "N3"));
	}

	private int addGenbank(String file) {
		InputStreamReader gbReader = new InputStreamReader(this.getClass().getResourceAsStream(file));       
		return engine.addFacts(new GenbankFeatureDAO(gbReader));
	}

	private void addDroolsFiles(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addDrools(file);
		}
	}
	private void addBlastFiles(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addBlast(file);
		}
	}
	private void addHmmFiles(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addHmm(file);
		}
	}
	private void addRdfFiles(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addRdf(file);
		}
	}    
	private void addN3Files(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addN3(file);
		}
	} 
	private void addGenbankFiles(String[] filesOrDirs) {
		for (String file : filesFromPaths(filesOrDirs)) {
			this.addGenbank(file);
		}
	}  

	private int addSmallGenome(String dbName) {

		if (dbName == null) {
			return 0;
		}
		
		System.out.println("Loading facts from Small Genome database " + dbName + "...");
		SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(dbName);

		int total = 0;

		// Add Genome Features
		System.out.println("Adding features...");
		for (Feature f : sgFactory.getFeatureDAO()) {
			features.add(f); // save features for later reporting
		}
		int count = total = engine.addFacts(features);
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

	private void log() {
		engine.setConsoleLogger();
	}
	private void log(String logFile) {
		engine.setFileLogger(logFile);
	}

	private void run() {
		engine.fireAllRules();
	}
	private void shutdown() {
		engine.shutdown();
	}
	public static List<String> filesFromPaths(String[] filesOrDirs) {

		ArrayList<String> files = new ArrayList<String>();
		if (filesOrDirs != null) {
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

	private void annotationsReport() {
		for (Feature f : features) {
			String annotation = getFeatureAnnotationText(f);
			if ( ! annotation.equals("") ) {
				System.out.print(annotation);
			}
		}		
	}
	private void hitsReport() {
		for (Feature f : features) {
			List<Annotation> assertedAnnotations = f.getAssertedAnnotations();
			for (Annotation a : assertedAnnotations) {
				System.out.println(a.getSource() + " " + f.getName());
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
