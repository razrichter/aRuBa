package org.jcvi.annotation.dao;


import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jcvi.annotation.facts.BlastHit;
import org.junit.Before;

public class TestBlastResultFileDAO extends TestCase{
    
    // private BlastResultFileDAO singleHitDAO;
    // private BlastResultFileDAO noHitsDAO;
    // private BlastResultFileDAO multipleHSPsDAO;
    private BlastResultFileDAO multipleHitsDAO;
    @Before
    public void setUp() throws Exception {
        // InputStream singleHitBlastStream = this.getClass().getResourceAsStream("single_blast_hit.blastp.out");
        // this.singleHitDAO = new BlastResultFileDAO(singleHitBlastStream);
        InputStream multipleHitsBlastStream = this.getClass().getResourceAsStream("data/full_blast.blastp.out");
        this.multipleHitsDAO = new BlastResultFileDAO(multipleHitsBlastStream);
        // TODO add other blast search results
    }
    
    public void testMultipleHits() {
        ArrayList<BlastHit> hits = multipleHitsDAO.getHits();
        assertEquals(hits.size(), 250);
        BlastHit hit = hits.get(0);
        assertEquals(hit.getProgram(), "ncbi-blastp");
        assertEquals(hit.getQueryId(), "clostox_0006");
        assertEquals(hit.getHitId(), "GB|ABC26002.1|83628244|DQ310546");
        assertEquals(hit.getEValue(), 0.0);
        assertEquals(hit.getBitScore(), 2608.0);
        //assertEquals(hit.getPValue(), 1);
        assertEquals(hit.getAlignmentLength(), 1296);
        assertEquals(hit.getQueryLength(), 1296);
        assertEquals(hit.getQueryStart(), 1);
        assertEquals(hit.getQueryEnd(), 1296);
        // assertEquals(hit.getQueryStrand(), "??");
        assertEquals(hit.getHitLength(), 1296);
        assertEquals(hit.getHitStart(), 1);
        assertEquals(hit.getHitEnd(), 1296);
        // assertEquals(hit.getHitStrand(), "??");
        assertEquals(hit.getPercentIdentity(), 100.0, 1e-50);
        assertEquals(hit.getPercentSimilarity(), 100.0, 1e-50);
        // TODO Add tests for worst hit
    }
    // TODO Add tests for singleHit, noHits, multipleHSPs

}
