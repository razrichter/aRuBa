package org.jcvi.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
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
import org.jcvi.annotation.dao.Hmmer2ResultFileDAO;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.dao.SmallGenomeDAOManager;
import org.jcvi.annotation.writer.factory.GenomePropertyWriterFactory;
import org.jcvi.annotation.writer.factory.GenomePropertyWriterFactory.GenomePropertiesFormat;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyWriter;

public class RunGP {

	private static final String sampleCommand = "genomeproperties -d <database> [ -f <rdf|xml|n3|dag|text> -r <rule drl file> -g <GenBank file>  -l <log file> --debug ]";
	private static GenomePropertiesFormat format = GenomePropertiesFormat.TEXT;
	
	public static void main(String[] args) {

		// Configure option parameters
		Options options = new Options();
		options.addOption("h","help",false, "Print this message");
		options.addOption("d", "database", true, "Small Genome Database id (required)");
		options.addOption("f", "format", true, "Output format (rdf|xml|n3|dag|text|detailed)");
		options.addOption("r", "rule", true, "Path to rule file or directory");
		options.addOption("b", "blast", true, "Path to Blast file or directory");
		options.addOption("hmm", true, "Path to HMM file or directory");
		options.addOption("rdf", true, "Path to RDF/XML file or directory");
		options.addOption("g", "genbank", true, "Path to Genbank file or directory");
		options.addOption("l","log", true, "Log file");
		options.addOption("debug", false, "Debug output");

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
		
		// Set genome properties output format
		if (cmd.hasOption("format")) {
			GenomePropertiesFormat requestFormat = GenomePropertiesFormat.getFormat(cmd.getOptionValue("format"));
			if (requestFormat != null) {
				format = requestFormat;
			}
		}

		try {
			// -----------------------------------------------------------------------------------
			// ADD RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
			// -----------------------------------------------------------------------------------
			final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource("/org/jcvi/annotation/rules/genomeproperties/GenomePropertiesChangeSet.xml", GenericFileDAO.class), ResourceType.CHANGE_SET);
			if (kbuilder.hasErrors()) {
				System.err.println(kbuilder.getErrors().toString());
				throw new RuntimeException("Unable to compile rules.");
			}
			final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

			// Log to file or stdout, if requested
			if (cmd.hasOption("log")) {
				KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, cmd.getOptionValue("log"));
			} else if (cmd.hasOption("debug")) {
				KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			}
			
			// -----------------------------------------------------------------------------------
			// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
			// -----------------------------------------------------------------------------------	
			
			// Add Facts from Database, Blast, HMM, Genbank or RDF files
			addSmallGenomeDatabase(cmd.getOptionValue("database"), ksession);
			addBlastFiles(cmd.getOptionValues("blast"), ksession);
			addHmmFiles(cmd.getOptionValues("hmm"), ksession);
			addGenbankFiles(cmd.getOptionValues("genbank"), ksession);
			addRdfFiles(cmd.getOptionValues("rdf"), ksession);
			addN3Files(cmd.getOptionValues("n3"), ksession);

			// Add Genome Properties
			GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager();
			GPManager.addGenomePropertiesFacts(ksession);

			// fire rules 
			ksession.fireAllRules();
			ksession.dispose();
			
			// Write report
			GenomePropertyWriter writer = GenomePropertyWriterFactory.getWriter(format);
			System.out.println(writer.write());


		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	// Adding Facts
	public static void addDao(Iterable<? extends Object> iter, StatefulKnowledgeSession ksession) {
		for (Object o : iter) {
			ksession.insert(o);
		}
	}
	private static void addSmallGenomeDatabase(String database, StatefulKnowledgeSession ksession) {
		if (database != null) {
			SmallGenomeDAOManager SGManager = new SmallGenomeDAOManager(database);
			SGManager.addSmallGenomeFacts(ksession);
		}
		
	}
	
	public static void addBlastFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new BlastResultFileDAO(file), ksession);
		}
	}
	public static void addHmmFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new Hmmer2ResultFileDAO(file), ksession);
		}
	}
	public static void addRdfFiles(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
		for (String file : filesFromPaths(filesOrDirs)) {
			addDao(new RdfFactDAO(file), ksession);
		}
	}    
	public static void addN3Files(String[] filesOrDirs, StatefulKnowledgeSession ksession) {
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

}