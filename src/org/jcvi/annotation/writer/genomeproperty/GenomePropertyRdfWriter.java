package org.jcvi.annotation.writer.genomeproperty;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.GenomePropertyFactory;

public class GenomePropertyRdfWriter implements GenomePropertyWriter {

	private String insPrefix = "urn:genome_properties:instances:";
	private String ontPrefix = "urn:genome_properties:ontology:";
	
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
		buffer.append(getFooter());
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
		buffer.append("\t<rdf:Description rdf:about=\"" + insPrefix + "GenomeProperty_" + p.getId() + "\">\n");
		buffer.append("\t\t<rdf:type rdf:resource=\"" + ontPrefix + "GenomeProperty\"/>\n");
		buffer.append("\t\t<id>" + p.getId() + "</id>\n");
		buffer.append("\t\t<accession>" + p.getAccession() + "</accession>\n");
		buffer.append("\t\t<category>" + p.getType() + "</category>\n");
		buffer.append("\t\t<threshold>" + p.getThreshold() + "</threshold>\n");
		
		if (!(p.getType().toUpperCase().equals("CATEGORY"))) {
			buffer.append("\t\t<state>" + p.getState().toString() + "</state>\n");
			buffer.append("\t\t<value>" + decimal.format(p.getValue()) + "</value>\n");
		}
		buffer.append("\t\t<title>" + p.getTitle() + "</title>\n");
		buffer.append("\t\t<definition>" + p.getDefinition() + "</definition>\n");
		
		GenomeProperty parent = p.getParent();
		if (parent != null) {
			buffer.append("\t\t<parent>" + parent.getId() + "</parent>\n");
		}
		List<GenomeProperty> children = p.getChildren();
		if (children != null) {
			buffer.append("\t\t<children> :\"");
			for (int i=0; i<children.size(); i++) {
				buffer.append(children.get(i).getId());
				if (i < children.size() - 1) {
					buffer.append(",");
				}
			}
			buffer.append("</children>\n");
		}
		buffer.append("\t</rdf:Description> \n");
		return buffer;
	}

	private StringBuffer getHeader() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<rdf:RDF\n");
		buffer.append("\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
		buffer.append("\txmlns=\"" + ontPrefix + "\"\n");
		buffer.append("\txmlns:gp=\"" + insPrefix + "\" >\n");
		return buffer;
	}
	private String getFooter() {
		return "</rdf:RDF>\n";
	}
}

