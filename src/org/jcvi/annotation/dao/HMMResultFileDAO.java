package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.annotation.dao.HmmCutoffTableDAO;
import org.jcvi.annotation.dao.HmmCutoffTableDAO.HmmCutoff;
import org.jcvi.annotation.facts.HmmHit;

public class HMMResultFileDAO implements HmmHitDAO {

	private class HMMDomainInfo {
		private String accession;
		private int domainNum;
		private int seqStart;
		private int seqEnd;
		private boolean fromSeqStart;
		private boolean fromSeqEnd;
		private int hmmStart;
		private int hmmEnd;
		private boolean fromHmmStart;
		private boolean fromHmmEnd;
		private double domainScore;
		private double domainEValue;

		public HMMDomainInfo() {
			super();
		}

		public HMMDomainInfo(String accession, int domainNum, int seqStart,
				int seqEnd, boolean fromSeqStart, boolean fromSeqEnd,
				int hmmStart, int hmmEnd, boolean fromHmmStart,
				boolean fromHmmEnd, double domainScore, double domainEValue) {
			super();
			this.accession = accession;
			this.domainNum = domainNum;
			this.seqStart = seqStart;
			this.seqEnd = seqEnd;
			this.fromSeqStart = fromSeqStart;
			this.fromSeqEnd = fromSeqEnd;
			this.hmmStart = hmmStart;
			this.hmmEnd = hmmEnd;
			this.fromHmmStart = fromHmmStart;
			this.fromHmmEnd = fromHmmEnd;
			this.domainScore = domainScore;
			this.domainEValue = domainEValue;
		}

		public String getAccession() {
			return accession;
		}

		public void setAccession(String accession) {
			this.accession = accession;
		}

		public int getDomainNum() {
			return domainNum;
		}

		public void setDomainNum(int domainNum) {
			this.domainNum = domainNum;
		}

		public int getSeqStart() {
			return seqStart;
		}

		public void setSeqStart(int seqStart) {
			this.seqStart = seqStart;
		}

		public int getSeqEnd() {
			return seqEnd;
		}

		public void setSeqEnd(int seqEnd) {
			this.seqEnd = seqEnd;
		}

		public boolean isFromSeqStart() {
			return fromSeqStart;
		}

		public void setFromSeqStart(boolean fromSeqStart) {
			this.fromSeqStart = fromSeqStart;
		}

		public boolean isFromSeqEnd() {
			return fromSeqEnd;
		}

		public void setFromSeqEnd(boolean fromSeqEnd) {
			this.fromSeqEnd = fromSeqEnd;
		}

		public int getHmmStart() {
			return hmmStart;
		}

		public void setHmmStart(int hmmStart) {
			this.hmmStart = hmmStart;
		}

		public int getHmmEnd() {
			return hmmEnd;
		}

		public void setHmmEnd(int hmmEnd) {
			this.hmmEnd = hmmEnd;
		}

		public boolean isFromHmmStart() {
			return fromHmmStart;
		}

		public void setFromHmmStart(boolean fromHmmStart) {
			this.fromHmmStart = fromHmmStart;
		}

		public boolean isFromHmmEnd() {
			return fromHmmEnd;
		}

		public void setFromHmmEnd(boolean fromHmmEnd) {
			this.fromHmmEnd = fromHmmEnd;
		}

		public double getDomainScore() {
			return domainScore;
		}

		public void setDomainScore(double domainScore) {
			this.domainScore = domainScore;
		}

		public double getDomainEValue() {
			return domainEValue;
		}

		public void setDomainEValue(double domainEValue) {
			this.domainEValue = domainEValue;
		}

	}

	private class HMMHitInfo {
		private String accession;
		private String description;
		private double totalScore;
		private double totalEValue;
		private int numDomains;
		private ArrayList<HMMDomainInfo> domains;

		public HMMHitInfo() {
			super();
		}

		public HMMHitInfo(String accession, String description,
				double totalScore, double totalEValue, int numDomains) {
			super();
			this.accession = accession;
			this.description = description;
			this.totalScore = totalScore;
			this.totalEValue = totalEValue;
			this.numDomains = numDomains;
		}

		public String getAccession() {
			return accession;
		}

		public void setAccession(String accession) {
			this.accession = accession;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public double getTotalScore() {
			return totalScore;
		}

		public void setTotalScore(double totalScore) {
			this.totalScore = totalScore;
		}

		public double getTotalEValue() {
			return totalEValue;
		}

		public void setTotalEValue(double totalEValue) {
			this.totalEValue = totalEValue;
		}

		public int getNumDomains() {
			return numDomains;
		}

		public void setNumDomains(int numDomains) {
			this.numDomains = numDomains;
		}

		public ArrayList<HMMDomainInfo> getDomains() {
			return domains;
		}

		public void setDomains(ArrayList<HMMDomainInfo> domains) {
			this.domains = domains;
		}

		public void addDomain(HMMDomainInfo domain) {
			this.domains.add(domain);
		}
		
	}

	private ArrayList<HmmHit> hits = new ArrayList<HmmHit>();
	private HmmCutoffTableDAO cutoffTable;
	
