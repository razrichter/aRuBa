package org.jcvi.annotation;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcvi.annotation.facts.GenomeProperty;

public class RunGenomeProperties {

	private static final String sampleCommand = "genomeproperties [ -b <blast_file|dir> -h <hmm_file|dir> -g <genbank_file|dir> -r <rdf_file|dir> -n <n3_file|dir> -r <drools_file|directory> -D <database> -l <log_file> ]  ...";
	private static Boolean debug = false;

	public static void main(String[] args) {

		Options options = new Options();
		
		// Define options
		options.addOption("h","help",false, "Print this message");
		options.addOption("d", "database", true, "Small Genome Database id (optional)");
		options.addOption("g", "genbank", false, "Path to Genbank file or directory");
		options.addOption("h", "hmm", false, "Path to HMM file or directory");
		options.addOption("b", "blast", false, "Path to Blast file or directory");
		options.addOption("r", "rdf", false, "Path to RDF file or directory");
		options.addOption("debug",false,"Debug output");
		options.addOption("l","log", false, "Log file");
		OptionBuilder.withLongOpt("rule");
		OptionBuilder.hasArgs();
		OptionBuilder.withDescription("Path to a rule file to load.");
		
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
		
		// Create an instance of our Aruba engine
		Aruba aruba = new Aruba();

		// Log to file or console if requested
		String logFile = cmd.getOptionValue("log");
		if (logFile != null) {
			aruba.log(logFile);
		} else if (debug) {
			aruba.log();
		}

		// Add rules
		aruba.addDroolsFiles(cmd.getOptionValues("rule"));

		// Add Facts from Blast, HMM, Genbank or RDF files
		aruba.addBlastFiles(cmd.getOptionValues("blast"));
		aruba.addHmmFiles(cmd.getOptionValues("hmm"));
		aruba.addGenbankFiles(cmd.getOptionValues("genbank"));
		aruba.addRdfFiles(cmd.getOptionValues("rdf"));

		// Add Small Genome database
		String dbName = cmd.getOptionValue("database");
		if (dbName != null) {
			aruba.addSmallGenome(dbName);
		}
		
		// Two step process for running genome properties
		//a.addGenomeProperties();
		aruba.addGenomePropertiesFacts();
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/suffices.drl");
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl");
		aruba.run();
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/requiredby.drl");
		aruba.run();

		// Output the GenomeProperty detail report
		GenomeProperty.detailReport(System.out);
		
		// End the StatefulKnowledgeSession
		aruba.shutdown();
	}
}
