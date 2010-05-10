package org.jcvi.annotation.rulesengine;

import java.net.URL;
import junit.framework.TestCase;
import org.drools.builder.ResourceType;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestRuleTranslator extends TestCase {

	private RulesEngine engine;
	private Feature feat;
	private Taxon taxon;
	private Annotation annot = new Annotation();
	
	@Before
	public void setUp() throws Exception {
		
		engine = new RulesEngine();

		// Add Drools resources
		URL url = engine.getClass().getResource("../rules/TestTranslatorSolution.drl");
		engine.addResource(url.toString(), ResourceType.DRL);
		
		// Get our feature objects (1 positive and 4 negative controls)
		feat = new Feature("pos");
		Feature feat1 = new Feature("n1");
		Feature feat2 = new Feature("n2");
		Feature feat3 = new Feature("n3");
		Feature feat4 = new Feature("n4");
		
		// Assign a taxon to our Feature
		/*
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		NCBITaxonomyDAO taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		taxonomyDAO.getParents(taxon);
		genome = new Genome(taxon);
		
		// feat and feat1 are Firmicutes
		feat.setGenome(genome);
		feat1.setGenome(genome);
		*/
		
		// feat and feat2 have the right Go Id
		annot.addGoId("GO:0030435");
		feat.setAssignedAnnotation(annot);
		feat2.setAssignedAnnotation(annot);
		
		// feat3 has the other good GO Id
		Annotation annot2 = new Annotation();
		annot2.addGoId("GO:0043936");
		feat3.setAssignedAnnotation(annot);
		
		// feat and feat4 have the right genome property
		Double value = 0.6;
		GenomeProperty property = GenomeProperty.create("GenProp0610");
		property.put("value", value);
		feat.addProperty(property);
		feat4.addProperty(property);
		
		// Add the feature to our knowledgebase
		engine.addFact(feat);
		engine.addFact(feat1);
		engine.addFact(feat2);
		engine.addFact(feat3);
		engine.addFact(feat4);
		engine.addFact(annot);
		engine.addFact(annot2);
		engine.addFact(taxon);
		
		// Fire up the rules engine
		engine.fireAllRules();
	}
	
	public void testGoAnnotation() {
		// (1)	Verify the rule conditions are true
		// 	1a. Verify the GO annotation is what we expect
		assertTrue(annot.getGoIds().contains("GO:0008150"));
		
		// 	1b. Verify the taxon restriction condition
		assertTrue(taxon.getParentNames().contains("Firmicutes"));
		
		// (2) 	Verify the rule actions occurred
		// 	2a. Verify the common name on the annotation was changed
		assertEquals("endospore", feat.getAssignedAnnotation().getCommonName());
		assertFalse(feat.getAssignedAnnotation().getCommonName().equals("sporulation"));
		
	}
	
	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
