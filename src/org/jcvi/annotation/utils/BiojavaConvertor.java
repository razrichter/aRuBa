package org.jcvi.annotation.utils;
import java.util.UUID;

import org.biojavax.bio.seq.RichSequence;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.Taxon;
public class BiojavaConvertor 
{
	public static org.jcvi.annotation.facts.Feature convertBiojavaFeatureToAnnotationFeature(org.biojava.bio.seq.StrandedFeature biojavaFeature){
		//get a unique id the for feature
		UUID id = UUID.randomUUID();
		org.jcvi.annotation.facts.Feature feature = new org.jcvi.annotation.facts.Feature(id.toString(), biojavaFeature.getType());
		if(biojavaFeature.getLocation() != null){
				feature.setStart(biojavaFeature.getLocation().getMin());
				feature.setEnd(biojavaFeature.getLocation().getMax());
		}
		feature.setStrand(biojavaFeature.getStrand().getValue());
		return feature;
	}
	
	

	public static org.jcvi.annotation.facts.Feature convertRichSequenceToFeature(RichSequence richFeature) {
		
		org.jcvi.annotation.facts.Feature feature = new org.jcvi.annotation.facts.Feature(richFeature.getIdentifier(), richFeature.getAlphabet().getName());		
		Taxon taxon = new Taxon(richFeature.getTaxon().getNCBITaxID(), richFeature.getTaxon().getDisplayName());
		Genome genome = new Genome(taxon);
		feature.setGenome(genome);
		return feature;
	}

	
	
}
