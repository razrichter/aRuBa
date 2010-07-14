package org.jcvi.annotation.rulesengine;

import java.net.URL;
import java.util.List;
import junit.framework.TestCase;
import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.dao.SmallGenomeAnnotationDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestGoAnnotationRule extends TestCase {

	private RulesEngine engine;
	private Feature feat;
	private Taxon taxon;
	private Genome genome;
	private Annotation annot = new Annotation();
	private SmallGenomeAnnotationDAO annotationDAO;
	
	@Before
	public void setUp() throws Exception {
		
		engine = new RulesEngine();

		// Add Drools resources
		URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
		URL dslrUrl = engine.getClass().getResource("../rules/BrainGrabRules.dslr");
		engine.addResource(dslUrl.toString(), ResourceType.DSL);
		engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
		
		// URL url = engine.getClass().getResource("../rules/TestGoAnnotationRule.drl");
		// engine.addResource(url.toString(), ResourceType.DRL);
		
		// Get our feature object
		feat = new Feature("GBAA_pXO2_0015");

		// Assign a taxon to our Feature
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		NCBITaxonomyDAO taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		taxonomyDAO.getParents(taxon);
		genome = new Genome(taxon);
		feat.setGenome(genome);
		
		// Get the current annotations on this feature object
		SmallGenomeDAOFactory sgDAOFactory = new SmallGenomeDAOFactory("gb6");
		annotationDAO = sgDAOFactory.getAnnotationDAO("gb6");
		
		// Get and assign the annotation with the GO Ids
		List<String> goIds = annotationDAO.getGoIds(feat.getFeatureId());
		annot.addGoIds(goIds);
		
		// When our rule fires, we set the common name to endospore
		annot.setCommonName("sporulation");
		
		// Set the annotation on the feature
		feat.setAssignedAnnotation(annot);
		
		// Add the feature to our knowledgebase
		engine.addFact(feat);
		engine.addFact(annot);
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
