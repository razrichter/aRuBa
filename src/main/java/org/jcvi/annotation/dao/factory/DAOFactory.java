package org.jcvi.annotation.dao.factory;

import org.jcvi.annotation.dao.FeatureDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;

public abstract class DAOFactory {
	
	// Types of DAOs supported by the factory
	public static final int SMALLGENOME = 1;
	public static final int GENBANK = 2;
	public static final int BLASTFILE = 3;
	public static final int HMMFILE = 4;
	
	// Any DAOFactory should implement the following method(s)
	public abstract FeatureDAO getFeatureDAO();
	
	public static DAOFactory getDAOFactory(int factoryName) {
		
		switch(factoryName) {
			case SMALLGENOME:
				return new SmallGenomeDAOFactory();
			
			/*
			case GENBANK:
				return new GenbankDAOFactory();
			case BLASTFILE:
				return new BlastDAOFactory();
			case HMMFILE:
				return new HmmDAOFactory();
			*/
				
			default:
				throw new IllegalArgumentException("DAOFactory does not currently support " + factoryName);
		}
	}

}
