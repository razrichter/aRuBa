package org.jcvi.annotation.dao;
import java.util.HashMap;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;

public class SmallGenomeDAOManager {

	private String genome;

	public SmallGenomeDAOManager(String genome) {
		super();
		this.genome = genome;
	}

	public HashMap<String, Integer> addSmallGenomeFacts(StatefulKnowledgeSession ksession) {
		return addSmallGenomeFacts(ksession, new HashMap<String, Integer>());
	}

	public HashMap<String, Integer> addSmallGenomeFacts(
			StatefulKnowledgeSession ksession, HashMap<String, Integer> data) {
		
		SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(genome);
		addDao(ksession, sgFactory.getFeatureDAO(), data);
		addDao(ksession, sgFactory.getAnnotationDAO(), data);
		addDao(ksession, sgFactory.getHmmHitDAO(), data);
		return data;		
	}

	private HashMap<String, Integer> addDao(StatefulKnowledgeSession ksession, Iterable<? extends Object> dao, HashMap<String, Integer> data) {
		for (Object o : dao) {
			ksession.insert(o);
			
			String name = o.getClass().getSimpleName();
			if (data.containsKey(name)) {
				data.put(name, Integer.valueOf(data.get(name)) + 1);
			}
			else
			{
				data.put(name, 1);
			}
		}
		return data;
	}

}
