package org.jcvi.annotation.dao;
import java.util.Map;
import org.jcvi.annotation.facts.Taxon;

public interface TaxonomyDAO {
	Map<Integer, Taxon> loadTaxonomyMap();
	Map<Integer, Taxon> loadTaxonomyMap(String taxonName);
}


