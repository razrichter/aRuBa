package org.jcvi.annotation
import org.jcvi.annotation.Aruba;

public class GenomeProperties {

	static void main(def args) {
		
		Iterator iter = args.iterator()
		
		if (iter.hasNext()) {
			Aruba a = new Aruba()
			a.addGenomeProperties()
			a.addSmallGenome iter.next()
			a.run()
		} else {
			System.out.println("A Small Genome database is required");
		}
	}
}
