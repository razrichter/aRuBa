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
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.PropertyState;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyTextWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyWriter;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_66575 extends TestCase {

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
		
		// Add only GenProp 66575
		GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager("data/GenomeProperty_66575.n3");
		GPManager.addGenomePropertiesFacts(ksession, data);
 
		// Init feature with our sufficient property
		Feature feature = new Feature("xyz");
		FeatureProperty propSufficient = FeatureProperty.create("TIGR03720"); // ("TIGR03720");
		FeatureProperty propAssigned = FeatureProperty.create("62994");
		feature.addProperty(propSufficient);
	
		// Init a genome that this feature is annotated on
		Genome genome = new Genome();
		feature.setGenome(genome);		
		
		ksession.insert(feature);
		ksession.insert(propSufficient);
		ksession.insert(propAssigned);
		ksession.insert(genome);
		
		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
    }


	@Test
	public void testGenomeProperty66575() {
		GenomeProperty gp = GenomeProperty.create("66575");
		GenomePropertyWriter writer = new GenomePropertyTextWriter();
		System.out.println(writer.write(gp));

		//FeatureProperty featurepropAsserted = FeatureProperty.create("62994");
		//assertEquals(1.0, featurepropAsserted.getValue());
		assertEquals(1.0, gp.getRequired());
		assertEquals(1.0, gp.getFilled());
		assertEquals(1.0, gp.getValue());
		assertEquals(PropertyState.YES, gp.getState());
	}

}
