package org.jcvi.annotation.writer.genomeproperty;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.GenomePropertyFactory;

public class GenomePropertyN3Writer implements GenomePropertyWriter {

	private String gpPrefix = "gp";
	
	@Override
	public String write(GenomeProperty p) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getHeader());
		return writeToStringBuffer(p, buffer).toString();
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
		buffer.append(getHeader());
		for (GenomeProperty p : properties) {
			buffer.append(write(p));
		}
		return buffer;
	}

	// Depth-first traversal of a property
	public StringBuffer writeToStringBuffer(GenomeProperty p, StringBuffer buffer) {
		buffer.append(this.nodeToString(p));
		List<GenomeProperty> children = p.getChildren();
		if (children != null) {
			Iterator<GenomeProperty> iter = children.listIterator();
			while (iter.hasNext()) {
				GenomeProperty child = iter.next();
				writeToStringBuffer(child, buffer);
			}
		}
		return buffer;
	}
	
	private StringBuffer nodeToString(GenomeProperty p) {
		StringBuffer buffer = new StringBuffer();
		
		
		// Report our Genome Property
		DecimalFormat decimal = new DecimalFormat("0.000");
		buffer.append(gpPrefix + ":GenomeProperty_" + p.getId() + "\n");
		buffer.append("\ta :GenomeProperty;\n");
		buffer.append("\t:id :\"" + p.getId() + "\";\n");
		buffer.append("\taccession :\"" + p.getAccession() + "\";\n");
		buffer.append("\t:category :\"" + p.getType() + "\";\n");
		buffer.append("\t:threshold :\"" + p.getThreshold() + "\";\n");
		if (!(p.getType().toUpperCase().equals("CATEGORY"))) {
			buffer.append("\t:state :\"" + p.getState().toString() + "\";\n");
			buffer.append("\t:value :\"" + decimal.format(p.getValue()) + "\";\n");
		}
		buffer.append("\t:title :\"" + p.getTitle() + "\";\n");
		buffer.append("\t:definition :\"" + p.getDefinition() + "\";\n");
		
		GenomeProperty parent = p.getParent();
		if (parent != null) {
			buffer.append("\t:parent :\"" + parent.getId() + "\";\n");
		}
		List<GenomeProperty> children = p.getChildren();
		if (children != null) {
			buffer.append("\t:children :\"");
			for (int i=0; i<children.size(); i++) {
				buffer.append(children.get(i).getId());
				if (i < children.size() - 1) {
					buffer.append(",");
				}
			}
			buffer.append("\";\n");
		}
		buffer.append(".\n");
		return buffer;
	}

	private StringBuffer getHeader() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("@prefix " + gpPrefix + ": <urn:genome_properties:instances:> .\n");
		buffer.append("@prefix : <urn:genome_properties:ontology:> .\n");
		return buffer;
	}
}
