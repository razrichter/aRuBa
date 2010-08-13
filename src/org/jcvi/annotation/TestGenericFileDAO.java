package org.jcvi.annotation;

import java.io.InputStream;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.GenericFileDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.HmmHit;
import org.jcvi.annotation.facts.PropertyRelationship;

public class TestGenericFileDAO {

	public static final void main(String[] args) {
		try {
			
			// Create and add rules to our KnowledgeBase
			final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add( ResourceFactory.newClassPathResource( "../rules/genomeproperties/AboveTrustedCutoff.drl", GenericFileDAO.class ),ResourceType.DRL );
			kbuilder.add( ResourceFactory.newClassPathResource( "../rules/genomeproperties/Suffices.drl", GenericFileDAO.class ),ResourceType.DRL );
			kbuilder.add( ResourceFactory.newClassPathResource( "../rules/genomeproperties/RequiredBy.drl", GenericFileDAO.class ),ResourceType.DRL );

			// Check that there are no errors
			if (kbuilder.hasErrors()) {
				System.err.println(kbuilder.getErrors().toString());
				throw new RuntimeException("Unable to compile rules.");
			}
			
			final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

			final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			
			// Add facts
			System.out.println("Adding Facts...");
			InputStream stream = GenericFileDAO.class.getResourceAsStream("data/hmp098.txt");
			GenericFileDAO dao = new GenericFileDAO(stream);
			for (Feature f : dao.getFeatures()) {
				ksession.insert(f);
			}
			for (HmmHit h : dao.getHmmHits()) {
				ksession.insert(h);
			}
			for (FeatureProperty p : dao.getFeatureProperties()) {
				ksession.insert(p);
			}
			for (GenomeProperty p : dao.getGenomeProperties()) {
				ksession.insert(p);
			}
			for (PropertyRelationship r : dao.getRelationships()) {
				ksession.insert(r);
			}
			System.out.println("   " + dao.getFeatures().size() + " Features");
			System.out.println("   " + dao.getHmmHits().size() + " Hmms");
			System.out.println("   " + dao.getFeatureProperties().size() + " Feature Properties");
			System.out.println("   " + dao.getGenomeProperties().size() + " Genome Properties");
			System.out.println("   " + dao.getRelationships().size() + " Relationships");
			
			// Fire rules
			System.out.println("Firing rules...");
			ksession.fireAllRules();
			ksession.dispose();
			System.out.println("complete.");
			
			GenomeProperty p = GenomeProperty.create("2029");
			System.out.println(p.toStringDetailReport());
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
