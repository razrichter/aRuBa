package org.jcvi.annotation.rulesengine;

import java.net.URL;
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
import org.jcvi.annotation.dao.SmallGenomeAnnotationDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.Taxon;
import org.junit.Before;

public class TestGoAnnotationRule extends TestCase {

	private Feature feat;
	private Taxon taxon;
	private Genome genome;
	private Annotation annot = new Annotation();
	private SmallGenomeAnnotationDAO annotationDAO;

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
		ksession.insert(feat);
		ksession.insert(annot);
		ksession.insert(taxon);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}

	public void testGoAnnotation() {
		// (1) Verify the rule conditions are true
		// 1a. Verify the GO annotation is what we expect
		assertTrue(annot.getGoIds().contains("GO:0008150"));

		// 1b. Verify the taxon restriction condition
		assertTrue(taxon.getParentNames().contains("Firmicutes"));

		// (2) Verify the rule actions occurred
		// 2a. Verify the common name on the annotation was changed
		assertEquals("endospore", feat.getAssignedAnnotation().getCommonName());
		assertFalse(feat.getAssignedAnnotation().getCommonName().equals(
				"sporulation"));

	}
}
