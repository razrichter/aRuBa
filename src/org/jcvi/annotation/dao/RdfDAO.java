package org.jcvi.annotation.dao;
import java.util.Iterator;
import com.hp.hpl.jena.rdf.model.Statement;

public interface RdfDAO extends Iterable<Statement> {
	Iterator<Statement> iterator();
}
