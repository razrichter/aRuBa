package org.jcvi.annotation.dao;
import java.util.Iterator;

import org.jcvi.annotation.facts.BlastHit;

public interface BlastDAO extends Iterable<BlastHit> {
	Iterator<BlastHit> iterator();

}
