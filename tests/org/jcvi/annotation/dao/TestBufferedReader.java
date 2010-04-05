package org.jcvi.annotation.dao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class TestBufferedReader {

	//private static Model model = ModelFactory.createDefaultModel();;
	private static String inFilename = "data/genomeproperties.n3";
    private static BufferedReader reader;
    
	public static void main(String[] args) throws IOException {

	    File inFile = new File(inFilename);
	    
	    try 
	    {
	    	if (inFile.exists()) {
	    		reader = new BufferedReader(new FileReader(inFile));
	    		
	    		String line = reader.readLine();
	    		while (line != null) {
	    			System.out.println(line);
	    			line = reader.readLine();
	    		}
	    	}
		    else {
		    	System.err.println("File: " + inFile + " not found");
		    }
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    }
	    finally 
	    {
	    	reader.close();
	    }
	    

		/*
		model.read(inStream, null); // Read from an input stream
		model.write(System.out);

		StmtIterator iter = model.listStatements();
		Statement stmt;
		while (iter.hasNext()) {

			stmt = iter.next();
			System.out.println(stmt.getSubject().toString() + "\t" + 
					stmt.getPredicate().toString() + "\t" +
					stmt.getObject().toString());
			
		}
		*/
	}


}
