package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.jcvi.annotation.facts.*;

public class GenericFileDAO {
	private ArrayList<Feature> features = new ArrayList<Feature>();
	private ArrayList<HmmHit> hits = new ArrayList<HmmHit>();
	private ArrayList<FeatureProperty> featureProperties = new ArrayList<FeatureProperty>();
	private ArrayList<GenomeProperty> genomeProperties = new ArrayList<GenomeProperty>();
	private ArrayList<PropertyRelationship> relationships = new ArrayList<PropertyRelationship>();
	
	public GenericFileDAO(String file) {
		super();
		this.addFile(file);
	}
	
	public GenericFileDAO(InputStream stream) {
		addStream(stream);
	}

	public ArrayList<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	public ArrayList<HmmHit> getHmmHits() {
		return hits;
	}
	public void setHmmHits(ArrayList<HmmHit> hits) {
		this.hits = hits;
	}
	public ArrayList<FeatureProperty> getFeatureProperties() {
		return featureProperties;
	}
	public void setFeatureProperties(ArrayList<FeatureProperty> featureProperties) {
		this.featureProperties = featureProperties;
	}
	public ArrayList<GenomeProperty> getGenomeProperties() {
		return genomeProperties;
	}
	public void setGenomeProperties(ArrayList<GenomeProperty> genomeProperties) {
		this.genomeProperties = genomeProperties;
	}

	public ArrayList<PropertyRelationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(
			ArrayList<PropertyRelationship> relationships) {
		this.relationships = relationships;
	}

	public void addFile(String file) {
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
			addStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void addStream(InputStream stream) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));

			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\\|");
				String objectClass = fields[0].trim();
				if (objectClass.equals("Feature")) {
					String featureId = fields[1].trim();
					String type = fields[2].trim();
					Feature f = new Feature(featureId, type);
					features.add(f);
				} 
				else if (objectClass.equals("FeatureProperty")) {
					String propId = fields[1].trim();
					FeatureProperty p = FeatureProperty.create(propId);
					featureProperties.add(p);
				
				} 
				else if (objectClass.equals("PropertyRelationship")) {
					String[] subject = fields[1].trim().split("\\.");
					String type = fields[2].trim();
					String[] object = fields[3].trim().split("\\.");
					RelationshipType t = RelationshipType.getRelationshipType(type);
					
					Property s = null;
					Property o = null;
					if (subject[0].trim().equals("FeatureProperty")) {
						s = FeatureProperty.create(subject[1].trim());
						featureProperties.add((FeatureProperty) s);
					} else {
						s = GenomeProperty.create(subject[1].trim());
						genomeProperties.add((GenomeProperty) s);
					}
					if (object[0].trim().equals("FeatureProperty")) {
						o = FeatureProperty.create(object[1].trim());
						featureProperties.add((FeatureProperty) o);
					} else {
						o = GenomeProperty.create(object[1].trim());
						genomeProperties.add((GenomeProperty) o);
					}
					
					// Add subject, object and relationship
					relationships.add(new PropertyRelationship(s, t, o));
				}
				else if (objectClass.equals("Hmm")) {
					String hitId = fields[1].trim();
					String queryId = fields[2].trim();
					HmmHit h = new HmmHit(queryId, hitId);
					
					//for temporary purposes
					h.setStrongHit();
					
					hits.add(h);					
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		
		} finally {
			this.close(reader);
		}
		
	}
	
	public void close (BufferedReader stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
