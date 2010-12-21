package org.jcvi.annotation.writer.genomeproperty;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.facts.RelationshipType;

public class GenomePropertyDetailedTextDAGWriter extends GenomePropertyTextDAGWriter {

	private StringBuffer nodeToString(GenomeProperty p, int level) {
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferIndent = new StringBuffer();
		while (level-- > 0) {
			bufferIndent.append("   ");
		}

		buffer.append(p.getId() + "\t" + String.format("%-80s", bufferIndent.toString() + p.getTitle()) + "\t"
				+ String.format("%-12s", p.getType().toUpperCase()));
		
		DecimalFormat decimal = new DecimalFormat("0.000");
		if (!(p.getType().toUpperCase().equals("CATEGORY"))) {
			buffer.append("\t" + String.format("%-15s", p.getState().toString()) + "\t" + decimal.format(p.getValue()));
		}
		buffer.append("\n");
		
		buffer.append(nodeEvidence(p, bufferIndent));
		return buffer;
	}

	private StringBuffer nodeEvidence(GenomeProperty p, StringBuffer bufferIndent) {
		// Show the required evidence for a Genome Property
		StringBuffer buffer = new StringBuffer();
		DecimalFormat decimal = new DecimalFormat("0.000");
		
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
		
		StringBuffer buf = new StringBuffer();
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
		return buf;

	}
	
	private Object nodeEvidence(GenomeProperty p, int level) {
		StringBuffer bufferIndent = new StringBuffer();
		while (level-- > 0) {
			bufferIndent.append("   ");
		}
		return nodeEvidence(p, bufferIndent);
	}
}
