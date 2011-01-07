package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class HmmCutoffTableDAO implements Iterable {

    public static class HmmCutoff {
        private String HmmAccession;
        private String isologyType;
        private String name;
        private String commonName;
        private Integer length;
        private Double totalTrustedCutoff;
        private Double totalNoiseCutoff;
        private Double domainTrustedCutoff;
        private Double domainNoiseCutoff;

        public HmmCutoff() {
            super();
        }

        public HmmCutoff(String hmmAccession, String isologyType, String name,
                String commonName, Integer length, Double totalTrustedCutoff,
                Double totalNoiseCutoff, Double domainTrustedCutoff,
                Double domainNoiseCutoff) {
            super();
            HmmAccession = hmmAccession;
            this.isologyType = isologyType;
            this.name = name;
            this.commonName = commonName;
            this.length = length;
            this.totalTrustedCutoff = totalTrustedCutoff;
            this.totalNoiseCutoff = totalNoiseCutoff;
            this.domainTrustedCutoff = domainTrustedCutoff;
            this.domainNoiseCutoff = domainNoiseCutoff;
        }

        public String getHmmAccession() {
            return HmmAccession;
        }

        public void setHmmAccession(String hmmAccession) {
            HmmAccession = hmmAccession;
        }

        public String getIsologyType() {
            return isologyType;
        }

        public void setIsologyType(String isologyType) {
            this.isologyType = isologyType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCommonName() {
            return commonName;
        }

        public void setCommonName(String commonName) {
            this.commonName = commonName;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Double getTotalTrustedCutoff() {
            return totalTrustedCutoff;
        }

        public void setTotalTrustedCutoff(Double totalTrustedCutoff) {
            this.totalTrustedCutoff = totalTrustedCutoff;
        }

        public Double getTotalNoiseCutoff() {
            return totalNoiseCutoff;
        }

        public void setTotalNoiseCutoff(Double totalNoiseCutoff) {
            this.totalNoiseCutoff = totalNoiseCutoff;
        }

        public Double getDomainTrustedCutoff() {
            return domainTrustedCutoff;
        }

        public void setDomainTrustedCutoff(Double domainTrustedCutoff) {
            this.domainTrustedCutoff = domainTrustedCutoff;
        }

        public Double getDomainNoiseCutoff() {
            return domainNoiseCutoff;
        }

        public void setDomainNoiseCutoff(Double domainNoiseCutoff) {
            this.domainNoiseCutoff = domainNoiseCutoff;
        }

        public boolean isAboveTrustedCutoff(Double totalScore,
                Double domainScore) {
            boolean isAboveTrusted = false;
            if (domainScore >= this.totalTrustedCutoff) {
                isAboveTrusted = true;
            }
            else if ((totalScore >= this.totalTrustedCutoff)
                    && (domainScore >= this.domainTrustedCutoff)) {
                isAboveTrusted = true;
            }
            return isAboveTrusted;
        }

        public boolean isAboveTrustedCutoff(Double totalScore) {
            if (totalScore >= this.totalTrustedCutoff) {
                return true;
            }
            else {
                return false;
            }
        }

        public boolean isAboveNoiseCutoff(Double totalScore, Double domainScore) {
            boolean isAboveNoise = false;
            if (domainScore >= this.totalNoiseCutoff) {
                isAboveNoise = true;
            }
            else if ((totalScore >= this.totalNoiseCutoff)
                    && (domainScore >= this.domainNoiseCutoff)) {
                isAboveNoise = true;
            }
            return isAboveNoise;
        }

        public boolean isAboveNoiseCutoff(Double totalScore) {
            if (totalScore >= this.totalNoiseCutoff) {
                return true;
            }
            else {
                return false;
            }
        }

        public boolean isBetweenNoiseAndTrustedCutoffs(Double totalScore,
                Double domainScore) {
            if ((!isAboveTrustedCutoff(totalScore, domainScore))
                    && isAboveNoiseCutoff(totalScore, domainScore)) {
                return true;
            }
            else {
                return false;
            }
        }

        public boolean isBetweenNoiseAndTrustedCutoffs(Double totalScore) {
            if ((!isAboveTrustedCutoff(totalScore))
                    && isAboveNoiseCutoff(totalScore)) {
                return true;
            }
            else {
                return false;
            }
        }

    }

    private static final String hmmCutoffTablePath = "data/hmm2CutoffTable.txt";
    private HashMap<String, HmmCutoff> hmmCutoffs = new HashMap<String, HmmCutoff>();

    public HmmCutoffTableDAO() throws IOException {
        super();

        Class<HmmCutoffTableDAO> resourceClass = HmmCutoffTableDAO.class;
        InputStream hmmCutoffTable = resourceClass
                .getResourceAsStream(hmmCutoffTablePath);
            readHmmCutoffs(hmmCutoffTable);
    }
    
    public HmmCutoffTableDAO(InputStream in) throws IOException {
        readHmmCutoffs(in);
    }
    
    public HmmCutoffTableDAO(String filename) throws IOException {
        InputStream hmmCutoffTable = new FileInputStream(filename);
        readHmmCutoffs(hmmCutoffTable);
    }
    
    public void readHmmCutoffs(InputStream table) throws IOException {
        try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(table));
			String line = null;
			while ((line = reader.readLine()) != null) {
			    if (line.matches("^\\s*#.*") || line.equals("") || line.matches("\\s+")) {
			        // Skip comments and blank lines
			    }
			    else {
			        /*
			         * Columns are 0 hmm_acc, 1 iso_type, 2 hmm_name, 3
			         * hmm_com_name, 4 hmm_len, 5 trusted_cutoff, 6 noise_cutoff, 7
			         * trusted_cutoff2, 8 noise_cutoff2
			         */
			        String[] c = line.split("\\t");
			        HmmCutoff cutoff = new HmmCutoff();
			        cutoff.setHmmAccession(c[0]);
			        cutoff.setIsologyType(c[1]);
			        cutoff.setName(c[2]);
			        cutoff.setCommonName(c[3]);
			        cutoff.setLength(new Integer(c[4]));
			        cutoff.setTotalTrustedCutoff(new Double(c[5]));
			        cutoff.setTotalNoiseCutoff(new Double(c[6]));
			        cutoff.setDomainTrustedCutoff(new Double(c[7]));
			        cutoff.setDomainNoiseCutoff(new Double(c[8]));
			        hmmCutoffs.put(cutoff.getHmmAccession(), cutoff);
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IOException(e);
		}

    }
    
    @Override
    public Iterator<HmmCutoff> iterator() {
        return hmmCutoffs.values().iterator();
    }

    public HmmCutoff get(String accession) {
        return hmmCutoffs.get(accession);
    }

    public boolean isTrustedHit(String accession, Double score) {
        HmmCutoff h = hmmCutoffs.get(accession);
        if (h != null) {
            return h.isAboveTrustedCutoff(score);
        }
        else {
            return false;
        }
    }
    public boolean isTrustedHit(String accession, Double totalScore, Double domainScore) {
        HmmCutoff h = hmmCutoffs.get(accession);
        if (h != null) {
            return h.isAboveTrustedCutoff(totalScore, domainScore);
        }
        else {
            return false;
        }
    }

    public boolean isNoisyHit(String accession, Double score) {
        HmmCutoff h = hmmCutoffs.get(accession);
        if (h != null) {
            return h.isBetweenNoiseAndTrustedCutoffs(score);
        }
        else {
            return false;
        }
    }

    public boolean isNoisyHit(String accession, Double totalScore, Double domainScore) {
        HmmCutoff h = hmmCutoffs.get(accession);
        if (h != null) {
            return h.isBetweenNoiseAndTrustedCutoffs(totalScore, domainScore);
        }
        else {
            return false;
        }
    }

}
