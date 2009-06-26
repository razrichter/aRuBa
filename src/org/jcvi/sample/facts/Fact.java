package org.jcvi.sample.facts;

import java.util.HashMap;

public class Fact {
	private static HashMap<String,Fact> factSet = new HashMap<String,Fact>();
	private String name;

	private Fact(String name) {
		super();
		// System.out.println("New Fact "+name);
		this.name = name;
	}
	
	public static Fact create (String name) {
		Fact fact;
		if (factSet.containsKey(name)) {
			fact = factSet.get(name);
		}
		else {
			fact = new Fact(name);
			factSet.put(name, fact);
		}
		return fact;
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
