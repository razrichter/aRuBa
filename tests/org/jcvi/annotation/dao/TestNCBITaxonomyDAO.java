package org.jcvi.annotation.dao;
import java.io.*;
import java.util.TreeMap;
import org.jcvi.annotation.facts.Taxon;
import org.jcvi.annotation.dao.NCBITaxonomyDAO;

import junit.framework.TestCase;

public class TestNCBITaxonomyDAO extends TestCase {

	// Taxonomy names and nodes file are downloaded from
	// ftp://ftp.ncbi.nih.gov/pub/taxonomy/
	private String nodesFile = "nodes.dmp";
	private String namesFile = "names.dmp";
	public TreeMap<Integer, Taxon> taxMap = new TreeMap<Integer, Taxon>();
	
	public void testGetTaxonomyMap() {

		// Create our names and nodes Buffered Readers
		InputStream nameStream = this.getClass().getResourceAsStream(namesFile);
		BufferedReader nameReader = new BufferedReader(new InputStreamReader(nameStream));
		InputStream nodeStream = this.getClass().getResourceAsStream(nodesFile);
		BufferedReader nodeReader = new BufferedReader(new InputStreamReader(nodeStream));

		// Load our taxonomy TreeMap
		NCBITaxonomyDAO taxonomyDAO = new NCBITaxonomyDAO(nameReader, nodeReader);
		taxonomyDAO.setNameClassFilter("scientific name");
		
		TreeMap<Integer, Taxon> taxMap = taxonomyDAO.loadTaxonomyMap();
		System.out.println("Number of Taxons: " + taxMap.size());

		// Let's print out the hierarchy for the root node
		Taxon t = taxMap.get(new Integer(43485));
		System.out.println("Taxonomy hierarchy:" + t.getIdHierarchy(taxMap));

		/*
		System.out.println("Load Names...");
		taxonomyDAO.loadNames();
		TreeMap<Integer, Taxon> taxMap = taxonomyDAO.getTaxonomyMap();
		System.out.println("Taxonomy: " + taxMap.size() + " names");
		System.out.print("Load Nodes Information... ");
		taxonomyDAO.loadNodes(false);
		System.out.println("complete.");
		*/
	}

}