package org.jcvi.annotation.facts;

public enum PropertyState {
	NONE_FOUND {
		public String toString() {
			return "None Found";
		}
	},
	NOT_SUPPORTED {
		public String toString() {
			return "Not Supported";
		}
	},
	SOME_EVIDENCE {
		public String toString() {
			return "Some Evidence";
		}
	},
	YES {
		public String toString() {
			return "Yes";
		}
	}
}
