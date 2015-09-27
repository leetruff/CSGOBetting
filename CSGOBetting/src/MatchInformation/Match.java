package MatchInformation;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Modelliert ein Match mit allen wichtigen Informationen
 * @author Lars
 *
 */
public class Match {

	String id;

	String team1Name;
	String team2Name;
	String eventName;
	int matchType;
	GregorianCalendar datum;
	String team1LoungeOdds;
	String team2LoungeOdds;
	String team1EGBOdds;
	String team2EGBOdds;
	int winner;
	Match relatedEGBMatch;
	public boolean isSwitched;
	
	/**
	 * Konstruktor fuer ein {@link Match}, welcher die Matchspezifischen Eigenschaften setzt
	 */
	public Match(String id, String team1Name, String team2Name, String eventName, int matchType, GregorianCalendar datum, 
			String team1LoungeOdds, String team2LoungeOdds){
		
		this.id = id;
		this.team1Name = team1Name;
		this.team2Name = team2Name;
		this.eventName = eventName;
		this.matchType = matchType;
		this.datum = datum;
		this.team1LoungeOdds = team1LoungeOdds;
		this.team2LoungeOdds = team2LoungeOdds;
	}
	
	
	
	/**
	 * 
	 * 
	 * Getters
	 */
	
	public String getID() {
		return id;
	}
	
	public String getTeam1Name() {
		return team1Name;
	}
	
	public String getTeam2Name() {
		return team2Name;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public int getMatchType() {
		return matchType;
	}
	
	public GregorianCalendar getDatum() {
		return datum;
	}
	
	public String getTeam1Odds() {
		return team1LoungeOdds;
	}
	
	public String getTeam2Odds() {
		return team2LoungeOdds;
	}
	
	public String getTeam1EGBOdds() {
		return team1EGBOdds;
	}
	
	public String getTeam2EGBOdds() {
		return team2EGBOdds;
	}
	
	public void setRelatedEGBMatch(Match relMatch){
		relatedEGBMatch = relMatch;
	}
	
	public Match getRelatedEGBMatch(){
		return relatedEGBMatch;
	}

	public void setSwitched(boolean switched){
		isSwitched = switched;
	}
	
	public boolean isSwitched(){
		return isSwitched;
	}

	/**
	 * Hilfsmethode, sucht nach Stichworten in einigen Eigenschaften des {@link Match Matches}
	 * @param begriffe Die zu suchenden Stichworte
	 * @return True oder False (vorhanden oder nicht vorhanden)
	 */
	public boolean containsBegriffe(String[] begriffe) {

		for(int i = 0; i < begriffe.length; i++){
			
			if(team1Name.equalsIgnoreCase(begriffe[i]))
				return true;
			if(team2Name.equalsIgnoreCase(begriffe[i]))
				return true;
			if(eventName.toLowerCase().contains(begriffe[i]))
				return true;
			if(eventName.contains(begriffe[i]))
				return true;
		}
		
		return false;
	}

	public boolean containsTeams(String[] begriffe) {
		
		if(  (team1Name.equalsIgnoreCase(begriffe[0]) && team2Name.equalsIgnoreCase(begriffe[2])) || 
				(team2Name.equalsIgnoreCase(begriffe[0]) && team1Name.equalsIgnoreCase(begriffe[2])) ){
			
			return true;
		}
		
		return false;
	}

	
	public boolean containsTeamsEvent(String[] begriffe) {

		if(  ((team1Name.equalsIgnoreCase(begriffe[0]) && team2Name.equalsIgnoreCase(begriffe[2])) || 
				(team2Name.equalsIgnoreCase(begriffe[0]) && team1Name.equalsIgnoreCase(begriffe[2])) ) && 
				(eventName.toLowerCase().contains(begriffe[4]) || eventName.contains(begriffe[4])) ){
			return true;
		}
		
		return false;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}



	public int getWinner() {
		return winner;
	}





	
}
