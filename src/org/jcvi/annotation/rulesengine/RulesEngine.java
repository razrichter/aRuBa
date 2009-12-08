package org.jcvi.annotation.rulesengine;

import java.io.File;
import java.io.StringReader;
import java.util.Map;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;


public class RulesEngine {

	private KnowledgeBase kbase;
	private KnowledgeBuilder kbuilder;
	private StatefulKnowledgeSession ksession;
	private String logFilename;
	private boolean debug = false;
	private KnowledgeRuntimeLogger logger;
	
	public RulesEngine() {
		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = kbase.newStatefulKnowledgeSession();	
    }

	public RulesEngine(String logFilename) {
		this();
		this.logFilename = logFilename;
	}

	public RulesEngine(String logFilename, boolean debug) {
		this();
		this.debug = debug;
		this.logFilename = logFilename;
	}
	
	public RulesEngine(boolean debug) {
		this();
		this.debug = debug;
	}

	public KnowledgeBase getKbase() {
		return kbase;
	}
	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}
	public KnowledgeBuilder getKbuilder() {
		return kbuilder;
	}
	public void setKbuilder(KnowledgeBuilder kbuilder) {
		this.kbuilder = kbuilder;
	}

	// Quick way to add a rule for development purposes
	public boolean addRule(String rule) {
		Resource res = ResourceFactory.newReaderResource(new StringReader(rule));
		return this.addResource(res, ResourceType.DRL);
	}
	
	public boolean addResource(String rfile, ResourceType rtype) {
		File f = new File(rfile);
		if (f.exists()) {
			if (f.canRead()) {
				Resource r = ResourceFactory.newFileResource(f);
				return this.addResource(r, rtype);
			} else {
				System.out.println("Error: " + f.getName() + " is unreadable.");
			}
		} else {
			System.out.println("Error: " + f.getName() + " does not exist.");
		}
		return false;
	}
	
	public boolean addResource(Resource resource, ResourceType resourceType) {
		if (!kbuilder.hasErrors()) {
			kbuilder.add(resource, resourceType);
			KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				throw new RulesResourceException(resource, errors);
			} else {
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
				return true;
			}
		} else {
			System.err
					.println("Attempt to addResource on a KnowledgeBase that already has errors.");
		}
		return false;
	}

	// Adding Decision Tables
	public boolean addResource(String rfile, ResourceType rtype, DecisionTableConfiguration dtconfig) {
		if (!kbuilder.hasErrors()) {
			kbuilder.add(ResourceFactory.newUrlResource(rfile), rtype, dtconfig);

			KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error : errors) {
					System.err.println(error);
				}
				throw new IllegalArgumentException(
						"Could not parse knowledge in " + rfile);
			} else {
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
				return true;
			}
		} else {
			System.err
					.println("Attempt to addResource on a KnowledgeBase that already has errors.");
		}
		return false;
	}
	
	public void addFact(Object fact) {
        ksession.insert(fact);
	}
	
	public int addFacts(Iterable< ? extends Object> facts) {
		int count = 0;
		for (Object fact : facts) {
			addFact(fact);
			count++;
		}
		return count;
	}

	
	
	public void addGlobal(String key, Object obj) {
		if (ksession == null) {
			ksession = kbase.newStatefulKnowledgeSession();
		}
		ksession.setGlobal(key,obj);
	}

	public void addGlobals(Map<String, ? extends Object> global) {
		if (ksession == null)
			ksession = kbase.newStatefulKnowledgeSession();
		for (String globalKey : global.keySet()) {
			ksession.setGlobal(globalKey, global.get(globalKey));
		}
	}

	public void shutdown() throws RulesEngineException {
		
		// Free up resources from stateful knowledge session
		ksession.dispose();
		ksession = null;

		if (logger != null) {
			logger.close();
		}
	
	}
	
	public void fireAllRules() throws RulesEngineException {
		if (ksession == null) {
			throw new RulesEngineException(
					"Invalid attempt to fire rules without instantiating a knowledge session.");
		}
		
		if (logFilename != null) {
			logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, logFilename);
		}
		
		if (debug) {
			if (logFilename == null) {
				logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			}
			ksession.addEventListener(new DebugWorkingMemoryEventListener());
		}
		
		
		ksession.fireAllRules();
	}
}
