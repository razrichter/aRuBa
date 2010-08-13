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
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.BlastResultFileDAO;
import org.jcvi.annotation.dao.GenbankFeatureDAO;
import org.jcvi.annotation.dao.GenericFileDAO;
import org.jcvi.annotation.dao.GenomePropertiesDAOManager;
import org.jcvi.annotation.dao.HMMResultFileDAO;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.dao.SmallGenomeDAOManager;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.GenomeProperty;

public class Aruba {

	private static final String sampleCommand = "aruba [ -b <blast_file|dir> -h <hmm_file|dir> -g <genbank_file|dir> -r <rdf_file|dir> -n <n3_file|dir> -r <drools_file|directory> -D <database> -l <log_file> ]  ...";
	private static final String braingrabRules = "/org/jcvi/annotation/rules/braingrab/BraingrabChangeSet.xml";
	private static Boolean debug = false;
	private static String DEFAULT_OUTPUT = "gp";
	private static ArrayList<Feature> features = new ArrayList<Feature>();

	public static String getRulesChangeSet() {
		return braingrabRules;
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();

		// add database option
		options.addOption("h","help",false, "Print this message");
		options.addOption("d", "database", true, "Small Genome Database id (required)");
		options.addOption("b", "blast", false, "Path to Blast file or directory");
		options.addOption("h", "hmm", false, "Path to HMM file or directory");
		options.addOption("r", "rdf", false, "Path to RDF file or directory");
		options.addOption("g", "genbank", false, "Path to Genbank file or directory");
		options.addOption("bg", "braingrab",false,"Load BrainGrab rules");
		options.addOption("gp", "genomeproperties",false,"Load facts and rules for Genome Properties");
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

		//TODO: how to manage output type?
		String output = cmd.getOptionValue("output");
		if (output == null) {
			output = DEFAULT_OUTPUT;
		}

		try {
			// Add rules to KnowledgeBuilder
			final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			// Add user-provided rules
			if (cmd.hasOption("rule")) {
				for (String file : filesFromPaths(cmd.getOptionValues("rule"))) {
					System.err.println("Adding rule file " + file);
					kbuilder.add( ResourceFactory.newFileResource(file), ResourceType.DRL );
				}
			}
			
			// Add Genome Properties rules
			if (cmd.hasOption("genomeproperties")) {
				System.err.println("Adding Genome Properties rules");
				kbuilder.add( ResourceFactory.newClassPathResource( "/org/jcvi/annotation/rules/genomeproperties/GenomePropertiesChangeSet.xml", GenericFileDAO.class ),ResourceType.CHANGE_SET );
			}

			// Add BrainGrab rules
			if (cmd.hasOption("braingrab")) {
				System.err.println("Adding BrainGrab rules");
				kbuilder.add(ResourceFactory.newClassPathResource(braingrabRules), ResourceType.CHANGE_SET);
			}
			
			if (kbuilder.hasErrors()) {
				System.err.println(kbuilder.getErrors().toString());
				throw new RuntimeException("Unable to compile rules.");
			}
			
			// Create KnowledgeBase and StatefulKnowledgeSession
			final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
			final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			
			// Log to file or console if requested
			String logFile = cmd.getOptionValue("log");
			if (logFile != null) {
				System.err.println("Writing logs to file " + logFile);
				KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, logFile);
			} else if (debug) {
				KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			}

			// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
			if (cmd.hasOption("genomeproperties")) {
				System.err.println("Adding Genome Properties facts");
				GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager();
				GPManager.addGenomePropertiesFacts(ksession);
			}
			
			// Add Facts from Blast, HMM, Genbank or RDF files
			addBlastFiles(cmd.getOptionValues("blast"), ksession);
			addHmmFiles(cmd.getOptionValues("hmm"), ksession);
			addGenbankFiles(cmd.getOptionValues("genbank"), ksession);
			addRdfFiles(cmd.getOptionValues("rdf"), ksession);

			// Add Small Genome database
			if (dbName != null) {
				System.err.println("Adding facts from " + dbName + " database");
				SmallGenomeDAOManager SGManager = new SmallGenomeDAOManager(dbName);
				SGManager.addSmallGenomeFacts(ksession);
			}
			
			// FIRE RULES, CLOSE AND SHUTDOWN
			ksession.fireAllRules();
			ksession.dispose();
			
			// Show output
			printOutputReports(output);			

			GenomeProperty p = GenomeProperty.create("2029");
			System.out.println(p.toStringDetailReport());

		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}

	public static void printOutputReports(String formats) {
		for (String format :  formats.split(",")) {
			if (format.equals("annotations") || format.equals("annot")) {
				annotationsReport();
			} 
			else if (format.equals("rules")) {
				hitsReport();

			} 
			else if (format.equals("gp")) {
				GenomeProperty.detailReport(System.out);
			}
			else {
				System.err.println("Error: Unknown output format (" + format + "). Skipping.");
			}

		}
	}
	
	// Adding Facts
	public static void addDao(Iterable<? extends Object> iter, StatefulKnowledgeSession ksession) {
		for (Object o : iter) {
			ksession.insert(o);
		}
	}

	public static void addBlastFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new BlastResultFileDAO(file), ksession);
		}
	}
	public static void addHmmFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new HMMResultFileDAO(file), ksession);
		}
	}
	public static void addRdfFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new RdfFactDAO(file), ksession);
		}
	}    
	public void addN3Files(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new RdfFactDAO(file, "N3"), ksession);
		}
	} 
	public static void addGenbankFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new GenbankFeatureDAO(file), ksession);
		}
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

	private static void annotationsReport() {
		for (Feature f : features) {
			String annotation = getFeatureAnnotationText(f);
			if ( ! annotation.equals("") ) {
				System.out.print(annotation);
			}
		}		
	}
	private static void hitsReport() {
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
