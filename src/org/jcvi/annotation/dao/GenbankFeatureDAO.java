package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.utils.BiojavaConvertor;

public class GenbankFeatureDAO implements FeatureDAO {

	
	private ArrayList<String> codingFeatureTypes;
	private String genbankFileName = null;
	private ArrayList<Feature> featureList = null;
	private ArrayList<Feature> sourceFeatureList = null;
	private InputStreamReader inputStreamReader = null;
		
	public void AddCodingFeatrueTypes(String type){
		if(codingFeatureTypes.indexOf(type)==-1)
			codingFeatureTypes.add(type);
	}
	
	public void removeCodingFeatrueTypes(String type){
		codingFeatureTypes.remove(type);
	}
	
	private void initCodingFeatureTypes(){
		codingFeatureTypes = new ArrayList<String>();
		codingFeatureTypes.add("tRNA");
		codingFeatureTypes.add("CDS");
		
	}
	public GenbankFeatureDAO(InputStreamReader inputStreamReader){
			this.inputStreamReader = inputStreamReader;
			initCodingFeatureTypes();
	}
	
	public GenbankFeatureDAO(String fileName){
		
		this.genbankFileName = fileName;
		initCodingFeatureTypes();
	}
	
	
	@Override
	public Feature getFeature(String featureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Feature> iterator() {
		if(this.genbankFileName == null && featureList == null && this.inputStreamReader == null)
			return new ArrayList<Feature>().iterator();
		if(featureList != null)
			return featureList.iterator();
		if(this.genbankFileName != null)
			return iterator(this.genbankFileName);
		return iterator(this.inputStreamReader);
	}
	
	public Iterator<Feature> iterator(InputStreamReader genbankStream) throws DaoException {
		this.genbankFileName = null;
		featureList = null;
		sourceFeatureList = null;
		return parseFileInputStream(genbankStream);
	}
	
	public Iterator<Feature> parseFileInputStream(InputStreamReader inputStream) throws DaoException{

		featureList = new ArrayList<Feature>();
		BufferedReader br = null;
		try{
			br = new BufferedReader(inputStream);
			Namespace ns = RichObjectFactory.getDefaultNamespace();
			RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br,ns);
			
			while(seqs.hasNext()){
				try{
					RichSequence rs = seqs.nextRichSequence();			
					org.jcvi.annotation.facts.Feature sourceFeature = BiojavaConvertor.convertRichSequenceToFeature(rs);
					sourceFeatureList = new ArrayList<Feature>();
					sourceFeatureList.add(sourceFeature);
					for(Iterator<org.biojava.bio.seq.Feature> featureIterator = rs.features(); featureIterator.hasNext();){
						org.biojava.bio.seq.StrandedFeature biojavaFeature = (org.biojava.bio.seq.StrandedFeature)featureIterator.next();
						if(this.codingFeatureTypes.indexOf(biojavaFeature.getType()) != -1){
							Feature feature = BiojavaConvertor.convertBiojavaFeatureToAnnotationFeature(biojavaFeature);
							feature.setSource(sourceFeature);
							feature.setGenome(sourceFeature.getGenome());
							featureList.add(feature);
						}

					}
					
					
				}
				catch(Exception e){
					String message = "failed to parsing genbank file " + ((this.genbankFileName == null)? "" : this.genbankFileName);
					throw(new DaoException("failed to parsing genbank file " + message));
				}
			}
			br.close(); 
		}catch(IOException e){
			String message = "failed to open genbank file " + ((this.genbankFileName == null)? "" : this.genbankFileName);
			throw(new DaoException(message));
		}finally{
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					throw(new DaoException("failed to close genbank file " + this.genbankFileName));
				}
		}
		return featureList.iterator();
	}
	public Iterator<Feature> iterator(String genbankFileName) throws DaoException {
		FileReader fileReader;
		if(this.genbankFileName == genbankFileName && featureList != null){
			return featureList.iterator();
			//need to reset features if passing in a different file;		
		}
		
		this.genbankFileName = genbankFileName;
		featureList = null;
		sourceFeatureList = null;
		inputStreamReader = null;
		try{	
			fileReader = new FileReader(this.getGenbankFileName());
			
		}catch(FileNotFoundException e){
			throw new DaoException("genbank file " + this.genbankFileName + " not found!");
		}
		return parseFileInputStream(fileReader);
	}

	public Iterator<Feature> sourceFeatureIterator(){
		if(sourceFeatureList == null){
			try {
				iterator();
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
