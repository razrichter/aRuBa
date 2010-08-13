package org.jcvi.annotation;

import java.util.HashMap;

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

public class RunGP {

	public static void main(String[] args) {

		for (String genome : args) {
			
			try {
				System.out.println("Running Genome Properties on " + genome);
				
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
				GenomePropertiesDAOManager GPManager = new GenomePropertiesDAOManager();
				GPManager.addGenomePropertiesFacts(ksession, data);
				
				// FIRE RULES, CLOSE AND SHUTDOWN
				ksession.fireAllRules();
				ksession.dispose();
				
				// REPORT GENOME PROPERTY 
				GenomeProperty.detailReport(System.out);
				//GenomeProperty p = GenomeProperty.create("2029");
				//System.out.println(p.toStringDetailReport());
				
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			break;
		}
	}
}
