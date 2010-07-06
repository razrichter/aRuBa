package org.jcvi.annotation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

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
		aruba.addGenomePropertiesFacts();
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/suffices.drl");
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl");
		
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
		
		// Two step process for running genome properties
		aruba.run();
		aruba.addDrools("/org/jcvi/annotation/rules/genomeproperties/requiredby.drl");
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
	
	public static void loadSimpleHmmsByFile(Aruba aruba) throws IOException {
		HmmCutoffTableDAO cutoffTable = new HmmCutoffTableDAO();
		
		String file = "C:/Documents and Settings/naxelrod/Desktop/hmms.txt";
		FileInputStream hmmStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(hmmStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] field = line.split("\\|");
			String queryId = field[0]; // a.feat_name
			String hitId = field[1];
			HmmHit hit = new HmmHit(queryId, hitId);
			hit.setStrongHit();
			aruba.getEngine().addFact(hit);
		}
		// Add another record
		aruba.getEngine().addFact(new HmmHit("BOGUS666", "BOGUS666"));
	}
	
	public static void loadHmmsByFile(Aruba aruba) throws IOException {
		HmmCutoffTableDAO cutoffTable = new HmmCutoffTableDAO();
		
		String file = "C:/Documents and Settings/naxelrod/Desktop/hmms.txt";
		FileInputStream hmmStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(hmmStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] field = line.split("\\|");
			String queryId = field[0]; // a.feat_name
			String hitId = field[1]; // evidence.accession
			int queryStart = Integer.parseInt(field[2]);
			int queryEnd = Integer.parseInt(field[3]);
			int hitStart = Integer.parseInt(field[4]);
			int hitEnd = Integer.parseInt(field[5]);
			double score = Double.parseDouble(field[6]);
			double domainScore = Double.parseDouble(field[7]);
			
			// convert from min, max to start, end, strand syntax
			int queryStrand = 1;
			if (queryStart > queryEnd) {
				int tmpEnd = queryStart;
				queryEnd = queryStart;
				queryStart = tmpEnd;
				queryStrand = -1;
			}
			int hitStrand = 1;
			if (hitStart > hitEnd) {
				int tmpEnd = hitStart;
				hitEnd = hitStart;
				hitStart = tmpEnd;
				hitStrand = -1;
			}
			
			HmmHit hit = new HmmHit(queryId, queryStart, queryEnd, queryStrand,
						hitId, hitStart, hitEnd, hitStrand);
			hit.setScore(score);
			hit.setDomainScore(domainScore);

			if (cutoffTable != null) {
			    HmmCutoff cutoff = cutoffTable.get(hitId);
			    if (cutoff != null) {
				    if (cutoff.isAboveTrustedCutoff(score,
                            domainScore)) {
                        hit.setStrongHit();
                    }
                    else if (cutoff.isAboveNoiseCutoff(score,
                            domainScore)) {
                        hit.setWeakHit();
                    }
                    else {
                        hit.setNonHit();
                    }
			    }
			}
			aruba.getEngine().addFact(hit);
		}
		
		
		
	}
}
