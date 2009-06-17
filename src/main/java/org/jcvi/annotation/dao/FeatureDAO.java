package org.jcvi.annotation.dao;

import java.util.ArrayList;

import org.jcvi.annotation.facts.Feature;

public interface FeatureDAO {

	// Reading methods	
	Feature getFeature(String featureId);
	ArrayList<Feature> getFeatures();
	
	
	// Writing (CRUD) methods
	// boolean addFeature(Feature feat);
	// boolean deleteFeature(Feature feat);
	// boolean updateFeature(Feature feat);

}


