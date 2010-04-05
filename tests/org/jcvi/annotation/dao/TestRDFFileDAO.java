package org.jcvi.annotation.dao;
import java.net.URL;
import java.util.Iterator;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

public class TestRDFFileDAO extends TestCase {
	private RdfFileDAO rdfDao;
	
	@Before
	public void setUp() throws Exception {
		URL rdfUrl = this.getClass().getResource("data/genomeproperties.rdf");
		//URL rdfUrl = this.getClass().getResource("data/genomeproperties.n3");
		rdfDao = new RdfFileDAO(rdfUrl);
	}
	
	@Test
	public void testGetIterator() {
		Iterator<Statement> iter = rdfDao.iterator();
		assertTrue(iter.hasNext());
		assertEquals("com.hp.hpl.jena.rdf.model.impl.StatementImpl", iter.next().getClass().getName());
		
		for (Statement stmt : rdfDao) {
			RDFNode subject = stmt.getSubject();
			RDFNode object = stmt.getObject();
			RDFNode predicate = stmt.getPredicate();
			
			System.out.println(subject.toString() + " " 
					+ predicate.toString() + " "
					+ object.toString()
			);
		}
	}
}