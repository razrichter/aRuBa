package org.jcvi.annotation.rulesengine;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.SpecificityType;
import org.jcvi.annotation.facts.Taxon;
import org.junit.Before;

@SuppressWarnings("restriction")
public class TestBlastHitWithTaxonomyRestriction extends TestCase {

	private Feature orf;
	private Annotation ann;
	private NCBITaxonomyDAO taxonomyDAO;
	private Taxon taxon;
	private Genome genome;

	@Before
	public void setUp() throws Exception {

		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/BrainGrabRulesTranslator.dsl", this
						.getClass()), ResourceType.DSL);
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/BrainGrabRules.dslr", this
						.getClass()), ResourceType.DSLR);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		final StatefulKnowledgeSession ksession = kbase
				.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION

		// Create a feature, and assign a genome with taxon
		orf = new Feature("testorf", "ORF", 0, 110, 1);
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		taxonomyDAO.getParents(taxon);

		genome = new Genome(taxon);
		orf.setGenome(genome);

		// Create our hits (only hit1 evalutes to true)
		BlastHit hit1 = new BlastHit("blastp", "testorf", "RF|NP_844922.1",
				0.001, 170, 170, 0.002, 100, 200, 1, 110, 100, 205, 1, 95.0,
				82.0);
		hit1.setQueryLength(orf.getLength());
		// hit1.setQuery(orf);

		BlastHit hit2 = new BlastHit("blastp", "testorf", "RF|NOT_IT.1", 0.001,
				170, 170, 0.002, 100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		hit2.setQueryLength(orf.getLength());
		// hit2.setQuery(orf);

		BlastHit hit3 = new BlastHit("blastp", "testorf", "RF|NOT_IT.1", 0.001,
				170, 170, 0.002, 100, 200, 1, 1000, 100, 205, 1, 95.0, 82.0);
		hit2.setQueryLength(orf.getLength());
		// hit3.setQuery(orf);

		// Add to our knowledgebase
		ksession.insert(taxon);
		ksession.insert(orf);
		ksession.insert(hit1);
		ksession.insert(hit2);
		ksession.insert(hit3);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}

	public void testDsl() {

		this.ann = orf.getAssertedAnnotations().get(0);

		// Test taxon assignment
		assertEquals(taxon.getTaxonId(), 390805);
		assertEquals(taxon.getName(), "Geosporobacter");

		// Test Annotation results
		assertEquals("exosporium protein K", ann.getCommonName());
		assertEquals("exsK", ann.getGeneSymbol());
		assertFalse(ann.getGeneSymbol() == "not_gene_symbol");
		assertEquals("", ann.getEcNumbers().get(0));
		assertEquals(SpecificityType.INIT_EQUIV, ann.getSpecificity());
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

}
