package org.jcvi.annotation.dao;

import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Before;

public class TestHmmCutoffTableDAO extends TestCase {
    private HmmCutoffTableDAO cutoffTable;
    private HmmCutoff testCutoff;
    private static final String testTablePath = "data/testHmmCutoffTable.txt";
    private static final String damagedTablePath = "data/badHmmCutoffTable.txt";
    private static final String testHmmAccession = "TE00001";

    @Before
    public void setUp() throws Exception {
        Class<TestHmmCutoffTableDAO> resourceClass = TestHmmCutoffTableDAO.class;
        InputStream testCutoffTable = resourceClass
                .getResourceAsStream(testTablePath);
        cutoffTable = new HmmCutoffTableDAO(testCutoffTable);
        testCutoff = cutoffTable.get(testHmmAccession);
    }

    public void testReadsLiveFile() {

        try {
            HmmCutoffTableDAO liveTable = HmmCutoffTableDAO.getHmmer2CutoffDAO();
            assertNotNull("Read live table without Error", liveTable);
        }
        catch (Exception e) {
            fail("Read live table without error failed: " + e.toString());
        }
    }

    public void testReadsTestFile() {
        try {
            Class<TestHmmCutoffTableDAO> resourceClass = TestHmmCutoffTableDAO.class;
            InputStream testCutoffTable = resourceClass
                    .getResourceAsStream(testTablePath);
            HmmCutoffTableDAO testTable = new HmmCutoffTableDAO(testCutoffTable);
            assertNotNull("Read test table without Error", testTable);
        }
        catch (Exception e) {
            fail("Read test table without error failed: " + e.toString());
        }
    }

    public void testErrorOnDamagedFile() {
        Class<TestHmmCutoffTableDAO> resourceClass = TestHmmCutoffTableDAO.class;
        InputStream testCutoffTable = resourceClass
                .getResourceAsStream(damagedTablePath);
        HmmCutoffTableDAO testTable = null;
        try {
            testTable = new HmmCutoffTableDAO(testCutoffTable);
            fail("Read test table without Error");
        }
        catch (Exception e) {
            // System.out.println("Read test table without error failed: " + e.toString());
            assertNull("Read test table without error failed: " + e.toString(),
                    testTable);
        }
    }

    public void testRetrieveCorrectEntry() {
        assertEquals("isologyType matches", "family", testCutoff
                .getIsologyType());
        assertEquals("name matches", "test", testCutoff.getName());
        assertEquals("commonName matches", "test hmm information line",
                testCutoff.getCommonName());
        assertEquals("length matches", Integer.valueOf(100), testCutoff
                .getLength());
        assertEquals("totalTrustedCutoff matches", 10.0, testCutoff
                .getTotalTrustedCutoff(), 1e-8);
        assertEquals("domainTrustedCutoff", 0.0, testCutoff
                .getDomainTrustedCutoff(), 1e-8);
        assertEquals("totalNoiseCutoff", -50.0, testCutoff
                .getTotalNoiseCutoff(), 1e-8);
        assertEquals("domainNoiseCutoff", -100.0, testCutoff
                .getDomainNoiseCutoff(), 1e-8);
    }

    public void testAboveTrustedByDomain() {
        assertTrue(testCutoff.isAboveTrustedCutoff(0.0, 10.0));
        assertFalse(testCutoff.isAboveTrustedCutoff(10.0, -0.1));
    }

    public void testAboveTrustedByTotalScore() {
        assertTrue(testCutoff.isAboveTrustedCutoff(10.0, 9.9));
        assertFalse(testCutoff.isAboveTrustedCutoff(9.9, 9.9));
    }

    public void testAboveTrustedNoDomainInfo() {
        assertTrue(testCutoff.isAboveTrustedCutoff(10.0));
        assertFalse(testCutoff.isAboveTrustedCutoff(9.9));
    }

    public void testAboveNoiseByDomain() {
        Double totalScore = -100.0;
        Double domainScore = -50.0;
        assertFalse(testCutoff.isAboveTrustedCutoff(totalScore, domainScore));
        assertTrue(testCutoff.isAboveNoiseCutoff(totalScore, domainScore));
        assertTrue(testCutoff.isBetweenNoiseAndTrustedCutoffs(totalScore,
                domainScore));
        assertFalse(testCutoff
                .isAboveNoiseCutoff(totalScore, domainScore - 1.0));
    }

    public void testAboveNoiseByTotalScore() {
        Double totalScore = -50.0;
        Double domainScore = -100.0;
        assertFalse(testCutoff.isAboveTrustedCutoff(totalScore, domainScore));
        assertTrue(testCutoff.isAboveNoiseCutoff(totalScore, domainScore));
        assertTrue(testCutoff.isBetweenNoiseAndTrustedCutoffs(totalScore,
                domainScore));
        assertFalse(testCutoff
                .isAboveNoiseCutoff(totalScore - 1.0, domainScore));

    }

    public void testAboveNoiseNoDomainInfo() {
        Double score = -50.0;
        assertFalse(testCutoff.isAboveTrustedCutoff(score));
        assertTrue(testCutoff.isAboveNoiseCutoff(score));
        assertTrue(testCutoff.isBetweenNoiseAndTrustedCutoffs(score));
    }

    public void testIsTrustedHit() {
        Double score = 10.0;
        assertTrue(cutoffTable.isTrustedHit(testHmmAccession, score));
        assertFalse(cutoffTable.isTrustedHit(testHmmAccession, score - 0.1));
    }

    public void testIsNoisyHit() {
        Double trustedScore = 10.0;
        Double noiseScore = -50.0;
        assertFalse(cutoffTable.isNoisyHit(testHmmAccession, trustedScore));
        assertTrue(cutoffTable.isNoisyHit(testHmmAccession, trustedScore - 0.1));
        assertTrue(cutoffTable.isNoisyHit(testHmmAccession, noiseScore));
        assertFalse(cutoffTable.isNoisyHit(testHmmAccession, noiseScore - 0.1));
    }

}
