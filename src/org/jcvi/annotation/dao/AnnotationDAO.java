package org.jcvi.annotation.dao;
import java.util.Iterator;
import org.jcvi.annotation.facts.Annotation;

public interface AnnotationDAO extends Iterable<Annotation> {

	// Reading methods	
	Annotation getAnnotation(String annotId);
	Iterator<Annotation> iterator();

}


