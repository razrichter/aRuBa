package org.jcvi.annotation.dao;
import java.util.Iterator;
import org.jcvi.annotation.facts.GoTerm;

public interface GoTermDAO {

	// Reading methods	
	GoTerm getGoTerm(String annotId);
	Iterator<GoTerm> getGoTerms();

}


