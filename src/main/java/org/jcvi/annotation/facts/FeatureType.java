package org.jcvi.annotation.facts;

public class FeatureType {
	public static final int ORF = 1;
	public static final int tRNA = 2;
	
	public static boolean isValidType (int type) {
		boolean isValid = false;
		if (type >= ORF && type <= tRNA) { // TODO write check correctly
			isValid = true;
		}
		return isValid;
	}
}
