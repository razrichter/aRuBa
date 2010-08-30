package org.jcvi.annotation.writer.genomeproperty;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.GenomePropertyFactory;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.facts.RelationshipType;

public class GenomePropertyTextWriter implements GenomePropertyWriter {

	@Override
	public String write() {
		return write(GenomePropertyFactory.getProperties());
	}

	@Override
	public String write(Collection<GenomeProperty> properties) {
		StringBuffer buffer = new StringBuffer();
		for (GenomeProperty p : properties) {
			buffer.append(writeToBuffer(p));
		}
		return buffer.toString();
	}
	
	@Override
	public String write(GenomeProperty p) {
		return writeToBuffer(p).toString();
	}
	
	public StringBuffer writeToBuffer(GenomeProperty p) {
		DecimalFormat decimal = new DecimalFormat("0.000");
		
		StringBuffer buf = new StringBuffer();
		buf.append("-----------------------------------------------------------------------------------\n");
		buf.append(String.format("%-7s", "Id") + "\t" +
				String.format("%-15s","Accession") + "\t" +
				String.format("%-15s","Type") + "\t" +
				String.format("%-15s","State") + "\t" +
				String.format("%-7s","Value") + "\t" +
				String.format("%-50s","Title") + "\n");
		buf.append("-----------------------------------------------------------------------------------\n");
		buf.append(String.format("%-7s", p.getId()) + "\t" +
				String.format("%-15s",p.getAccession()) + "\t" +
				String.format("%-15s",p.getType()) + "\t" +
				String.format("%-15s",p.getState()) + "\t" +
				String.format("%-7s",decimal.format(p.getValue())) + "\t" +
				p.getTitle());
		buf.append("\n\n");
		
		// Required evidence (steps)
		StringBuffer bufferSteps = new StringBuffer();
		HashMap<RelationshipType, HashSet<Property>> relationships = p.getChildRelationships();
		for (Entry<RelationshipType, HashSet<Property>> typeProperties : relationships.entrySet()) {
			for (Property property : typeProperties.getValue()) {
				bufferSteps.append(String.format("%-7s", property.getId()) + "\t" +
						String.format("%-7s", decimal.format(property.getValue())) + "\t" +
						String.format("%-15s",typeProperties.getKey().toString()) + "\t" +
						property.getTitle() + "\n");
			}
		}
		if (bufferSteps.length() > 0) {
			
			buf.append("-----------------------------------------------------------------------------------\n");
			buf.append(String.format("%-7s", "StepId") + "\t" +
					String.format("%-7s","Value") + "\t" +
					String.format("%-15s","Relationship") + "\t" +
					String.format("%-50s","Subject") + "\n");
			
			buf.append("-----------------------------------------------------------------------------------\n");
			buf.append(bufferSteps);
			buf.append("\n");
		}
	
		buf.append("-----------------------------------------------------------------------------------\n");
		buf.append("Definition\n");
		buf.append("-----------------------------------------------------------------------------------\n");
		buf.append(stringToParagraph(p.getDefinition()));
		buf.append("\n");
		
		return buf;	
	}
	
	private String stringToParagraph(String text) {
		return stringToParagraph(text, 80);
	}

	private String stringToParagraph(String text, int lineLength) {
		String[] words = text.split(" ");
		int numWords = words.length;
		int i = 0;
		StringBuffer buf = new StringBuffer();
		while (i < numWords) {
			StringBuffer bufLine = new StringBuffer();
			while (i < numWords && bufLine.length() + words[i].length() <= lineLength) {
				bufLine.append(" " + words[i].trim());
				i++;
			}
			bufLine.append("\n");
			buf.append(bufLine);
		}
		return buf.toString();
	}

}
