package org.jcvi.annotation.dao;
import java.util.Iterator;

import org.jcvi.annotation.facts.Feature;

public interface FeatureDAO extends Iterable<Feature> {

	Feature getFeature(String featureId);
	Iterator<Feature> iterator() throws DaoException;

}
