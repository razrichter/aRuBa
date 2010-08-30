package org.jcvi.annotation.writer.genomeproperty;
import java.util.Collection;

import org.jcvi.annotation.facts.GenomeProperty;

public interface GenomePropertyWriter {
	public String write( GenomeProperty property );
	public String write( Collection<GenomeProperty> properties );
	public String write(); // Writes all properties using factory cache
}
