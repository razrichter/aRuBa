package org.jcvi.annotation.rulesengine;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.io.Resource;

public class RulesResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RulesResourceException() {
	}

	public RulesResourceException(Resource resource) {
		super("Could not parse resource in " + resource.toString());
	}

	// exception chaining
	public RulesResourceException(Throwable cause) {
		super(cause);
	}

	public RulesResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RulesResourceException(KnowledgeBuilderErrors errors) {
		super("KnowledgeBuilder failed with errors\n" + errors.toString());
	}

	public RulesResourceException(Resource resource, KnowledgeBuilderErrors errors) {
		super("Could not parse resource in " + resource.toString() + " with errors\n" + errors.toString());
	}

}
