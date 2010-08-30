package org.jcvi.annotation.dao;
import java.net.URL;
import java.util.HashMap;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.facts.GenomePropertyFactory;

public class GenomePropertiesDAOManager {

	private URL url;
	
	public GenomePropertiesDAOManager(URL url) {
		super();
		this.setUrl(url);
	}
	public GenomePropertiesDAOManager(String file) {
		super();
		this.setUrl(this.getClass().getResource(file));
	}
	public GenomePropertiesDAOManager() {
		super();
	}

	public HashMap<String, Integer> addGenomePropertiesFacts(StatefulKnowledgeSession ksession) {
		return addGenomePropertiesFacts(ksession, new HashMap<String, Integer>());
	}

	public HashMap<String, Integer> addGenomePropertiesFacts(
			StatefulKnowledgeSession ksession,
			HashMap<String, Integer> data) {

		// Empty out the cache of GenomeProperty objects from the factory class
		GenomePropertyFactory.clearCache();
		
		// Load genomeProperty objects from our RDF DAO, and insert into KnowledgeSession
		if (url == null) {
			url = this.getClass().getResource("data/genomeproperties.n3");
		}
		RdfFactDAO dao = new RdfFactDAO(url, "N3");
		
		for (Object f : dao.getFacts()) {
			ksession.insert(f);
			String name = f.getClass().getSimpleName();
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

	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

}
