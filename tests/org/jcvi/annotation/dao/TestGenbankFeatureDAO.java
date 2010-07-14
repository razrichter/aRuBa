package org.jcvi.annotation.dao;

import static org.junit.Assert.*;


import java.io.InputStreamReader;


import org.junit.*;
import org.jcvi.annotation.facts.*;

public class TestGenbankFeatureDAO {
	
	GenbankFeatureDAO gbFeatureDAO;
	InputStreamReader gbInputStreamReader ;
	@Before
	public void setUp(){
		//String fileName = "c:/CP000855.gb";
		gbInputStreamReader = new InputStreamReader(this.getClass().getResourceAsStream("data/CP000855.gb"));       
		gbFeatureDAO = new GenbankFeatureDAO(gbInputStreamReader);
		
		
	}
	
	@Test
	public void testIterator() {
		assertTrue(gbFeatureDAO.iterator().next() instanceof Feature);
		/*
		for(Iterator<Feature> it = gbFeatureDAO.iterator(); it.hasNext(); ){
			Feature feature = it.next();
			System.out.println(feature.getStart() + "\t" + feature.getEnd() + "\t" + feature.getType() + "\t" + feature.getTaxon().getTaxonId());
		}*/
		
	}

	@Test
	public void testSourceFeatureIterator(){
		assertTrue(gbFeatureDAO.sourceFeatureIterator().next() instanceof Feature);
	}
}
