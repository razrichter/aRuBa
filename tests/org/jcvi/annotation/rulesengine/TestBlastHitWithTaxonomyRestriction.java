package org.jcvi.annotation.rulesengine;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestBlastHitWithTaxonomyRestriction extends TestCase {

	private RulesEngine engine;
	private Feature orf;
	private Annotation ann;
	private NCBITaxonomyDAO taxonomyDAO;
	private Taxon taxon;
	
	@Before
	public void setUp() throws Exception {
		
		// Setup our rules engine
		engine = new RulesEngine();
		URL url = engine.getClass().getResource("../rules/TestBlastAndTaxRestriction.drl");
		engine.addResource(url.toString(), ResourceType.DRL);

		// Create our Feature
		orf = new Feature("testorf", "ORF", 0, 110, 1);

		// Assign a taxon to our Feature
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		orf.setTaxon(taxon);

		// Create our hits (only hit1 evalutes to true)
		BlastHit hit1 = new BlastHit("blastp","testorf", "RF|NP_844922.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		hit1.setQueryLength(orf.getLength());
		
		BlastHit hit2 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
		BlastHit hit3 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 1000, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
		
        // Add to our knowledgebase
        engine.addFact(taxon);
		engine.addFact(orf);
		engine.addFact(hit1);
		engine.addFact(hit2);
		engine.addFact(hit3);
		engine.fireAllRules();
		this.ann = orf.getAssertedAnnotations().get(0);
	}
	
	public void testGetTaxon() {
		assertEquals(taxon.getTaxonId(), 390805);
		assertEquals(taxon.getName(), "Geosporobacter");
	}

	public void testSampleBlastCommonName() {
		assertEquals(ann.getCommonName(), "exosporium protein K");
	}
	public void testSampleBlastGeneSymbol() {
		assertEquals(ann.getGeneSymbol(), "exsK");
	}
	
	public void testSampleBlastGeneSymbolFails2() {
		assertFalse(ann.getGeneSymbol() == "not_gene_symbol");
	}

	public void testSampleBlastEcNumber() {
		assertEquals(ann.getEcNumber(), null);
	}
	public void testSampleBlastGoIds() {
		ArrayList<String> goIds = new ArrayList<String>();
		goIds.add("GO:0043592"); 
		goIds.add("GO:0003674");
		goIds.add("GO:0008150");
		assertEquals(goIds, ann.getGoIds());
	}
	public void testSampleBlastRoleIds() {
		List<String> roleIds = ann.getRoleIds();
		assertTrue(roleIds.contains("705"));
	}
	
	public void testSampleBlastGeneSymbolFails() {
		assertFalse(ann.getGeneSymbol() == "not_gene_symbol");
	}
	
	public void testSampleBlastSpecificity() {
		 assertEquals(ann.getSpecificity(), Annotation.EQUIVALOG);
	}

	public void testSampleBlastAssertionType() {
		assertEquals(ann.getAssertionType(), Annotation.EXACT);
	}
	
	public void testSampleBlastConfidence() {
		assertEquals(ann.getConfidence(), 80.0);
	}
	
	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
