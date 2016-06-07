package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.annotation.facts.HmmHit;

public class Hmmer3ResultTbloutFileDAO implements HmmHitDAO {

	

	private ArrayList<HmmHit> hits = new ArrayList<HmmHit>();
	private HmmCutoffTableDAO cutoffTable;
	
	{
		try {
			cutoffTable = HmmCutoffTableDAO.getHmmer3CutoffDAO();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Hmmer3ResultTbloutFileDAO() {
		super();
	}

	public Hmmer3ResultTbloutFileDAO(String hmmFile) {
		super();
		addHmmResultFile(hmmFile);
	}

	public Hmmer3ResultTbloutFileDAO(BufferedReader hmmReader) {
		super();
		try {
			parseHmmResultFile(hmmReader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addHmmResultFile(String hmmFile) {
		try {
			BufferedReader hmmReader = new BufferedReader(new FileReader(
					hmmFile));
			parseHmmResultFile(hmmReader);
			hmmReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void parseHmmResultFile(BufferedReader hmmReader) throws IOException {
		String line;
		while ((line = hmmReader.readLine()) != null) {
			HmmHit hit = new HmmHit();
			if (! line.startsWith("#")) {
				String[] lineColumns = line.split("\\s+",11); // don't care about domain statistics
				if (! lineColumns[3].equals("")) { // if it has HMM accession in col 4, it's hmmsearch output
					hit.setQueryId(lineColumns[0]);
					hit.setHitId(lineColumns[2]);
				}
				else {
					hit.setQueryId(lineColumns[2]);
					hit.setHitId(lineColumns[0]);
				}
				hit.setScore(Double.parseDouble(lineColumns[5]));
				hit.setDomainScore(Double.parseDouble(lineColumns[8]));

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
			else {
				// is a comment
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
