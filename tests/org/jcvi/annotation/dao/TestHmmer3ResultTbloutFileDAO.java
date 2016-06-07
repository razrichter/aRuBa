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

public class TestHmmer3ResultTbloutFileDAO {

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

	private static final String noHitFile = "data/no_hits.tblout";
	private static final String poorHitFile = "data/no_above_threshold_hits.tblout";
	private static final String singleHitFile = "data/one_hit_seven_misses.tblout";

	private Hmmer3ResultTbloutFileDAO loadFile(String resource) {
		BufferedReader in = new BufferedReader(new InputStreamReader(this
				.getClass().getResourceAsStream(resource)));
		Hmmer3ResultTbloutFileDAO dao = new Hmmer3ResultTbloutFileDAO(in);
		return dao;
	}

	@Test
	public void testHmmer3NoHits() {
		Hmmer3ResultTbloutFileDAO dao = loadFile(noHitFile);
		List<HmmHit> hits = dao.getHits();
		assertTrue(hits.size() == 0);
	}

	@Test
	public void testPoorHits() {
		Hmmer3ResultTbloutFileDAO dao = loadFile(poorHitFile);
		List<HmmHit> hits = dao.getHits();
		assertEquals(0, hits.size());
	}

	@Test
	public void testSingleHit() {
		Hmmer3ResultTbloutFileDAO dao = loadFile(singleHitFile);
		List<HmmHit> hits = dao.getHits();
		assertEquals(1, hits.size());
	}
	
	
}
