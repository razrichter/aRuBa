package org.jcvi.annotationrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
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

	public boolean addResource(String rfile, ResourceType rtype) {
		if (!kbuilder.hasErrors()) {
			kbuilder.add(ResourceFactory.newUrlResource(rfile), rtype);
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
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(fact);
		addFacts(facts);
	}
	
	public void addFacts(Iterable<Object> facts) {
		if (ksession == null)
			ksession = kbase.newStatefulKnowledgeSession();
		for (Iterator<Object> iter = facts.iterator(); iter.hasNext();) {
			Object fact = iter.next();
			ksession.insert(fact);
		}
	}

	public void addGlobals(HashMap<String, Object> global) {
		if (ksession == null)
			ksession = kbase.newStatefulKnowledgeSession();
		for (String globalKey : global.keySet()) {
			ksession.setGlobal(globalKey, global.get(globalKey));
		}
	}

	public void fireAllRules() throws RulesEngineException {
		if (ksession == null) {
			throw new RulesEngineException(
					"Invalid attempt to fire rules without instantiating a knowledge session.");
		}
		logger = (logFilename == null) 
				? KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession) 
				: KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, logFilename);

		if (debug) {
			ksession.addEventListener(new DebugWorkingMemoryEventListener());
		}
		ksession.fireAllRules();
		ksession.dispose();
		ksession = null;
		logger.close();
	}

}
