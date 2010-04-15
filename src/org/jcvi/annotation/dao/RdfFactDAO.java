package org.jcvi.annotation.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.PropertyRelationship;
import org.jcvi.annotation.facts.RelationshipType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
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
	private Model model;
	private ArrayList<PropertyRelationship> relationships = new ArrayList<PropertyRelationship>();
	private HashSet<HashMap<String, Object>> properties = new HashSet<HashMap<String, Object>>();
	
	// Used to test if our predicate is a relationship
	private ArrayList<String> relations = new ArrayList<String>(Arrays.asList("sufficient_for", "required_by", "part_of"));
		
	public RdfFactDAO(URL inUrl) {
		this(new RdfFileDAO(inUrl));
	}
	public RdfFactDAO(URL inUrl, String lang) {
		this(new RdfFileDAO(inUrl, lang));
	}
	
	public RdfFactDAO(String inFile) {
		this(new RdfFileDAO(inFile));
	}
	public RdfFactDAO(String inFile, String lang) {
		this(new RdfFileDAO(inFile, lang));
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
	
	public void addRdf(URL inUrl, String lang) {
		this.addDao(new RdfFileDAO(inUrl, lang));
	}
	public void addRdf(String inFile, String lang) {
		this.addDao(new RdfFileDAO(inFile, lang));
	}
	
	public HashSet<HashMap<String, Object>> getProperties() {
		return properties;
	}
	public void setProperties(HashSet<HashMap<String, Object>> properties) {
		this.properties = properties;
	}
	public ArrayList<PropertyRelationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(ArrayList<PropertyRelationship> relationships) {
		this.relationships = relationships;
	}

	public int addDao(RdfFileDAO dao) {
				
		model = dao.getModel();
	   	System.out.println("Loading facts from RDF...");
	   	
	   	// Returns a count of all facts
	   	int total = 0;
	   	
	   	// Load Genome Properties
	   	int numGenomePropertiesBefore = this.getNumGenomeProperties();
	   	
		if (this.addGenomeProperties()) {
			int numGenomePropertiesAdded = this.getNumGenomeProperties() - numGenomePropertiesBefore;
			System.out.println(" " + numGenomePropertiesAdded + " genome properties");

			// Load Feature Properties
			int numFeaturePropertiesBefore = this.getNumFeatureProperties();
			if (this.addFeatureProperties()) {
				int numFeaturePropertiesAdded = this.getNumFeatureProperties() - numFeaturePropertiesBefore;
				System.out.println(" " + numFeaturePropertiesAdded + " feature properties");
				total = numGenomePropertiesAdded + numFeaturePropertiesAdded;
			}
		}
		System.out.println(" " + this.getRelationships().size() + " relationships");
		System.out.println(" " + this.getTotalFacts() + " total facts");
		
		return total;
	}
	

	private boolean addGenomeProperties() {
		
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
                 	GenomeProperty genomeProperty = GenomeProperty.create(name);
                	
                	// Add this Genome Property to our HashSet
                 	properties.add(genomeProperty);
                	
                	// Set the rest of the properties
                	StmtIterator propIter = subject.listProperties();
                	while (propIter.hasNext()) {
                		Statement propStmt = propIter.next();
                		
            			// Add all key-value pairs, except name, and
            			// type urn:genome_properties:ontology:GenomeProperty statement
                		String key = propStmt.getPredicate().getLocalName();
                		RDFNode propNode = propStmt.getObject();
                		String value = propNode.toString();
                		if (key != null) {
                			
                			// Handle property relationships
                			if (this.relations.contains(key)) {
                				
                				// Get the parent FeatureProperty object
                				String propNodeId = propNode.toString();
                				
                				// Get/Create the parent FeatureProperty
                				FeatureProperty parentProperty = FeatureProperty.create(propNodeId);
                   				properties.add(parentProperty);
                   				
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

	private boolean addFeatureProperties() {
		
		boolean isSuccess = true;
		
		// Get our FeatureProperty node as a resource
		Resource gp = model.getResource(ontologyNs + "FeatureProperty");
		
		// This refers to our ":a" predicate in N3 syntax
		Property propTypeOf = model.getProperty(rdfNs, "type");
		
		// FeatureProperty requires a unique name 
		Property propName 	= model.getProperty(ontologyNs + "name");
		
		// Query the model for all Feature Property instances
		Selector featpropSelector = new SimpleSelector(null, propTypeOf, gp);

		// Returns an iterator of statements of syntax
		// urn:genome_properties:instances:FeatureProperty_name a: FeatureProperty
		StmtIterator iter = model.listStatements(featpropSelector);
		
		if (iter.hasNext()) {
            while (iter.hasNext()) {
            	Statement stmt = iter.nextStatement();

                // Get our subject resource from this rdf statement
                Resource subject = stmt.getSubject();
                
                // Get the name statement for our subject resource
                Statement sName = subject.getProperty(propName);
                
                // Throw an error
                if (sName == null) {
                	System.err.println("A name is required for a Feature Property.");
                	isSuccess = false;
                	
                } else {
                    // Get the string identifying the resource of the subject of our name statement
                    // Eg. urn:genome_properties:instances:FeatureProperty_TIGR03006 urn:genome_properties:ontology:name "TIGR03006" .
                    // Eg. <propResourceId> <urn:genome_properties:ontology:name> <String>
                	String propResourceId = sName.getSubject().toString();
                 	String name = sName.getString();
                 	
                 	// Create our FeatureProperty, and add it to our properties list
                	FeatureProperty featureProperty = FeatureProperty.create(name);
                	properties.add(featureProperty);
                	
                	// Handle all of the statements about this FeatureProperty
                	StmtIterator propIter = subject.listProperties();
                	while (propIter.hasNext()) {
                		Statement propStmt = propIter.next();
                		
            			// Add all key-value pairs, except name, and
            			// type urn:genome_properties:ontology:GenomeProperty statement
                		String key = propStmt.getPredicate().getLocalName();
                		RDFNode propNode = propStmt.getObject();
                		String value = propNode.toString();
                		
                		if (key != null) {
                			
                			
                			// Handle property relationships
                			if (this.relations.contains(key)) {
                				
                				// Get the property that the object of this statement refers to
                				HashMap<String, Object> parentProperty = this.getProperty(propNode);
 
                				if (parentProperty != null) {
                					
	                   				// Add the property to our properties list
	                 				properties.add(parentProperty);
	                				
	                  				// Add our PropertyRelationship
		                			if (key.equals("sufficient_for")) {
		                				this.relationships.add(new PropertyRelationship(featureProperty, 
	                							RelationshipType.SUFFICIENT_FOR, parentProperty));
		                			}
		                			else if (key.equals("required_by")) {
		                				this.relationships.add(new PropertyRelationship(featureProperty, 
		                							RelationshipType.REQUIRED_BY, parentProperty));
		                			}
		                			else if (key.equals("part_of")) {
		                				this.relationships.add(new PropertyRelationship(featureProperty, 
		                							RelationshipType.PART_OF, parentProperty));
		                			}
		                			else {
		                				System.out.println("ERROR: " + key + " relation not found");
		                			}
                				} else {
                					System.out.println("Unable to getProperty( " + propNode.toString() +" )");
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
	
	private HashMap<String, Object> getProperty(RDFNode propNode) {
		// Get the property object that this resource refers to
		HashMap<String, Object> property = null;
		
		// type and name properties
		Property propTypeOf = model.getProperty(rdfNs, "type");
		Property propName = model.getProperty(ontologyNs + "name");
		
		String propNodeId = propNode.toString();
		if (model.containsResource(propNode)) {
			
			// Get the resource for this property
			// urn:genome_properties:instances:*Property_63239, or
			// gp:GenomeProperty_66644
			Resource propResource = model.getResource(propNodeId);
			
			// We expect that our Property Resource has a type statement
			if (model.contains(propResource, propTypeOf)) {
				
				// List the values of the type property of a resource
				NodeIterator propTypeNodes = model.listObjectsOfProperty(propResource, propTypeOf);
				while (propTypeNodes.hasNext()) {
					RDFNode propTypeNode = propTypeNodes.next();
					
	                // Get the name statement for our subject resource
	                Statement nameStmt = propResource.getProperty(propName);					
					if (propTypeNode.toString().equals(ontologyNs + "FeatureProperty")) {
						property = FeatureProperty.create(nameStmt.getString());
					}
					else if (propTypeNode.toString().equals(ontologyNs + "GenomeProperty"))
					{
						property = GenomeProperty.create(nameStmt.getString());
					} 
					else
					{
						System.out.println("Error: Unrecognized resource class " + propTypeNode.toString());
					}
				}
				
			} else {
				System.out.println("Error: Type property is undefined for resource " + propResource.toString());
			}
		}
		
		return property;
	}
	
	// Testing purposes only
	public void testListStatements(Resource resource) {
		System.out.println("  testListStatements( " + resource.toString() + " )");
		Selector s = new SimpleSelector(resource, null, (RDFNode) null);
		StmtIterator iter = model.listStatements(s);
		while (iter.hasNext()) {
			Statement stmt = iter.next();
			System.out.println("   statement: " + stmt.toString());
			
		}
	}
	
	public ArrayList<Object> facts() {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.addAll(this.properties);
		facts.addAll(this.relationships);
    	return facts;
	}
	
	public int getTotalFacts() {
		return this.properties.size() + this.relationships.size();
	}

	public int getNumFeatureProperties() {
		int count = 0;
		for (Object fact : this.properties) {
			if (fact instanceof org.jcvi.annotation.facts.FeatureProperty)
			{
				count++;
			}
		}
		return count;
	}

	public int getNumGenomeProperties() {
		int count = 0;
		for (Object fact : this.properties) {
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
