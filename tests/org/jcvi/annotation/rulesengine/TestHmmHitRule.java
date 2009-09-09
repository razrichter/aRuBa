package org.jcvi.annotation.rulesengine;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.HmmHit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class TestHmmHitRule extends TestCase {
    private RulesEngine engine;
    private ArrayList<Feature> orfs = new ArrayList<Feature>();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        engine = new RulesEngine();
        
        URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
        URL dslrUrl = engine.getClass().getResource("../rules/HmmHitRule.dslr");
        engine.addResource(dslUrl.toString(), ResourceType.DSL);
        engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
        
        orfs.add( new Feature("testorf1", "ORF", 0, 110, 1) );
        orfs.add(  new Feature("testorf2", "ORF", 0, 110, 1) );
        // Only hit1 should evaluate true according to our SampleBlastHit rule
        HmmHit hit1 = new HmmHit("testorf1", "TIGR02469");
        hit1.setStrongHit();
        HmmHit hit2 = new HmmHit("testorf1", "TIGR02467");
        hit2.setStrongHit();
        HmmHit hit3 = new HmmHit("testorf2", "TIGR02469");
        hit3.setWeakHit();
        HmmHit hit4 = new HmmHit("testorf2", "TIGR02467");
        hit4.setStrongHit();
        
        engine.addFacts(orfs);
        engine.addFact(hit1);
        engine.addFact(hit2);
        engine.addFact(hit3);
        engine.addFact(hit4);
        engine.fireAllRules();
    }

    @After
    public void tearDown() throws Exception {
        engine = null;
    }

    
    public void testHitsAssigned() {
        Feature orf = orfs.get(0);
        assertTrue(orf.getFeatureId().equals("testorf1"));
        assertTrue(orf.getAssertedAnnotations().size() == 1);
        Annotation ann = orf.getAssertedAnnotations().get(0);
        assertTrue(ann.getCommonName().equals("precorrin-6Y C5,15-methyltransferase (decarboxylating)"));
        assertTrue(ann.getEcNumber().equals("2.1.1.132"));
        assertTrue(ann.getGeneSymbol().equals("cobL"));
        // TODO: I don't know how to properly check this. contains looks at object id, List.equals() does as well
        // assertEquals(Arrays.asList("GO:0046140", "GO:0009236", "GO:0046025"), ann.getGoIds());
        assertTrue(ann.getRoleIds().contains("79"));
    }
    
    public void testMissNotAssigned() {
        assertTrue(orfs.get(1).getFeatureId().equals("testorf2"));
        assertTrue(orfs.get(1).getAssertedAnnotations().size() == 0);
    }
}
