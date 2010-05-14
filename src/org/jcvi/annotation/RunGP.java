package org.jcvi.annotation;

import org.drools.builder.ResourceType;

public class RunGP {

	public static void main(String[] args) {

		String genome = (args == null) ? null : args[0];
		if (genome == null) {
			System.out.println("A Small Genome database is required");
		} else {
			Aruba a = new Aruba();
			a.addSmallGenome(genome);
			a.addGenomeProperties();
			a.run();
		}
	}
}
