package org.jcvi.annotation.rules.genomeproperties;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.GenericFileDAO;
import org.jcvi.annotation.dao.GenomePropertiesDAOManager;
import org.jcvi.annotation.dao.SmallGenomeDAOManager;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.HmmHit;
import org.jcvi.annotation.facts.PropertyState;
import org.junit.Before;
import org.junit.Test;

public class TestGenomePropertyDependency extends TestCase {

	private String genomeId = "gb6";
	private ArrayList<Feature> orfs = new ArrayList<Feature>();
	private Genome genome;
	private ArrayList<HmmHit> hits = new ArrayList<HmmHit>();
	
    @Before
    public void setUp() throws Exception {
    	
		// ADD RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( ResourceFactory.newClassPathResource( "/org/jcvi/annotation/rules/genomeproperties/GenomePropertiesChangeSet.xml", GenericFileDAO.class ),ResourceType.CHANGE_SET );
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		orfs.add(new Feature("testorf1", "ORF", 0, 110, 1));
		orfs.add(new Feature("testorf2", "ORF", 111, 210, 1));
		
		// Add only GenPropDependentProperty
		GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager("data/GenomePropertyDependentProperty.n3");
		GPManager.addGenomePropertiesFacts(ksession, data);
 
		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
    }
	
	@Test
	public void testGenomeProperty_1() {
		System.out.println("\ntest GenomeProperty Dependency...");

		// Get our GenomeProperty from the GenomeProperty.propsCache,
		// which was loaded during setup()
		GenomeProperty p = GenomeProperty.create("1");
		System.out.println(p.toStringReport());
		
		assertEquals(7.0, p.getFilled());
		assertEquals(7.0, p.getRequired());
		assertEquals(1.0, p.getValue());
		assertEquals(PropertyState.YES, p.getState());
	}

}
