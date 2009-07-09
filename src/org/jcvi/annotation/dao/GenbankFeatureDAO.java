package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.jcvi.annotation.exceptions.GenbankFileIOException;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.utils.BiojavaConvertor;

public class GenbankFeatureDAO implements FeatureDAO {

	
	private ArrayList<String> codingFeatureTypes;
	private String genbankFileName = null;
	private ArrayList<Feature> featureList = null;
	private ArrayList<Feature> sourceFeatureList = null;
		
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
	public Iterator<Feature> getFeatures()throws Exception {
		if(this.genbankFileName == null)
			return new ArrayList<Feature>().iterator();
		return getFeatures(this.genbankFileName);
	}
	
	
	
	public Iterator<Feature> getFeatures(String genbankFileName) throws GenbankFileIOException {
		if(this.genbankFileName != genbankFileName){
			//need to reset features if passing in a different file;
			this.genbankFileName = genbankFileName;
			featureList = null;
			sourceFeatureList = null;
		}
		
		if(featureList == null){
			featureList = new ArrayList<Feature>();
			BufferedReader br = null;
			try{
				br = new BufferedReader(
				        new FileReader(this.getGenbankFileName()));
				Namespace ns = RichObjectFactory.getDefaultNamespace();
				RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br,ns);
				
				while(seqs.hasNext()){
					try{
						RichSequence rs = seqs.nextRichSequence();			
						org.jcvi.annotation.facts.Feature sourceFeature = BiojavaConvertor.convertRichSequenceToFeature(rs,"DNA");
						sourceFeatureList = new ArrayList<Feature>();
						sourceFeatureList.add(sourceFeature);
						for(Iterator<org.biojava.bio.seq.Feature> featureIterator = rs.features(); featureIterator.hasNext();){
							org.biojava.bio.seq.StrandedFeature biojavaFeature = (org.biojava.bio.seq.StrandedFeature)featureIterator.next();
							if(this.codingFeatureTypes.indexOf(biojavaFeature.getType()) != -1){
								Feature feature = BiojavaConvertor.convertBiojavaFeatureToAnnotationFeature(biojavaFeature);
								feature.setSource(sourceFeature);
								feature.setTaxon(sourceFeature.getTaxon());
								featureList.add(feature);
							}
	
						}
						
						
					}
					catch(Exception e){
						throw(new GenbankFileIOException("failed to parsing genbank file " + this.genbankFileName));
					}
				}
				br.close(); 
			}catch(IOException e){				
				throw(new GenbankFileIOException("failed to open genbank file " + this.genbankFileName));
			}finally{
				if(br != null)
					try {
						br.close();
					} catch (IOException e) {
						throw(new GenbankFileIOException("failed to close genbank file " + this.genbankFileName));
					}
			}
			
		}
		return featureList.iterator();	
	}

	public Iterator<Feature> getSourceFeatures(){
		if(sourceFeatureList == null){
			try {
				getFeatures();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ArrayList<Feature>().iterator();				
			}
		}
		return sourceFeatureList.iterator();
	}
	
	public String getGenbankFileName() {
		return genbankFileName;
	}
		

}
