package org.jcvi.annotation.dao;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.jcvi.annotation.facts.HmmHit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHmmer2ResultFileDAO {
	
	private static final String truncatedFile="data/truncated.hmmsearch2";
	private static final String noHitFile="data/no_good_hits.hmmsearch2";
	private static final String poorHitFile="data/poor_hits.hmmsearch2";
	private static final String multiDomainFile="data/multi_domain.hmmsearch2";

	private Hmmer2ResultFileDAO loadFile(String resource) {
		BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)));
		Hmmer2ResultFileDAO dao = new Hmmer2ResultFileDAO(in);
		return dao;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testHmmer2TruncatedFile() {
		Hmmer2ResultFileDAO dao = loadFile(truncatedFile);
		List<HmmHit> hits = dao.getHits();
		assertTrue(hits.size() == 0);
	}
	
	@Test
	public void testHmmer2NoHits() {
		Hmmer2ResultFileDAO dao = loadFile(noHitFile);
		List<HmmHit> hits = dao.getHits();
		assertTrue(hits.size() == 0);
	}
	
	@Test
	public void testPoorHits() {
		Hmmer2ResultFileDAO dao = loadFile(poorHitFile);
		List<HmmHit> hits = dao.getHits();
		assertEquals(0,hits.size());
	}

	@Test
	public void testMultiDomainHits() {
		Hmmer2ResultFileDAO dao = loadFile(multiDomainFile);
		List<HmmHit> hits = dao.getHits();
		assertEquals(51, hits.size());
	}
	
}
