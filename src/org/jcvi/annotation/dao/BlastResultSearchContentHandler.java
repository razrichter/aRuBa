package org.jcvi.annotation.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.biojava.bio.search.SearchContentAdapter;
import org.jcvi.annotation.facts.BlastHit;


public class BlastResultSearchContentHandler extends SearchContentAdapter {
	// states
    private enum State {
        START, END, START_SEARCH, END_SEARCH, START_HIT, END_HIT,
        START_SUBHIT, END_SUBHIT, START_IGNORED_SUBHIT, END_IGNORED_SUBHIT
    }

	private static HashMap<State, Set<State>> stateMap = new HashMap<State, Set<State>>();
	static {
		stateMap.put(State.START, new HashSet<State>(Arrays.asList(State.START_SEARCH,
		        State.END)));
		stateMap.put(State.START_SEARCH, new HashSet<State>(Arrays.asList(
		        State.START_HIT, State.END_SEARCH)));
		stateMap.put(State.START_HIT, new HashSet<State>(Arrays.asList(
		        State.START_SUBHIT, State.END_HIT)));
		stateMap.put(State.START_SUBHIT, new HashSet<State>(Arrays
				.asList(State.END_SUBHIT)));
		stateMap.put(State.END_SUBHIT, new HashSet<State>(Arrays.asList(
		        State.START_IGNORED_SUBHIT, State.END_HIT)));
		stateMap.put(State.START_IGNORED_SUBHIT, new HashSet<State>(Arrays
				.asList(State.END_IGNORED_SUBHIT)));
		stateMap.put(State.END_IGNORED_SUBHIT, new HashSet<State>(Arrays.asList(
		        State.START_IGNORED_SUBHIT, State.END_HIT)));
		stateMap.put(State.END_HIT, new HashSet<State>(Arrays.asList(State.START_HIT,
		        State.END_SEARCH)));
		stateMap.put(State.END_SEARCH, new HashSet<State>(Arrays.asList(
		        State.START_SEARCH, State.END)));
	}

	private ArrayList<BlastHit> blastHits = new ArrayList<BlastHit>();
	private String resultProgram;
	private String resultQueryId;
	private Integer resultQueryLength;
	@SuppressWarnings("unused")
    private String resultDatabase;
	@SuppressWarnings("unused")
    private Integer resultDatabaseLength;
    @SuppressWarnings("unused")
    private Integer hitNumIdentical;
    private Integer hitNumSimilar;

    private boolean isFirstSubHit = true;
	private BlastHit workingHit;
	private State state = State.START;

	private void checkStateIs(State testState) {
	    if (! state.equals(testState)) {
	        throw new RuntimeException("Invalid State: "+state+". Expected: "+testState);
	    }
	}
	
	private void checkValidTransition(State newState) {
		boolean valid = isValidTransition(state,newState);
        if ( ! valid ) {
            throw new RuntimeException("Invalid State Transition: " + state +" to " + newState);
        }
	}
	private boolean isValidTransition (State oldState, State newState) {
		Set<State> validTransitions = stateMap.get(oldState);
		boolean valid = validTransitions.contains(newState);
		return valid;
	}
	
	public ArrayList<BlastHit> getBlastHits() {
		return blastHits;
	}

	// SAX content handler methods / state machine transitions
	public void startHit() {
        // // System.out.println("startHit()");
		checkValidTransition(State.START_HIT);
		state = State.START_HIT;
		isFirstSubHit = true;
		this.workingHit = new BlastHit();
		workingHit.setProgram(resultProgram);
		workingHit.setQueryId(resultQueryId);
		workingHit.setQueryLength(resultQueryLength);
		
	}

	public void endHit() {
        // // System.out.println("endHit()");
		checkValidTransition(State.END_HIT);
		blastHits.add(workingHit);
		state = State.END_HIT;
	}

	public void startSubHit() {
		if (isFirstSubHit) {
            // // System.out.println("startSubHit()");
            checkValidTransition(State.START_SUBHIT);
			state = State.START_SUBHIT;
		}
		else {
            // // System.out.println("startSubHit() [ignored]");
            checkValidTransition(State.START_IGNORED_SUBHIT);
			state = State.START_IGNORED_SUBHIT;
		}
	}

	public void endSubHit() {
		if (isFirstSubHit) {
            // // System.out.println("endSubHit()");
            checkValidTransition(State.END_SUBHIT);
            if ( hitNumSimilar != null && workingHit.getAlignmentLength() != 0) {
                workingHit.setPercentSimilarity(100.0 * hitNumSimilar.doubleValue() / workingHit.getAlignmentLength());
            }
			state = State.END_SUBHIT;
			isFirstSubHit = false;
		}
		else {
            // System.out.println("endSubHit() [ignored]");
			checkValidTransition(State.END_IGNORED_SUBHIT);
			state = State.END_IGNORED_SUBHIT;
		}
	}
	

