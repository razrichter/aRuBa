package org.jcvi.annotation.dao;
import java.util.Iterator;
import org.jcvi.annotation.facts.GoTerm;

public interface GoTermDAO extends Iterable<GoTerm> {

	// Reading methods	
	GoTerm getGoTerm(String annotId);
	
	Iterator<GoTerm> iterator();

}


