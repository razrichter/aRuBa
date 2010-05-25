package org.jcvi.annotation;

import org.jcvi.annotation.facts.GenomeProperty;

public class RunGP {

	public static void main(String[] args) {

		String genome = (args == null) ? null : args[0];
		if (genome == null) {
			System.out.println("A Small Genome database is required");
		} else {
			Aruba a = new Aruba();
			a.log();
			
			//a.addGenomeProperties();
			a.addGenomePropertiesFacts();
			a.addDrools("/org/jcvi/annotation/rules/genomeproperties/suffices.drl");
			a.addDrools("/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl");
			
			a.addSmallGenome(genome);

			a.run();
			
			a.addDrools("/org/jcvi/annotation/rules/genomeproperties/requiredby.drl");

			a.run();
			
			GenomeProperty.detailReport(System.out);
		}
	}
}
