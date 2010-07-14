package org.jcvi.annotation.facts;

import java.util.*;

public class GenericRuleEnum {

	public enum RuleType {
		LEGACY_RULE,
		DRL_RULE,
		DSLR_RULE;
	}
 
	private String ruleId;
	private String conditions;
	private List<String> actions;

	//public abstract void convertTo();

	public GenericRuleEnum() {}
	public GenericRuleEnum(String ruleId) {
		this.setRuleId(ruleId);
	}

	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
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
