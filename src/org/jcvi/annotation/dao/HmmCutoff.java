package org.jcvi.annotation.dao;

public class HmmCutoff {
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
