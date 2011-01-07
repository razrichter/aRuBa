package org.jcvi.annotation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojavax.Namespace;
import org.biojavax.Note;
import org.biojavax.RichAnnotation;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.ontology.ComparableTerm;
import org.jcvi.annotation.utils.BiojavaConvertor;

public class PeptideFastaExtractor implements Iterable<Sequence> {

	private RichSequenceIterator genbankSeqIO;
	private ArrayList<Sequence> proteinSeqs = new ArrayList<Sequence>();

	private static final ComparableTerm translationTerm = RichObjectFactory
			.getDefaultOntology().getOrCreateTerm("translation");

	public PeptideFastaExtractor(String genbankFileName) {
		super();
		BufferedReader br;
		Namespace ns = RichObjectFactory.getDefaultNamespace();

		try {
			br = new BufferedReader(new FileReader(genbankFileName));
			this.genbankSeqIO = RichSequence.IOTools.readGenbankDNA(br, ns);
		} catch (Exception be) {
			be.printStackTrace();
			System.exit(-1);
		}

		// Read all sequences in the file
		while (genbankSeqIO.hasNext()) {
			RichSequence gb;
			try {
				gb = genbankSeqIO.nextRichSequence();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// read CDS features to get protein sequence
			for (Iterator<org.biojava.bio.seq.Feature> i = gb
					.features(); i.hasNext();) {
				RichFeature feat = (RichFeature) i.next();
				if (! feat.getType().equals("CDS")) {
					continue; // skip anything but CDS features
				}
				RichAnnotation ann = (RichAnnotation) feat.getAnnotation();
				String featId = BiojavaConvertor.generateIdFromBiojavaFeature(
						gb, feat);
				String proteinSequence = null;
				for (Iterator<Note> it = ann.getNoteSet().iterator(); it
						.hasNext();) {
					Note note = it.next();
					if (note.getTerm().equals(translationTerm)) {
						proteinSequence = note.getValue().toString();
						break;
					}
				}
				if (proteinSequence != null) {
					try {
						Sequence proteinSeq = ProteinTools
								.createProteinSequence(proteinSequence, featId);
						proteinSeqs.add(proteinSeq);
					} catch (IllegalSymbolException ex) {
						System.err.println("Invalid symbols in translation of "
								+ featId + ": " + proteinSequence);
					}
				} else {
					System.err.println("CDS feature " + featId
							+ " has no sequence");
				}
			}
		}
	}

	@Override
	public Iterator<Sequence> iterator() {
		// TODO Auto-generated method stub
		return this.proteinSeqs.iterator();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (String filename : args) {
			PeptideFastaExtractor stream = new PeptideFastaExtractor(filename);
			for (Sequence seq : stream) {
				System.out.println(">"+seq.getName()+"\n"+seq.seqString());
			}
		}
	}

}
