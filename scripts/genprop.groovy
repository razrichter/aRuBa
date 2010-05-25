#!/usr/bin/env groovy

import org.jcvi.annotation.Aruba

a = new Aruba()
a.log()

// a.addGenomeProperties();
a.addDrools('/org/jcvi/annotation/rules/genomeproperties/suffices.drl')
a.addDrools('/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl')

a.addGenomePropertiesFacts()

a.addSmallGenome('gb6')

a.run()

a.addDrools('/org/jcvi/annotation/rules/genomeproperties/requiredby.drl')

a.run()

GenomeProperty.report(System.out);
