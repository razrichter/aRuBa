package org.jcvi.annotation.dao;
import java.util.TreeMap;
import org.jcvi.annotation.facts.Taxon;

public interface TaxonomyDAO {

	TreeMap<Integer, Taxon> getTaxonomyMap();

}


