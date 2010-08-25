package org.jcvi.annotation.rulesengine;

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
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.SpecificityType;
import org.junit.Before;

public class TestBlastPHitRule extends TestCase {

	private Feature orf;
	private Annotation ann;
	
	@Before
	public void setUp() throws Exception {
		
		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( ResourceFactory.newClassPathResource( "/org/jcvi/annotation/rules/BrainGrabRulesTranslator.dsl", this.getClass() ), ResourceType.DSL );
		kbuilder.add( ResourceFactory.newClassPathResource("/org/jcvi/annotation/rules/BrainGrabRules.dslr", this.getClass()), ResourceType.DSLR);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		orf = new Feature("testorf", "ORF", 0, 110, 1);
		
		// Only hit1 should evaluate true according to our SampleBlastHit rule
		BlastHit hit1 = new BlastHit("blastp","testorf", "RF|NP_844922.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		hit1.setQueryLength(orf.getLength());
		BlastHit hit2 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
		BlastHit hit3 = new BlastHit("blastp","testorf", "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 1000, 100, 205, 1, 95.0, 82.0);
        hit2.setQueryLength(orf.getLength());
		
        
		ksession.insert(orf);
		ksession.insert(hit1);
		ksession.insert(hit2);
		ksession.insert(hit3);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();

		this.ann = orf.getAssertedAnnotations().get(0);
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
		assertEquals("", ann.getEcNumbers().get(0));
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
		 assertEquals(ann.getSpecificity(), SpecificityType.INIT_EQUIV);
	}

	public void testSampleBlastAssertionType() {
		assertEquals(ann.getAssertionType(), Annotation.EXACT);
	}
	
	public void testSampleBlastConfidence() {
		assertEquals(ann.getConfidence(), 96.0);
	}
}
