package org.jcvi.annotation.dao;
import java.util.Iterator;
import org.jcvi.annotation.facts.Taxon;

public interface TaxonomyDAO extends Iterable<Taxon> {
	
	Iterator<Taxon> iterator();
}


