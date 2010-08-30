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
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyTextDAGWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyWriter;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_66644 extends TestCase {

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
		
		// Add only GenProp 66644
		GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager("data/GenomeProperty_66644.n3");
		GPManager.addGenomePropertiesFacts(ksession, data);
 
		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
    }


	@Test
	public void testGenomeProperty66644() {
		GenomeProperty gp = GenomeProperty.create("66644");
		GenomePropertyWriter writer = new GenomePropertyTextDAGWriter();
		System.out.println(writer.write(gp));
		assertEquals(2.0, gp.getRequired());
		assertEquals(0.0, gp.getFilled());
		assertEquals(0.0, gp.getValue());
		assertEquals(PropertyState.NONE_FOUND, gp.getState());
	}

}
