package org.jcvi.annotation.dao;

import static org.junit.Assert.*;
import org.junit.*;
import org.jcvi.annotation.facts.*;

public class TestGenbankFeatureDAO {
	
	GenbankFeatureDAO gbFeatureDAO;
	
	@Before
	public void setUp(){
		String fileName = "/org/jcvi/annotation/dao/CP000855.gb";
		gbFeatureDAO = new GenbankFeatureDAO(fileName);
	}
	
	@Test
	public void testIterator() {
		assertTrue(gbFeatureDAO.iterator().next() instanceof Feature);
	}

	@Test
	public void testSourceFeatureIterator(){
		assertTrue(gbFeatureDAO.sourceFeatureIterator().next() instanceof Feature);
	}
}
