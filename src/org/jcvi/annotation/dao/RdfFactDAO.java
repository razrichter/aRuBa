package org.jcvi.annotation.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.PropertyRelationship;
import org.jcvi.annotation.facts.Relationship;
import org.jcvi.annotation.facts.RelationshipType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RdfFactDAO implements Iterable<Object> {

	// Ontology and instance prefixes
	private String ontologyNs = "urn:genome_properties:ontology:";
	private String instanceNs = "urn:genome_properties:instances:";
	private String rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private HashMap<String, HashMap<String, Object>> properties = new HashMap<String, HashMap<String, Object>>();
	private ArrayList<Relationship> relationships = new ArrayList<Relationship>();
	
	// Used to test if our predicate is a relationship
	private ArrayList<String> relations = new ArrayList<String>(Arrays.asList("sufficient_for", "required_by", "part_of"));
		
	private boolean isValid = true;
	
	public RdfFactDAO(URL inUrl) {
		this(new RdfFileDAO(inUrl));
	}
	
	public RdfFactDAO(String inFile) {
		this(new RdfFileDAO(inFile));
	}
	
	public RdfFactDAO(RdfFileDAO dao) {
		super();
		this.addDao(dao);
	}
	
	public void addRdf(URL inUrl) {
		this.addDao(new RdfFileDAO(inUrl));
	}
	public void addRdf(String inFile) {
		this.addDao(new RdfFileDAO(inFile));
	}
	
	public HashMap<String, HashMap<String, Object>> getProperties() {
		return properties;
	}
	public void setProperties(HashMap<String, HashMap<String, Object>> properties) {
		this.properties = properties;
	}
	public ArrayList<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(ArrayList<Relationship> relationships) {
		this.relationships = relationships;
	}

	public int addDao(RdfFileDAO dao) {
				
		Model model = dao.getModel();
	   	System.out.println("Loading facts from RDF...");
	   	
	   	// Returns a count of all facts
	   	int total = 0;
	   	
	   	// Load Genome Properties
	   	int numGenomePropertiesBefore = this.getNumGenomeProperties();
		if (this.addGenomeProperties(model)) {
			int numGenomePropertiesAdded = this.getNumGenomeProperties() - numGenomePropertiesBefore;
			System.out.println(" " + numGenomePropertiesAdded + " genome properties");

			// Load Feature Properties
			int numFeaturePropertiesBefore = this.getNumFeatureProperties();
			if (this.addFeatureProperties(model)) {
				int numFeaturePropertiesAdded = this.getNumFeatureProperties() - numFeaturePropertiesBefore;
				System.out.println(" " + numFeaturePropertiesAdded + " feature properties");
				total = numGenomePropertiesAdded + numFeaturePropertiesAdded;
			}
		}
		return total;
	}
	

	private boolean addGenomeProperties(Model model) {
		
		boolean isSuccess = true;
		
		// Get our FeatureProperty node as a resource
		Resource gp = model.getResource(ontologyNs + "GenomeProperty");
		
		// This basically refers to our ":a" syntax
		Property propTypeOf = model.getProperty(rdfNs, "type");
		
		// Properties of a Genome Property resource
		Property propName 	= model.getProperty(ontologyNs + "name");
		
		// fetch all subjects that are a type of Genome Property
		Selector genpropSelector = new SimpleSelector(null, propTypeOf, gp);

		// Returns an iterator of statements about Genome Properties
		StmtIterator iter = model.listStatements(genpropSelector);
		
		if (iter.hasNext()) {
            while (iter.hasNext()) {
            	Statement stmt = iter.nextStatement();

                // Get our subject resource from this rdf statement
                Resource subject = stmt.getSubject();
                
                // Get the name statement for our subject resource
                Statement sName = subject.getProperty(propName);
                
                 if (sName == null) {
                	System.err.println("A name is required for a Genome Property.");
                	isSuccess = false;
                	
                } else {
                    // Get the string identifying the resource of the subject of our name statement
                 	String propResourceId = sName.getSubject().toString();

                 	String name = sName.getString();
                 	GenomeProperty genomeProperty = new GenomeProperty(name);
                	
                	// Add this Genome Property to our hash map
                	properties.put(propResourceId, genomeProperty);
                	
                	// Set the rest of the properties
                	StmtIterator propIter = subject.listProperties();
                	while (propIter.hasNext()) {
                		Statement propStmt = propIter.next();
                		
            			// Add all key-value pairs, except name, and
            			// type urn:genome_properties:ontology:GenomeProperty statement
                		String key = propStmt.getPredicate().getLocalName();
                		RDFNode objectNode = propStmt.getObject();
                		String value = objectNode.toString();
                		if (key != null) {
                			
                			// Handle property relationships
                			if (this.relations.contains(key)) {
                				
                				// Get the parent FeatureProperty object
                				String objectNodeId = objectNode.toString();
                				
                				// Get/Create the parent FeatureProperty
                				GenomeProperty parentProperty;
                  				if (properties.containsKey(objectNodeId)) {
                					parentProperty = (GenomeProperty) properties.get(objectNodeId);
                				
                 				} else {
                					parentProperty = new GenomeProperty(objectNodeId);
                					properties.put(objectNodeId, parentProperty);
                   				}
                				
                  				// Add our PropertyRelationship
	                			if (key.equals("sufficient_for")) {
	                				relationships.add(new PropertyRelationship(genomeProperty, 
	                							RelationshipType.SUFFICIENT_FOR, parentProperty));
	                			}
	                			else if (key.equals("required_by")) {
	                				relationships.add(new PropertyRelationship(genomeProperty, 
	                							RelationshipType.REQUIRED_BY, parentProperty));
	                			}
	                			else if (key.equals("part_of")) {
	                				relationships.add(new PropertyRelationship(genomeProperty, 
	                							RelationshipType.PART_OF, parentProperty));
	                			}
	                			else {
	                				System.out.println("ERROR: " + key + " relation not found");
	                			}
                			}
                			
                			else if (!(key.equals("name") || (key.equals("type") && value.equals(ontologyNs + "GenomeProperty")))) {
                				genomeProperty.put(key, value);
                			}
                		}
                	}
                 }
            }			
			
        } else {
            System.out.println("No statements were found in the database");
        }  
		return isSuccess;
	}

	private boolean addFeatureProperties(Model model) {
		
		boolean isSuccess = true;
		
		// Get our FeatureProperty node as a resource
		Resource gp = model.getResource(ontologyNs + "FeatureProperty");
		
		// This basically refers to our ":a" syntax
		Property propTypeOf = model.getProperty(rdfNs, "type");
		
		// Properties of a Genome Property resource
		Property propName 	= model.getProperty(ontologyNs + "name");
		
		// fetch all subjects that are a type of Genome Property
		Selector genpropSelector = new SimpleSelector(null, propTypeOf, gp);

		// Returns an iterator of statements about Genome Properties
		StmtIterator iter = model.listStatements(genpropSelector);
		
		if (iter.hasNext()) {
            while (iter.hasNext()) {
            	Statement stmt = iter.nextStatement();

                // Get our subject resource from this rdf statement
                Resource subject = stmt.getSubject();
                
                // Get the name statement for our subject resource
                Statement sName = subject.getProperty(propName);
                
                 if (sName == null) {
                	System.err.println("A name is required for a Feature Property.");
                	isSuccess = false;
                	
                } else {
                    // Get the string identifying the resource of the subject of our name statement
                    // Eg. urn:genome_properties:instances:FeatureProperty_TIGR03006 urn:genome_properties:ontology:name "TIGR03006" .
                    // Eg. <propResourceId> <urn:genome_properties:ontology:name> <String>
                	String propResourceId = sName.getSubject().toString();

                 	String name = sName.getString();
                	FeatureProperty featureProperty = new FeatureProperty(name);
                	
                	// Add this Feature Property to our hash map
                	properties.put(propResourceId, featureProperty);
                	
                	// Set the rest of the properties
                	StmtIterator propIter = subject.listProperties();
                	while (propIter.hasNext()) {
                		Statement propStmt = propIter.next();
                		
            			// Add all key-value pairs, except name, and
            			// type urn:genome_properties:ontology:GenomeProperty statement
                		String key = propStmt.getPredicate().getLocalName();
                		RDFNode objectNode = propStmt.getObject();
                		String value = objectNode.toString();
                		if (key != null) {
                			
                			
                			// Handle property relationships
                			if (this.relations.contains(key)) {
                				
                				// Get the parent FeatureProperty object
                				String objectNodeId = objectNode.toString();
                				
                				// Get/Create the parent FeatureProperty
                  				FeatureProperty parentProperty;
                  				if (properties.containsKey(objectNodeId)) {
                					parentProperty = (FeatureProperty) properties.get(objectNodeId);
                				
                 				} else {
                					parentProperty = new FeatureProperty(objectNodeId);
                					properties.put(objectNodeId, parentProperty);
                   				}
                				
                  				// Add our PropertyRelationship
	                			if (key.equals("sufficient_for")) {
	                				relationships.add(new PropertyRelationship(featureProperty, 
	                							RelationshipType.SUFFICIENT_FOR, parentProperty));
	                			}
	                			else if (key.equals("required_by")) {
	                				relationships.add(new PropertyRelationship(featureProperty, 
	                							RelationshipType.REQUIRED_BY, parentProperty));
	                			}
	                			else if (key.equals("part_of")) {
	                				relationships.add(new PropertyRelationship(featureProperty, 
	                							RelationshipType.PART_OF, parentProperty));
	                			}
	                			else {
	                				System.out.println("ERROR: " + key + " relation not found");
	                			}
                			}
                			
                			else if (!(key.equals("name") || (key.equals("type") && value.equals(ontologyNs + "FeatureProperty")))) {
                				featureProperty.put(key, value);
                			}
                			
                		}
                	}
                 }
            }			
			
        } else {
            System.out.println("No statements were found in the database");
        }  
		return isSuccess;
	}
	
	public ArrayList<Object> facts() {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.addAll(this.properties.values());
		facts.addAll(this.relationships);
    	return facts;
	}
	
	public int getTotalFacts() {
		return this.facts().size();
	}

	public int getNumFeatureProperties() {
		int count = 0;
		for (Object fact : this.properties.values()) {
			if (fact instanceof org.jcvi.annotation.facts.FeatureProperty)
			{
				count++;
			}
		}
		return count;
	}

	public int getNumGenomeProperties() {
		int count = 0;
		for (Object fact : this.properties.values()) {
			if (fact instanceof org.jcvi.annotation.facts.GenomeProperty)
			{
				count++;
			}
		}
		return count;
	}
	
	public Iterator<Object> iterator() {
		return this.facts().iterator();
    }
}
