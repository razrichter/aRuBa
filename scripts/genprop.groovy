#!/usr/bin/env groovy

import org.jcvi.annotation.Aruba
import org.jcvi.annotation.facts.GenomeProperty

// Create a new instance of our Aruba class
a = new Aruba()
a.log()

// Load Genome Properties facts and rules
// a.addGenomeProperties()
a.addDrools('/org/jcvi/annotation/rules/genomeproperties/suffices.drl')
a.addDrools('/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl')
a.addGenomePropertiesFacts()

// Load facts from the Small Genome database
a.addSmallGenome('gb6')

// Run the first phase
a.run()

// Add rule to compute Genome Property values
a.addDrools('/org/jcvi/annotation/rules/genomeproperties/requiredby.drl')

a.run()

// Detailed report
GenomeProperty.detailReport(System.out);
