package org.jcvi.annotation.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.biojava.bio.program.sax.BlastLikeSAXParser;
import org.biojava.bio.program.ssbind.SeqSimilarityAdapter;
import org.jcvi.annotation.facts.BlastHit;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BlastResultFileDAO implements BlastDAO {

	private ArrayList<BlastHit> blastHits = new ArrayList<BlastHit>();
	
	public BlastResultFileDAO(String blastFile) {
		super();
		addBlastResultFile(blastFile);
	}
	
	public BlastResultFileDAO(InputStream blastStream) {
		super();
		addBlastResultFile(blastStream);
	}

	public void addBlastResultFile(String blastFile) {
		FileInputStream blastStream;
		try {
			blastStream = new FileInputStream(blastFile);
			addBlastResultFile(blastStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void addBlastResultFile(InputStream blastStream) {
		try {
			parseBlastResultFile(blastStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void parseBlastResultFile(InputStream blastStream)
			throws IOException, SAXException {
		// make a BlastLikeSAXParser
		BlastLikeSAXParser parser = new BlastLikeSAXParser();
		// calling this means the parser doesn't bother checking the
		// version of the Blast report before parsing it.
		parser.setModeLazy();
		ContentHandler handler = new SeqSimilarityAdapter();

		// use our custom SearchContentHandler (see below)
		BlastResultSearchContentHandler scHandler = new BlastResultSearchContentHandler();
		((SeqSimilarityAdapter) handler).setSearchContentHandler(scHandler);
		parser.setContentHandler(handler);
		parser.parse(new InputSource(blastStream));
		blastHits.addAll(scHandler.getBlastHits());
	}

	public ArrayList<BlastHit> getHits() {
		return blastHits;
	}

}
