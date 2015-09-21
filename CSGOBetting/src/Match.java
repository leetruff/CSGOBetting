import java.util.Date;


/**
 * Modelliert ein Match mit allen wichtigen Informationen
 * @author Lars
 *
 */
public class Match {

	String loungeID;

	String team1Name;
	String team2Name;
	String eventName;
	int matchType;
	Date datum;
	String team1LoungeOdds;
	String team2LoungeOdds;
	String team1EGBOdds;
	String team2EGBOdds;
	int winner;
	
	/**
	 * Konstruktor fuer ein {@link Match}, welcher die Matchspezifischen Eigenschaften setzt
	 */
	public Match(String id, String team1Name, String team2Name, String eventName, int matchType, Date datum, 
			String team1LoungeOdds, String team2LoungeOdds){
		
		this.loungeID = id;
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
	
	public String getLoungeID() {
		return loungeID;
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
	
	public Date getDatum() {
		return datum;
	}
	
	public String getTeam1LoungeOdds() {
		return team1LoungeOdds;
	}
	
	public String getTeam2LoungeOdds() {
		return team2LoungeOdds;
	}
	
	public String getTeam1EGBOdds() {
		return team1EGBOdds;
	}
	
	public String getTeam2EGBOdds() {
		return team2EGBOdds;
	}


	/**
	 * Hilfsmethode, sucht nach Stichworten in einigen Eigenschaften des {@link Match Matches}
	 * @param begriffe Die zu suchenden Stichworte
	 * @return True oder False (vorhanden oder nicht vorhanden)
	 */
	public boolean containsBegriffe(String[] begriffe) {

		for(int i = 0; i < begriffe.length; i++){
			
			if(team1Name.equals(begriffe[i]))
				return true;
			if(team2Name.equals(begriffe[i]))
				return true;
			if(eventName.equals(begriffe[i]))
				return true;
		}
		
		return false;
	}
	
}
