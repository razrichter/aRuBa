package org.jcvi.annotation.dao;

import static org.junit.Assert.*;

import org.junit.*;
import java.util.*;
import org.jcvi.annotation.facts.*;

public class TestGenbankFeatureDAO {
	
	GenbankFeatureDAO gbFeatureDAO;
	
	@Before
	public void setUp(){
		String fileName = "c:/CP000855.gb";
		gbFeatureDAO = new GenbankFeatureDAO(fileName);
	}
	
	@Test
	public void testGetFeatures() {
		
		try {
			for(Iterator<Feature> fi = gbFeatureDAO.getFeatures();fi.hasNext();){
				Feature feature = fi.next();
				System.out.println(feature.getFeatureId() + "\t" + feature.getType() + "\t" + feature.getStart() + "\t" + feature.getEnd() +  "\t" + feature.getStrand());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
	}

	@Test
	public void testGetSourceFeatures(){
		try {
			for(Iterator<Feature> fi = gbFeatureDAO.getSourceFeatures();fi.hasNext();){
				Feature feature = fi.next();
				System.out.println(feature.getFeatureId() + "\t" + feature.getType() + "\t" + feature.getStart() + "\t" + feature.getEnd() +  "\t" + feature.getTaxon().getName());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
	}
}
