package org.jcvi.annotation.writer.genomeproperty;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.GenomePropertyFactory;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.facts.RelationshipType;

public class GenomePropertyTextDAGWriter implements GenomePropertyWriter {

	@Override
	public String write(GenomeProperty p) {
		return write(p, 0);
	}

	@Override
	public String write(Collection<GenomeProperty> properties) {
		return writeToStringBuffer(properties).toString();
	}

	// By default, we start at the root, and write all of the children
	public String write() {
		return write(GenomePropertyFactory.getRootProperty().getChildren());
	}
	
	public StringBuffer writeToStringBuffer(Collection<GenomeProperty> properties) {
		StringBuffer buffer = new StringBuffer();
		for (GenomeProperty p : properties) {
			buffer.append(write(p));
		}
		return buffer;
	}


	public String write(GenomeProperty p, int level) {
		return writeToStringBuffer(p, level, new StringBuffer()).toString();
	}
	
	// Depth-first traversal of a property
	public StringBuffer writeToStringBuffer(GenomeProperty p, int level, StringBuffer buffer) {
		buffer.append(this.nodeToString(p, level));
		List<GenomeProperty> children = p.getChildren();
		if (children != null) {
			Iterator<GenomeProperty> iter = children.listIterator();
			level++;
			while (iter.hasNext()) {
				GenomeProperty child = iter.next();
				writeToStringBuffer(child, level, buffer);
			}
		}
		return buffer;
	}

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
		
		// Testing
		// buffer.append(nodeEvidence(p, bufferIndent));
		
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
				bufferSteps.append(property.getId() + "\t" + String.format("%-80s", bufferIndent.toString() + property.getTitle() + " " 
						+ typeProperties.getKey().toString()
						+ " " + p.getTitle()) + "\n");
			}
		}
		return bufferSteps;
	}
}
