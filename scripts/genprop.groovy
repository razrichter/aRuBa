#!/usr/bin/env groovy

import org.jcvi.annotation.Aruba

a = new Aruba()
a.log()

a.addDrools('/usr/local/devel/ANNOTATION/naxelrod/Aruba/trunk/rules/org/jcvi/annotation/rules/genomeproperties/suffices.drl')
a.addDrools('/usr/local/devel/ANNOTATION/naxelrod/Aruba/trunk/rules/org/jcvi/annotation/rules/genomeproperties/AboveTrustedCutoff.drl')
a.addDrools('/usr/local/devel/ANNOTATION/naxelrod/Aruba/trunk/rules/org/jcvi/annotation/rules/genomeproperties/requiredby.drl')

a.addGenomePropertiesFacts()

a.addSmallGenome('gb6')

a.run()
