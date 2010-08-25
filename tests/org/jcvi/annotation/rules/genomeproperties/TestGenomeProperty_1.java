package org.jcvi.annotation.rules.genomeproperties;

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
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.PropertyState;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_1 extends TestCase {

	private String genome = "gb6";
	
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
		SmallGenomeDAOManager SGManager = new SmallGenomeDAOManager(genome);
		HashMap<String, Integer> data = SGManager.addSmallGenomeFacts(ksession);
		
		// Add only GenProp 1
		GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager("data/GenomeProperty_1.n3");
		GPManager.addGenomePropertiesFacts(ksession, data);
 
		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
    }
	
	@Test
	public void testGenomeProperty_1() {
		System.out.println("\ntest GenomeProperty 1...");

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
