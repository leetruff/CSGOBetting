package MatchInformation;
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
	String recommendedBet;
	int winner;
	Match relatedEGBMatch;
	Match relatedCSGLMatch;
	
	/**
	 * isSwitched ist nur fuer EGB Matches relevant(wird nur bei EGB matches gesetzt), welche einem Lounge Match zugeordnet wurden!
	 */
	public boolean isSwitched;
	Matchtyp typ;
	
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
		recommendedBet = "Noch nicht berechnet";
	}
	
		public void createRecommendedBetString(){
			switch(typ){
			case EGB:{
				if(relatedCSGLMatch == null){
					recommendedBet = "Keine Wette moeglich";
				}else{
					double allOdds[] = new double[4];
					allOdds[2] = Double.parseDouble(team1LoungeOdds);
					allOdds[3] = Double.parseDouble(team2LoungeOdds);
					if(isSwitched){
						allOdds[0] = Double.parseDouble(relatedCSGLMatch.getTeam2Odds());
						allOdds[1] = Double.parseDouble(relatedCSGLMatch.getTeam1Odds());
					}else{
						allOdds[0] = Double.parseDouble(relatedCSGLMatch.getTeam1Odds());
						allOdds[1] = Double.parseDouble(relatedCSGLMatch.getTeam2Odds());
					}
					processOdds(allOdds);
				}
			} break;
			case CSGOLounge:{
				if(relatedEGBMatch == null){
					recommendedBet = "Keine Wette moeglich";
				}else{
					double allOdds[] = new double[4];
					allOdds[0] = Double.parseDouble(team1LoungeOdds);
					allOdds[1] = Double.parseDouble(team2LoungeOdds);
					if(relatedEGBMatch.isSwitched){
						allOdds[2] = Double.parseDouble(relatedEGBMatch.getTeam2Odds());
						allOdds[3] = Double.parseDouble(relatedEGBMatch.getTeam1Odds());
					}else{
						allOdds[2] = Double.parseDouble(relatedEGBMatch.getTeam1Odds());
						allOdds[3] = Double.parseDouble(relatedEGBMatch.getTeam2Odds());
					}
					processOdds(allOdds);
				}
			} break;
			default:{
				System.out.println("Beim ENUM in der match klasse ist was schiefgegangen");
			} break;
			}
		}
	
		//check on what team one should bet and give the odds to the according functions
		private void processOdds(double[] allOdds){
			//calculate percent odds from rates
			double a[] = new double[]{allOdds[0], allOdds[1], allOdds[2], allOdds[3]};
			allOdds[0] = a[0]/(a[0]+a[1]);
			allOdds[1] = a[1]/(a[0]+a[1]);
			allOdds[2] = a[3]/(a[2]+a[3]);
			allOdds[3] = a[2]/(a[2]+a[3]);
			
			
			//calc new egb odds
			allOdds[2] = calculateNewOdds(allOdds)[0];
			allOdds[3] = calculateNewOdds(allOdds)[1];
			
			if(Double.isNaN(allOdds[0]) || Double.isNaN(allOdds[1])
					|| Double.isNaN(allOdds[2]) || Double.isNaN(allOdds[3]) 
					|| allOdds[0] > 1 || allOdds[2] > 1 || allOdds[0] < 0  || allOdds[2] < 0
					|| allOdds[1] > 1 || allOdds[3] > 1 || allOdds[1] < 0  || allOdds[3] < 0){
				setBetString("Error with Odds", 0);
				return;
			}
			
			if(allOdds[0] > allOdds[2]){
				//bet on lounge team2
				if(allOdds[1] < 0.09 || allOdds[1] > 0.7){
					setBetString("Skip", 0);
					return;
				}
				//setBetString(""+100*getKellyBet(allOdds[1], allOdds[3]) , 2);
				double temp = 100*getKellyBet(allOdds[1], allOdds[3]);
				temp = Math.round(temp * 100) / 100.0;
				setBetString(""+ temp, 2);
			}else{
				//bet on lounge team1
				if(allOdds[0] < 0.09 || allOdds[0] > 0.7){
					setBetString("Skip", 0);
					return;
				}
				double temp = 100*getKellyBet(allOdds[0], allOdds[2]);
				temp = Math.round(temp * 100) / 100.0;
				setBetString(""+ temp, 1);
			}
		}
		
		private void setBetString(String string, int team){
			switch(team){
				case 0:{
					switch(typ){
					case CSGOLounge:{
						recommendedBet = string;
					}break;
					case EGB:{
						relatedCSGLMatch.setRecommendedBet(string);
					}break;
					}
				}break;
				case 1:{
					switch(typ){
					case CSGOLounge:{
						recommendedBet = string + "% on Team " + team1Name;
					}break;
					case EGB:{
						relatedCSGLMatch.setRecommendedBet(string + "% on Team " + relatedCSGLMatch.getTeam1Name());
					}break;
					}
				}break;
				case 2:{
					switch(typ){
					case CSGOLounge:{
						recommendedBet = string + "% on Team " + team2Name;
					}break;
					case EGB:{
						relatedCSGLMatch.setRecommendedBet(string + "% on Team " + relatedCSGLMatch.getTeam2Name());
					}break;
					}
					
				}break;
			}
		}
		
		//EGB Odds einfuegen -> veraenderte Odds returnen
		private double[] calculateNewOdds(double[] oldOdds){
			double[] newOdds = new double[2];
			newOdds[0] = oldOdds[2] + funktion1(oldOdds[2], 0.7, 0.16);
			newOdds[1] = oldOdds[3] + funktion1(oldOdds[3], 0.7, 0.16);
			return newOdds;
		}

		//ganz normaler kelly rechner
		private double getKellyBet(Double loungeOdds, Double realOdds) {
			double kellyPercentage;
			double b;
			b = (1-loungeOdds)/loungeOdds;
			//System.out.println("Lounge Odds: " + loungeOdds + "   Real Odds: " + realOdds + "    b: " + b);
			kellyPercentage = (realOdds*(b + 1) - 1)/b;
			if(kellyPercentage > 0.1 && kellyPercentage <= 0.4){
				kellyPercentage *= 0.5;
			}else if(kellyPercentage > 0.4 && kellyPercentage <= 1){
				kellyPercentage *= 0.33333333;
			}
			return kellyPercentage;
		}
		
		//funktion um die egb odds zu verändern
		private double funktion1(double x, double multiplier, double shift){
			double y = x;
			x = x - 0.5;
			double k = (1.0/(4.0*shift))-0.5;
			double j = (0.25-0.5*shift)/(0.5-shift);
			if(x<=-shift){
				y=-j*x-0.5*j;
				y *= multiplier;
			}else if (x > -shift && x <= shift){
				y=k*x;
				y *= multiplier;
			}else if (x > shift && x <= 0.5){
				y=-j*x+0.5*j;
				y *= multiplier;
			}
			return y;
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
	
	public void setRelatedCSGLMatch(Match relMatch){
		relatedCSGLMatch = relMatch;
	}
	
	public Match getRelatedCSGLMatch(){
		return relatedCSGLMatch;
	}

	public void setSwitched(boolean switched){
		isSwitched = switched;
	}
	
	public boolean isSwitched(){
		return isSwitched;
	}
	
	

	public Matchtyp getTyp() {
		return typ;
	}

	public void setTyp(Matchtyp typ) {
		this.typ = typ;
	}

	public String getRecommendedBet() {
		return recommendedBet;
	}

	public void setRecommendedBet(String recommendedBet) {
		this.recommendedBet = recommendedBet;
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
	
	public void setMatchtyp(Matchtyp typ){
		this.typ = typ;
	}
	
	public Matchtyp getMatchtyp(){
		return typ;
	}





	
}
