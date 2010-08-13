package org.jcvi.annotation.facts;

public enum RelationshipType {

		SUFFICIENT_FOR,
		REQUIRED_BY,
		PART_OF;

		public static RelationshipType getRelationshipType(String type) {
			if (type.equals("SUFFICIENT_FOR")) {
				return RelationshipType.SUFFICIENT_FOR;
			} 
			else if (type.equals("REQUIRED_BY")) {
				return RelationshipType.REQUIRED_BY;
			}
			else if (type.equals("PART_OF")) {
				return RelationshipType.PART_OF;
			}
			throw new AssertionError("Unknown RelationshipType: " + type);
		}
}
