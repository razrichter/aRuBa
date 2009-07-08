package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.utils.BiojavaConvertor;

public class GenbankFeatureDAO implements FeatureDAO {

	
	private ArrayList<String> codingFeatureTypes;
	private String genbankFileName = null;
	private ArrayList<Feature> featureList = null;
		
	public void AddCodingFeatrueTypes(String type){
		if(codingFeatureTypes.indexOf(type)==-1)
			codingFeatureTypes.add(type);
	}
	
	public void removeCodingFeatrueTypes(String type){
		codingFeatureTypes.remove(type);
	}
	
	public GenbankFeatureDAO(String fileName){
		
		this.genbankFileName = fileName;
		codingFeatureTypes = new ArrayList<String>();
		codingFeatureTypes.add("tRNA");
		codingFeatureTypes.add("CDS");
	}
	
	
	@Override
	public Feature getFeature(String featureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Feature> getFeatures() {
		if(this.genbankFileName == null)
			return null;
		return getFeatures(this.genbankFileName);
	}
	
	
	
	public Iterator<Feature> getFeatures(String genbankFileName) {
		if(this.genbankFileName != genbankFileName){
			//need to reset features if passing in a different file;
			this.genbankFileName = genbankFileName;
			featureList = null;
		}
		
		if(featureList == null){
			featureList = new ArrayList<Feature>();
			try{
				BufferedReader br = new BufferedReader(
				        new FileReader(this.getGenbankFileName()));
				Namespace ns = RichObjectFactory.getDefaultNamespace();
				RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br,ns);
				while(seqs.hasNext()){
					try{
						RichSequence rs = seqs.nextRichSequence();
						for(Iterator<org.biojava.bio.seq.Feature> featureIterator = rs.features(); featureIterator.hasNext();){
							org.biojava.bio.seq.StrandedFeature biojavaFeature = (org.biojava.bio.seq.StrandedFeature)featureIterator.next();
							if(this.codingFeatureTypes.indexOf(biojavaFeature.getType()) != -1){
								Feature feature = BiojavaConvertor.convertBiojavaFeatureToAnnotationFeature(biojavaFeature);
								featureList.add(feature);
							}
	
						}
						
						
					}
					catch(Exception e){
						System.out.println(e.getStackTrace());
					}
				}
				 
			}catch(IOException e){
				System.out.println(e.getStackTrace());
			}
		}
		return featureList.iterator();	
	}

	public String getGenbankFileName() {
		return genbankFileName;
	}
		

}
