package org.jcvi.annotation.writer.factory;

import org.jcvi.annotation.writer.genomeproperty.GenomePropertyDetailedTextDAGWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyN3Writer;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyRdfWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyTextDAGWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyTextWriter;
import org.jcvi.annotation.writer.genomeproperty.GenomePropertyWriter;

public abstract class GenomePropertyWriterFactory {

	public enum GenomePropertiesFormat {
		TEXT,
		DAG,
		DETAILED,
		N3,
		RDF,
		XML;
		
		public static GenomePropertiesFormat getFormat(String format) {
			GenomePropertiesFormat formatType = null;
			if (format != null) {
				format = format.toUpperCase();
				if (format.equals("TEXT")) {
					return GenomePropertiesFormat.TEXT;
				} 
				else if (format.equals("DETAILED")) {
					return GenomePropertiesFormat.DETAILED;
				}
				else if (format.equals("DAG")) {
					return GenomePropertiesFormat.DAG;
				}
				else if (format.equals("N3")) {
					return GenomePropertiesFormat.N3;
				}	
				else if (format.equals("RDF")) {
						return GenomePropertiesFormat.RDF;
				}
				else if (format.equals("XML")) {
					return GenomePropertiesFormat.RDF;
				}
				throw new AssertionError("Unknown Genome Properties Format: " + format);
			}
			return formatType;
		}
	}
	
	// Any DAOFactory should implement the following method(s)
	// public abstract FeatureDAO getFeatureDAO();
	
	public static GenomePropertyWriter getWriter(GenomePropertiesFormat format) {
		
		switch(format) {
			case TEXT:
				return new GenomePropertyTextWriter();
			
			case DAG:
				return new GenomePropertyTextDAGWriter();

			case DETAILED:
				return new GenomePropertyDetailedTextDAGWriter();
				
			case N3:
				return new GenomePropertyN3Writer();

			case RDF:
				return new GenomePropertyRdfWriter();
			
			case XML:
				return new GenomePropertyRdfWriter();
				
			default:
				return new GenomePropertyTextWriter();
		}
	}

}
