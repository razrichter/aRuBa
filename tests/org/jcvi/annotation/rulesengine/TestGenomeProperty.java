package org.jcvi.annotation.rulesengine;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;
import org.jcvi.annotation.dao.SmallGenomeDAOManager;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.facts.Taxon;
import org.junit.Before;

public class TestGenomeProperty extends TestCase {

	private Feature orf;
	private Annotation ann;
	private NCBITaxonomyDAO taxonomyDAO;
	private Taxon taxon;
	private Genome genome;
	private Property property;

	@Before
	public void setUp() throws Exception {

		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/TestGenomePropertyRule.drl", this
						.getClass()), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		final StatefulKnowledgeSession ksession = kbase
				.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		orf = new Feature("testorf", "ORF", 0, 110, 1);

		// Assign a genome with taxon to our Feature
		URL namesUrl = this.getClass().getResource("../dao/names.dmp");
		URL nodesFile = this.getClass().getResource("../dao/nodes.dmp");
		taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
		taxon = taxonomyDAO.getTaxon("Geosporobacter");
		taxonomyDAO.getParents(taxon);

		genome = new Genome(taxon);
		orf.setGenome(genome);

		// Add facts from our SmallGenome DAO
		SmallGenomeDAOManager SGManager = new SmallGenomeDAOManager("gb6");
		SGManager.addSmallGenomeFacts(ksession);

		// Get and assign a genome property to this ORF
		// property = genomePropertyDAO.getProperty("GenProp0610");
		property = GenomeProperty.create("GenProp0610");
		property.setValue(0.6);
		orf.addProperty(property);

		// Make GO assignments
		ann = new Annotation("Test Genome Property Rule");
		ann.addGoId("GO:0030435");
		orf.setAssignedAnnotation(ann);

		// Add to our knowledgebase
		ksession.insert(ann);
		ksession.insert(property);
		ksession.insert(genome);
		ksession.insert(taxon);
		ksession.insert(orf);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}

	public void testDrl() {

		// Test taxon assignment
		assertEquals(390805, taxon.getTaxonId());
		assertEquals("Geosporobacter", taxon.getName());

		// Test Annotation results -- This should be assigned by our rule
		assertEquals("endospore former", ann.getCommonName());
		assertEquals("endospore formers rule", ann.getSource());
	}
}
