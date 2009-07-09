package org.jcvi.annotation.dao;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.io.*;

import org.biojava.utils.bytecode.*;
import org.biojava.bio.seq.Feature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.SimpleBioEntry;
import org.biojavax.bio.seq.io.*;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequenceIterator;

import org.jcvi.annotation.facts.Annotation;

/**
 * @author ylin
 *	
 */
public class GenbankAnnotationDAO implements AnnotationDAO {
	
	private ArrayList<String> codingFeatureTypes;
	private String genbankFileName = null;
		
	public void AddCodingFeatrueTypes(String type){
		if(codingFeatureTypes.indexOf(type)==-1)
			codingFeatureTypes.add(type);
	}
	
	public void removeCodingFeatrueTypes(String type){
		codingFeatureTypes.remove(type);
	}
	
	public GenbankAnnotationDAO(String fileName){
		
		this.genbankFileName = fileName;
		codingFeatureTypes = new ArrayList<String>();
		codingFeatureTypes.add("tRNA");
		codingFeatureTypes.add("CDS");
	}
	
	/* (non-Javadoc)
	 * @see org.jcvi.annotation.dao.AnnotationDAO#getAnnotation(java.lang.String)
	 */
	@Override
	public Annotation getAnnotation(String annotId) {
		// TODO Auto-generated method stub, not implemented here. 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jcvi.annotation.dao.AnnotationDAO#getAnnotations()
	 */
	@Override
	public Iterator<Annotation> getAnnotations() {
		if(this.genbankFileName == null)
			return null;
		return getAnnotations(this.genbankFileName);
	}
	
	public Iterator<Annotation> getAnnotations(String genbankFileName) {
		
		this.genbankFileName = genbankFileName;
		
		
		
		try{
			BufferedReader br = new BufferedReader(
			        new FileReader(this.getGenbankFileName()));
			Namespace ns = RichObjectFactory.getDefaultNamespace();
			//SimpleRichSequenceBuilderFactory sbf= new SimpleRichSequenceBuilderFactory();
			RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br,ns);
			while(seqs.hasNext()){
				try{
					RichSequence rs = seqs.nextRichSequence();
					for(Iterator<Feature> featureIterator = rs.features(); featureIterator.hasNext();){
						Feature feature = (Feature)featureIterator.next();
						if(this.codingFeatureTypes.indexOf(feature.getType()) != -1){
							System.out.println(feature.toString());
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
		
		return null;	
	}

	public String getGenbankFileName() {
		return genbankFileName;
	}
		
}
