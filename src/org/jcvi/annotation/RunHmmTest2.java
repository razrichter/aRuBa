package org.jcvi.annotation;

import org.jcvi.annotation.facts.GenomeProperty;

public class RunHmmTest2 {

	public static void main(String[] args) {

		String dbName = "hmp098";
		
		Aruba a = new Aruba();
		a.addSmallGenome(dbName);
		a.addGenomePropertiesFacts();
		a.addDrools("/org/jcvi/annotation/rules/genomeproperties/suffices.drl");
		a.addDrools("/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.testing.drl");
		//a.addDrools("/org/jcvi/annotation/rules/genomeproperties/requiredby.drl");
		a.run();
		
		// Why not FeatureProperty_103?
		// FeatureProperty_TIGR01240 :sufficient_for	gp:FeatureProperty_103;
		
		GenomeProperty g = GenomeProperty.create("2029");
		System.out.println(g.toStringDetailReport());

		a.shutdown();
		System.out.println("Complete.");
	}

}