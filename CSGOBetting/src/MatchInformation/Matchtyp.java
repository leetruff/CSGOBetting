package MatchInformation;

/**
 * 
 * @author Lars
 *
 */
public enum Matchtyp {
	/**
	 * Ein CSGOLounge Match
	 */
	CSGOLounge,
	/**
	 * Ein Egamingbets Match
	 */
	EGB;

	
	/**
	 * Gibt das Enum als String zurueck
	 * @return String
	 */
	@Override
	public String toString(){
		switch(this){
			case CSGOLounge: return "CSGOLounge";
			case EGB: return "Egamingbets";
			default: return null;
		}
	}
}
