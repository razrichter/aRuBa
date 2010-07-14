package org.jcvi.annotation.dao;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class RdfFileDAO implements RdfDAO {

	private Model model = ModelFactory.createDefaultModel();
	
	public RdfFileDAO(URL inUrl) {
		super();
		addRdfFile(inUrl);
	}
	public RdfFileDAO(String inFile) {
		super();
		addRdfFile(inFile);
	}
	public RdfFileDAO(String inFile, String lang) {
		super();
		addRdfFile(inFile, lang);
	}
	public RdfFileDAO(URL inUrl, String lang) {
		super();
		addRdfFile(inUrl, lang);
	}
	
	// Add RDF files, or files of other RDF languages
	public void addRdfFile(String inFile) {
		URL inUrl = this.getClass().getResource(inFile);
		addRdfFile(inUrl);
	}
	public void addRdfFile(String inFile, String lang) {
		URL inUrl = this.getClass().getResource(inFile);
		addRdfFile(inUrl, lang);
	}
	
	public void addRdfFile(URL inUrl) {
		model.read(inUrl.toString());
	}
	public void addRdfFile(URL inUrl, String lang) {
		model.read(inUrl.toString(), lang);
	}
	
	// An iterable of com.hp.hpl.jena.rdf.model.Statement objects
    public Iterator<Statement> iterator() {
        return model.listStatements();
    }
    
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	
}

