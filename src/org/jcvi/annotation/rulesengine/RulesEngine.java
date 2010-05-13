package org.jcvi.annotation.rulesengine;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
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
import org.drools.runtime.rule.FactHandle;


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
	public RulesEngine(String logfile) {
		this();
		this.setFileLogger(logfile);
	}
	public RulesEngine(boolean debug) {
		this();
		if (debug) 
			this.setConsoleLogger();
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
	public StatefulKnowledgeSession getKsession() {
		return ksession;
	}
	public void setKsession(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}
	public KnowledgeRuntimeLogger getLogger() {
		return logger;
	}
	public void setLogger(KnowledgeRuntimeLogger logger) {
		this.logger = logger;
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
	
	public boolean addResource(URL rurl, ResourceType rtype) {
		Resource r = ResourceFactory.newUrlResource(rurl);
		return this.addResource(r, rtype);
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
		FactHandle h = ksession.getFactHandle(fact);
		if (h != null) {
			ksession.retract(h);
		}
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
	public int addFactsTimer(Iterable< ? extends Object> facts) {
		long stime = System.currentTimeMillis();
		long total = 0;
		int count = 0;
		for (Object fact : facts) {
			addFact(fact);
			count++;
			if (count % 1000 == 0) {
				long etime = System.currentTimeMillis() - stime;
				System.out.println(count + " facts added (" + etime + " ms)");
				stime = System.currentTimeMillis();
				total += etime;
			}
		}
		System.out.println("total: " + total + " ms");
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

	public void setFileLogger(String file) {
		this.setLogger(KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, logFilename));
	}
	public void setConsoleLogger() {
		this.setLogger(KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession));
	}
	
	public void fireAllRules() throws RulesEngineException {
		if (debug) {
			if (logFilename == null) {
				logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			}
			ksession.addEventListener(new DebugWorkingMemoryEventListener());
		}
		ksession.fireAllRules();
	}
	public void fireAndDispose() throws RulesEngineException {
		fireAllRules();
		ksession.dispose();
	}

	public void shutdown() throws RulesEngineException {
		
		// Free up resources from stateful knowledge session
		ksession.dispose();
		ksession = null;

		if (logger != null) {
			logger.close();
		}
	
	}
}