	{
		try {
			cutoffTable = new HmmCutoffTableDAO();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HMMResultFileDAO() {
		super();
	}

	public HMMResultFileDAO(String hmmFile) {
		super();
		addHmmResultFile(hmmFile);
	}

	public void addHmmResultFile(String hmmFile) {
		try {
			BufferedReader hmmReader = new BufferedReader(new FileReader(
					hmmFile));
			parseHmmResultFile(hmmReader);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void parseHmmResultFile(BufferedReader hmmReader) throws IOException {

		final String numRe = "-?\\d+(?:\\.\\d+)?(?:[eE]-?\\d+)?";
		final Pattern programNameRE = Pattern.compile("^hmm(search|pfam)\\s+");
		final Pattern constantAccRE = Pattern.compile("^Query\\s+(HMM|[Ss]equence):\\s+(.*)$");
		// Accession description Score E-value num_Domains
		final Pattern hmmHitTableRE = Pattern.compile("^(\\S+)\\s+(.+)\\s+("
				+ numRe + ")\\s+(" + numRe + ")\\s+(\\d+)\\s*$");
		final Pattern hmmDomainTableRE = Pattern.compile(
				// Accession Domain/Total
				"^(\\S+)\\s+" + "(\\d+)/(\\d+)\\s+" +
				// Seq-f Seq-t fromStart toEnd
				"(\\d+)\\s" + "(\\d+)\\s+" + "([.\\[])([.\\]])\\s+" +
				// HMM-f HMM-t fromStart toEnd
				"(\\d+)\\s" + "(\\d+)\\s+" + "([.\\[])([.\\]])\\s+" +
				// Score E-value
				"(" + numRe + ")\\s+"+ "(" + numRe + ")\\s*$"				
		);

		String programName  = "";
		String constantAcc = null;
		HashMap<String,HMMHitInfo> hits = new HashMap<String,HMMHitInfo>();

		String line;
		while ((line = hmmReader.readLine()) != null) {
			Matcher nameMatcher = programNameRE.matcher(line);
			Matcher constantAccMatcher = constantAccRE.matcher(line);
			Matcher hitMatcher = hmmHitTableRE.matcher(line);
			Matcher domainMatcher = hmmDomainTableRE.matcher(line);
			if (nameMatcher.find()) { // new result
				if ( ! hits.isEmpty() ) { // add hits
					addParsedHits(programName, constantAcc, hits.values());
				}
				// reset entries
				programName = nameMatcher.group(0);
				constantAcc = null;
				hits = new HashMap<String,HMMHitInfo>();

			}
			else if (constantAccMatcher.matches()) {
				String program = constantAccMatcher.group(1);
				if (constantAcc == null) {
					constantAcc = constantAccMatcher.group(2);
				}
				else {
					throw new RuntimeException("attempt to override constant accession at "+line);
				}
			}
			else if (hitMatcher.matches()) {
				String acc = hitMatcher.group(1);
				String description = hitMatcher.group(2);
				double score = Double.valueOf(hitMatcher.group(3));
				double evalue = Double.valueOf(hitMatcher.group(4));
				int numDomains = Integer.valueOf(hitMatcher.group(5));
				hits.put(acc, new HMMHitInfo(acc, description, score, evalue, numDomains));
			}
			else if (domainMatcher.matches()) {
				String accession = domainMatcher.group(1);
				int domainNum = Integer.valueOf(domainMatcher.group(2));
				int totalDomains = Integer.valueOf(domainMatcher.group(3));
				int seqStart = Integer.valueOf(domainMatcher.group(4));
				int seqEnd = Integer.valueOf(domainMatcher.group(5));
				boolean fromSeqStart = domainMatcher.group(6)=="[";
				boolean fromSeqEnd = domainMatcher.group(7)=="]";
				int hmmStart = Integer.valueOf(domainMatcher.group(8));
				int hmmEnd = Integer.valueOf(domainMatcher.group(9));
				boolean fromHmmStart = domainMatcher.group(10)=="[";
				boolean fromHmmEnd = domainMatcher.group(11)=="]";
				double domainScore = Double.valueOf(domainMatcher.group(12));
				double domainEValue = Double.valueOf(domainMatcher.group(13));
								
				HMMDomainInfo domain = new HMMDomainInfo(accession, domainNum, seqStart, seqEnd, fromSeqStart, fromSeqEnd, hmmStart, hmmEnd, fromHmmStart, fromHmmEnd, domainScore, domainEValue);
				HMMHitInfo hit = hits.get(accession);
				if (hit != null && ( hit.getNumDomains() == totalDomains ) ) {
					hit.addDomain(domain);
				}
			}
		}
		if ( ! hits.isEmpty() ) { // save existing hits
			addParsedHits(programName, constantAcc, hits.values());
		}
	}

	private void addParsedHits(String programName, String constantAcc,
			Collection<HMMHitInfo> hits) {
		for (HMMHitInfo hitInfo : hits) {
			for (HMMDomainInfo domainInfo: hitInfo.getDomains()) {
				HmmHit hit = null;
				if ("search".equals(programName)) {
					hit = new HmmHit(
						hitInfo.getAccession(), 
						constantAcc
					);
					
				}
				else if ("pfam".equals(programName)) {
					hit = new HmmHit(
						constantAcc,
						hitInfo.getAccession()
					);
				}
				else {
					throw new RuntimeException("found hit/domain table before result");
					// something really bad happened here
				}
				hit.setScore(hitInfo.getTotalScore());
				hit.setDomainScore(domainInfo.getDomainScore());

				// get cutoff info and add
			    HmmCutoff cutoff = cutoffTable.get(hit.getHitId());
			    if (cutoff != null) {
				    if (cutoff.isAboveTrustedCutoff(hit.getScore(),
		                    hit.getDomainScore() )) {
		                hit.setStrongHit();
		                this.hits.add(hit);
		            }
		            else if (cutoff.isAboveNoiseCutoff(hit.getScore(),
		                    hit.getDomainScore() )) {
		                hit.setWeakHit();
		                this.hits.add(hit);
		            }
		            else {
		                hit.setNonHit();
		            }
			    }
			}
		}
	}

	@Override
	public Iterator<HmmHit> iterator() {
		return hits.iterator();
	}

	public List<HmmHit> getHits() {
		return hits;
	}

}
