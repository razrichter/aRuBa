package org.jcvi.annotation;

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
import org.jcvi.annotation.dao.GenericFileDAO;
import org.jcvi.annotation.dao.GenomePropertiesDAOManager;
import org.jcvi.annotation.dao.SmallGenomeDAOManager;
import org.jcvi.annotation.writer.factory.GenomePropertyWriterFactory;
import org.jcvi.annotation.writer.factory.GenomePropertyWriterFactory.GenomePropertiesFormat;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyWriter;

public class RunGP {

	private static final String sampleCommand = "genomeproperties -d <database> [ -f <rdf|xml|n3|dag|text> -r <rule drl file> -g <GenBank file>  -l <log file> --debug ]";
	private static GenomePropertiesFormat format = GenomePropertiesFormat.DAG;
	
	public static void main(String[] args) {

		// Configure option parameters
		Options options = new Options();
		options.addOption("h","help",false, "Print this message");
		options.addOption("d", "database", true, "Small Genome Database id (required)");
		options.addOption("f", "format", true, "Output format (rdf|xml|n3|dag|text)");
		options.addOption("r", "rule", true, "Path to rule");
		options.addOption("g", "genbank", true, "Path to Genbank file or directory");
		options.addOption("l","log", true, "Log file");
		options.addOption("debug",false,"Debug output");

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
			
			// Add Small Genome database, if requested
			if (cmd.hasOption("database")) {
				SmallGenomeDAOManager SGManager = new SmallGenomeDAOManager(cmd.getOptionValue("database"));
				SGManager.addSmallGenomeFacts(ksession);
			} else 
			{
				System.err.println("A small genome database is required.");
				System.exit(0);
			}
			
			// Add Genome Properties
			GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager();
			GPManager.addGenomePropertiesFacts(ksession);

			// -----------------------------------------------------------------------------------
			// FIRE RULES, DISPOSE AND WRITE GENOME PROPERTIES REPORT
			// -----------------------------------------------------------------------------------
			ksession.fireAllRules();
			ksession.dispose();
			
			GenomePropertyWriter writer = GenomePropertyWriterFactory.getWriter(format);
			System.out.println(writer.write());


		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}