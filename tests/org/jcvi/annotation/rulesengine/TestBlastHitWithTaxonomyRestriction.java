package org.jcvi.annotation.rulesengine;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DecisionTableFactory;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestBlastHitWithTaxonomyRestriction extends TestCase {

	private RulesEngine engine;
	private Feature orf;
	private Annotation ann;
	private NCBITaxonomyDAO taxonomyDAO;
	private Taxon taxon;
	private Genome genome;
	
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
				
		// Create our hits (only hit1 evalutes to true)
		BlastHit hit1 = new BlastHit("blastp","testorf", "RF|NP_844922.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		hit1.setQueryLength(orf.getLength());
		// hit1.setQuery(orf);
		
		BlastHit hit2 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
		// hit2.setQuery(orf);
		
        BlastHit hit3 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 1000, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
        // hit3.setQuery(orf);
        
        // Add to our knowledgebase
        engine.addFact(taxon);
		engine.addFact(orf);
		engine.addFact(hit1);
		engine.addFact(hit2);
		engine.addFact(hit3);
	}
	
	public void testDsl() {
		
		URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
		URL dslrUrl = engine.getClass().getResource("../rules/BrainGrabRules.dslr");
		engine.addResource(dslUrl.toString(), ResourceType.DSL);
		engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
		engine.fireAllRules();
		this.ann = orf.getAssertedAnnotations().get(0);

		// Test taxon assignment
		assertEquals(taxon.getTaxonId(), 390805);
		assertEquals(taxon.getName(), "Geosporobacter");

		// Test Annotation results
		assertEquals("exosporium protein K", ann.getCommonName());
		assertEquals("exsK", ann.getGeneSymbol());
		assertFalse(ann.getGeneSymbol() == "not_gene_symbol");
		assertEquals("", ann.getEcNumber());
		assertEquals(Annotation.INIT_EQUIV, ann.getSpecificity());
		assertEquals(Annotation.EXACT, ann.getAssertionType());
		assertEquals(96.0, ann.getConfidence());

		ArrayList<String> goIds = new ArrayList<String>();
		goIds.add("GO:0043592"); 
		goIds.add("GO:0003674");
		goIds.add("GO:0008150");
		assertEquals(goIds, ann.getGoIds());

		List<String> roleIds = ann.getRoleIds();
		assertTrue(roleIds.contains("705"));

	}
	
	@SuppressWarnings("restriction")
	public void testDecisionTable() {
		
		// Requires the DecisionTableConfiguration class
		DecisionTableConfiguration dtconfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
		dtconfig.setInputType(DecisionTableInputType.XLS);

		// Add our DecisionTable to our knowledgebase, and fire rules
		URL xlsUrl = engine.getClass().getResource("../rules/BlastHitAndTaxRestrictionTable.xls");

		// Let's check out the Drools translation
		try {
			String drlString = DecisionTableFactory.loadFromInputStream(xlsUrl.openStream(), dtconfig);
			System.out.println(drlString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		engine.addResource(xlsUrl.toString(), ResourceType.DTABLE, dtconfig);
		engine.fireAllRules();
		this.ann = orf.getAssertedAnnotations().get(0);

		// Test taxon assignment
		assertEquals(taxon.getTaxonId(), 390805);
		assertEquals(taxon.getName(), "Geosporobacter");

		// Test Annotation results
		assertEquals("exosporium protein K", ann.getCommonName());
		assertEquals("exsK", ann.getGeneSymbol());
		assertFalse(ann.getGeneSymbol() == "not_gene_symbol");
		// assertEquals("", ann.getEcNumber());
		assertEquals(Annotation.INIT_EQUIV, ann.getSpecificity());
		assertEquals(Annotation.EXACT, ann.getAssertionType());
		assertEquals(96.0, ann.getConfidence());

		ArrayList<String> goIds = new ArrayList<String>();
		goIds.add("GO:0043592"); 
		goIds.add("GO:0003674");
		goIds.add("GO:0008150");
		assertEquals(goIds, ann.getGoIds());

		List<String> roleIds = ann.getRoleIds();
		assertTrue(roleIds.contains("705"));

	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
