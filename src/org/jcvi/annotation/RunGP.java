package org.jcvi.annotation;

import org.jcvi.annotation.facts.GenomeProperty;

public class RunGP {

	public static void main(String[] args) {

		String genome = (args == null) ? null : args[0];
		if (genome == null) {
			System.out.println("A Small Genome database is required");
		} else {
			Aruba a = new Aruba();
			//a.addGenomeProperties();
			// a.addGenomePropertiesRules();
			String path = "C:/development/workspace/RulesBasedAnnotation/rules/org/jcvi/annotation/rules/genomeproperties/";
			a.addDrools(path + "AboveTrustedCutoff.drl");
			a.addDrools(path + "suffices.drl");
			// a.addDrools(path + "testrules.drl");

			a.addSmallGenome(genome);
			//a.addGenomePropertiesFacts();

			a.log();
			System.out.println("Run engine...");
			a.run();
			System.out.println("Engine is complete.");
		}
	}
}
