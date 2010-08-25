package org.jcvi.annotation.rulesengine;

import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.junit.After;
import org.junit.Before;

public class TestBlastPHitNearBlastPHitRule extends TestCase {

	private ArrayList<Feature> orfs = new ArrayList<Feature>();
	
	@Before
	public void setUp() throws Exception {
		
		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( ResourceFactory.newClassPathResource( "/org/jcvi/annotation/rules/BrainGrabRulesTranslator.dsl", this.getClass() ), ResourceType.DSL );
		kbuilder.add( ResourceFactory.newClassPathResource("/org/jcvi/annotation/rules/BlastPHitNearBlastPHit.dslr", this.getClass()), ResourceType.DSLR);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		
		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		orfs.add(new Feature("orfA", "ORF", 1, 101, 1));
		orfs.add(new Feature("orfB", "ORF", 200, 300, 0));
		orfs.add(new Feature("orfC", "ORF", 4000, 4100, 0));
        Feature source = new Feature("source", "assembly");
		for (Feature f : orfs) {
		    f.setSource(source);
		    ksession.insert(f);
		}
		
		// Only orfA (via orfB) should evaluate true according to our SampleBlastHit rule
		BlastHit hit1 = new BlastHit("blastp","orfA", "SP|P35158", 0.001, 175, 175, 0.002,
				1, 101, 1, 105, 100, 205, 1, 95.0, 92.0);
		hit1.setQueryLength(orfs.get(0).getLength());
		BlastHit hit2 = new BlastHit("blastp","orfC", "SP|P35158", 0.001, 175, 175, 0.002,
                1, 101, 1, 105, 100, 205, 1, 95.0, 92.0);
        hit2.setQueryLength(orfs.get(1).getLength());
        BlastHit hit3 = new BlastHit("blastp","orfB", "SP|P35150", 0.001, 290, 290, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
        hit3.setQueryLength(orfs.get(2).getLength());
		
        ksession.insert(hit1);
        ksession.insert(hit2);
        ksession.insert(hit3);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}
	
	public void testAnnotations() {
	    assertEquals("OrfA is annotated", 1, orfs.get(0).getAssertedAnnotations().size());
        assertEquals("OrfB is not annotated", 0, orfs.get(1).getAssertedAnnotations().size());
	    assertEquals("OrfC is not annotated", 0, orfs.get(2).getAssertedAnnotations().size());
	}
}
