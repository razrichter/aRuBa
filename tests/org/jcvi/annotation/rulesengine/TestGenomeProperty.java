package org.jcvi.annotation.rulesengine;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.dao.SmallGenomePropertyDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestGenomeProperty extends TestCase {

	private RulesEngine engine;
	private Feature orf;
	private Annotation ann;
	private NCBITaxonomyDAO taxonomyDAO;
	private SmallGenomePropertyDAO genomePropertyDAO;
	private SmallGenomeDAOFactory sgDAOFactory;
	private Taxon taxon;
	private Genome genome;
	private Property property;
	
	@Before
	public void setUp() throws Exception {
		
		// Setup our rules engine
		engine = new RulesEngine();
		
		// Create our Feature
		orf = new Feature("testorf", "ORF", 0, 110, 1);

		// Assign a genome with taxon to our Feature
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		taxonomyDAO.getParents(taxon);
		
		genome = new Genome(taxon);
		orf.setGenome(genome);
		
		// Setup our Genome Property DAO
		sgDAOFactory = new SmallGenomeDAOFactory("common");
		genomePropertyDAO = sgDAOFactory.getGenomePropertyDAO("gb6");
		
		// Get and assign a genome property to this ORF
		property = genomePropertyDAO.getProperty("GenProp0610");
		orf.addProperty(property);
		
		// Make GO assignments
		ann = new Annotation("Test Genome Property Rule");
		ann.addGoId("GO:0030435");
		orf.setAssignedAnnotation(ann);
		
        // Add to our knowledgebase
        engine.addFact(ann);
        engine.addFact(property);
        engine.addFact(genome);
        engine.addFact(taxon);
		engine.addFact(orf);
	}
	
	public void testDrl() {
		
		URL drlUrl = engine.getClass().getResource("../rules/TestGenomePropertyRule.drl");
		engine.addResource(drlUrl.toString(), ResourceType.DRL);
		
		/*
		URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
		URL dslrUrl = engine.getClass().getResource("../rules/BrainGrabRules.dslr");
		engine.addResource(dslUrl.toString(), ResourceType.DSL);
		engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
		*/
		
		engine.fireAllRules();

		// Test taxon assignment
		assertEquals(390805, taxon.getTaxonId());
		assertEquals("Geosporobacter", taxon.getName());

		// Test Annotation results
		assertEquals("endospore former", ann.getCommonName());
		assertEquals("endospore formers rule", ann.getSource());
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
