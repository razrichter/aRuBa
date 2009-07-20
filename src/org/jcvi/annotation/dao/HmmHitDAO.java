package org.jcvi.annotation.dao;
import java.util.Iterator;
import org.jcvi.annotation.facts.HmmHit;

public interface HmmHitDAO extends Iterable<HmmHit> {
	Iterator<HmmHit> iterator();
}