	public void startSearch() {
        // System.out.println("startSearch");
	    checkValidTransition(State.START_SEARCH);
		state = State.START_SEARCH;
	}

	public void endSearch() {
        // System.out.println("endSearch");
	    checkValidTransition(State.END_SEARCH);
	    state = State.END_SEARCH;
	}

    public void setQueryID(String queryID) {
        // System.out.println("\tQueryID:\t " + queryID);
        checkStateIs(State.START_SEARCH);
        resultQueryId = queryID;
    }

    public void setDatabaseID(String databaseID) {
        // System.out.println("\tDatabaseID: " + databaseID);
        checkStateIs(State.START_SEARCH);
        // TODO parse database size out of database id (and clean up database id info, generally)
        resultDatabase = databaseID;
    }

    public void addSearchProperty(Object key, Object val) {
        // System.out.println("\tSearchProp:\t" + key + ": " + val);
	    checkStateIs(State.START_SEARCH);
	    String strKey = key.toString();
	    if (strKey.equals("program")) {
	        this.resultProgram = val.toString();
	    }
	    else if (strKey.equals("version")) {
	        // do nothing
	    }
	    else if (strKey.equals("queryLength")) {
	        this.resultQueryLength = Integer.parseInt(val.toString());
	    }
	    else {
            throw new RuntimeException("Unknown Search Property key: "+strKey);
        }
        
	}

    public void addHitProperty(Object key, Object val) {
        // System.out.println("\tHitProp:\t" + key + ": " + val);
        checkStateIs(State.START_HIT);
        String strKey = key.toString();
        if (strKey.equals("subjectId")) {
            workingHit.setHitId(val.toString());
        }
        else if (strKey.equals("subjectSequenceLength")) {
            workingHit.setHitLength(Integer.parseInt(val.toString()));
        }
        else if (strKey.equals("subjectDescription")) {
            // do nothing
        }
        else {
            throw new RuntimeException("Unknown Hit Property key: "+strKey);
        }
        
    }

    public void addSubHitProperty(Object key, Object val) {
        // // System.out.println("\tSubHitProp:\t" + key + ": " + val);
	    if (isFirstSubHit) {
	        checkStateIs(State.START_SUBHIT);
	        String strKey = key.toString();
	        if (strKey.equals("score")) {
	            workingHit.setBitScore(Double.parseDouble(val.toString()));
	        }
	        else if (strKey.equals("expectValue")) {
	            String strVal = val.toString();
	            if (strVal.charAt(0) == 'e') {
	                strVal = "1"+strVal;
	            }
	            workingHit.setEValue(Double.parseDouble(strVal));
	        }
	        else if (strKey.equals("numberOfIdentities")) {
	            hitNumIdentical = Integer.parseInt(val.toString());
	        }
	        else if (strKey.equals("alignmentSize")) {
	            workingHit.setAlignmentLength(Integer.parseInt(val.toString()));
	        }
	        else if (strKey.equals("percentageIdentity")) {
	            workingHit.setPercentIdentity(Double.parseDouble(val.toString()));
	        }
	        else if (strKey.equals("numberOfPositives")) {
	            hitNumSimilar = Integer.parseInt(val.toString());
	        }
	        else if (strKey.equals("querySequenceStart")) {
	            workingHit.setQueryStart(Integer.parseInt(val.toString()));
	        }
	        else if (strKey.equals("querySequenceEnd")) {
	            workingHit.setQueryEnd(Integer.parseInt(val.toString()));
            }
	        else if (strKey.equals("querySequence")) {
	            // do nothing
            }
	        else if (strKey.equals("subjectSequenceStart")) {
	            workingHit.setHitStart(Integer.parseInt(val.toString()));
            }
	        else if (strKey.equals("subjectSequenceEnd")) {
	            workingHit.setHitEnd(Integer.parseInt(val.toString()));
            }
	        else if (strKey.equals("subjectSequence")) {
	            // do nothing
            }
	        else {
	            throw new RuntimeException("Unknown SubHit Property key: "+strKey);
	        }
	    }
	    else {
            // // System.out.println("\tSubHitProp [Ignored]:\t" + key + ": " + val);
	        checkStateIs(State.START_IGNORED_SUBHIT);
	    }
	}

}
