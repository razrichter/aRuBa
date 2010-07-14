package org.jcvi.annotation.facts;

import java.util.*;

public class GenericRule {

	public enum RuleType {
		LEGACY_RULE,
		DRL_RULE,
		DSLR_RULE;
	}
 
	private RuleType type;
	private String ruleId;
	private String conditions;
	private List<String> actions;

	public GenericRule() {}
	public GenericRule(String ruleId) {
		this.setRuleId(ruleId);
	}

	public GenericRule(String ruleId, RuleType type) {
		this.setRuleId(ruleId);
		this.setType(type);
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public RuleType getType() {
		return type;
	}
	public void setType(RuleType type) {
		this.type = type;
	}
	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}
}
