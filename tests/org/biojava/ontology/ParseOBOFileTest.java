package org.biojava.ontology;
import org.biojava.ontology.io.OboParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

import junit.framework.TestCase;

public class ParseOBOFileTest extends TestCase {

	private String urlAddress = "http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology.1_2.obo";

	public void testsOBOFileParsing(){

		OboParser parser = new OboParser();

		try {
			URL url = new URL(urlAddress);
			InputStreamReader goReader = new InputStreamReader(url.openStream());
			BufferedReader oboFile = new BufferedReader ( goReader );
			Ontology ontology = parser.parseOBO(oboFile, "GO Ontology", "The Gene Ontology");

			// Tests sufficient number of terms
			Set<Term> keys = ontology.getTerms();
			assertTrue("Did not find the expected nr of Ontology Terms!" , keys.size() > 50 );
			
			// Tests Contains term
			String goId = "GO:0005186";
			assertTrue("Does not contain GO Term ", ontology.containsTerm(goId));
			
			// Tests description
			Term hth = ontology.getTerm(goId);
			String goDesc = "pheromone activity";
			assertTrue("Term description does not match expectation (" + goDesc + ")", hth.getDescription().equals(goDesc));
		}
		catch (Exception e){
			fail (e.getMessage());
		}
	}
}
