package org.jcvi.annotation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcvi.annotation.dao.HmmCutoffTableDAO;
import org.jcvi.annotation.dao.HmmCutoffTableDAO.HmmCutoff;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.HmmHit;

public class RunGenomeProperties {

	private static final String sampleCommand = "genomeproperties [ -b <blast_file|dir> -h <hmm_file|dir> -g <genbank_file|dir> -r <rdf_file|dir> -n <n3_file|dir> -r <drools_file|directory> -d <database> -l <log_file> ]  ...";
	private static Boolean debug = false;
	private static Boolean showDetailReport = false;
	
	public static void main(String[] args) {

		Options options = new Options();
		
		// Define options
		options.addOption("h","help",false, "Print this message");
		options.addOption("d", "database", true, "Small Genome Database id (optional)");
		options.addOption("g", "genbank", false, "Path to Genbank file or directory");
		options.addOption("h", "hmm", false, "Path to HMM file or directory");
		options.addOption("b", "blast", false, "Path to Blast file or directory");
		options.addOption("r", "rdf", false, "Path to RDF file or directory");
		options.addOption("l", "log", false, "Path to log file");
		options.addOption("debug",false,"Debug output");
		options.addOption("details",false,"Show detailed Genome Properties report");
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
		if (cmd.hasOption("details")) {
			showDetailReport = true;
		}
		
		// Create an instance of our Aruba engine
		Aruba aruba = new Aruba();
		aruba.addGenomeProperties();
		
		// Log to file or console if requested
		String logFile = cmd.getOptionValue("log");
		if (logFile != null) {
			aruba.log(logFile);
		} else if (debug) {
			System.err.println("Setting to debug mode.");
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
		
		// Fire all rules
		aruba.run();

		// Output the GenomeProperty detail report
		if (showDetailReport) {
			GenomeProperty.detailReport(System.out);
		}
		else
		{
			GenomeProperty.report(System.out);
		}
		aruba.shutdown();
	}
}
