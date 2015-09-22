import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;





/**
 * 
 * Der {@link ListenController} verwaltet die aktuelle (-> idealerweise die aktuell angezeigte) {@link java.util.ArrayList Liste} von {@link Match Matches}
 * @author Lars
 *
 */
public class ListenController {

	private File file;
	String filepath = "C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt";
	BufferedReader reader = null;
	StringTokenizer tokenizer;
	ArrayList<Match> matchList;
	ArrayList<Match> aktuelleList;
	
	
	
	public ListenController() throws IOException{
		file = new File(filepath);
		
		
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Reader erstellen hats verkackt!");
		}
		
		//tokenizer = new StringTokenizer(str, ";");
		matchList = new ArrayList<Match>();
		aktuelleList = null;
		readMatchDateFromFile();
	}
	
	
	/**
	 * Liest unser MatchdatenFile und erstellt daraus eine {@link java.util.ArrayList Liste} von {@link Match Matches}
	 * @throws IOException
	 */
	private void readMatchDateFromFile() throws IOException{
		
		String line;
		/**
		 * Laeuft bis es die erste Zeile findet, welche kein Semikolon enthaelt (also exakt die letzte Zeile des Files)
		 */
		while(((line = reader.readLine()) != null) && line.contains(";")){
			
			try {
				/**
				 * Erste Zeile ueberspringen
				 */
				if(line.startsWith("ID")){
					continue;
				}
				
				/**
				 * String zerlegen und einzelne Werte auslesen
				 */
				tokenizer = new StringTokenizer(line, ";");
				String matchID = tokenizer.nextToken();
				String jahr = tokenizer.nextToken();
				String monat = tokenizer.nextToken();
				String tag = tokenizer.nextToken();
				String stunde = tokenizer.nextToken();
				String minute = tokenizer.nextToken();
				String team1 = tokenizer.nextToken();
				String team1Odds = tokenizer.nextToken();
				String team2 = tokenizer.nextToken();
				String team2Odds = tokenizer.nextToken();
				String winner = tokenizer.nextToken();
				String finished = tokenizer.nextToken();
				String event = tokenizer.nextToken();
				String matchType = tokenizer.nextToken();
				
				/**
				 * Jahr ist irgendwie immer mit 1900 initialisiert und wird komischerweise immer zur Eingabe hinzuaddiert.
				 * Deshalb 1900 subtrahieren.
				 */
				jahr = "" + (Integer.parseInt(jahr) - 1900);
				
				/**
				 * Date Objekt anlegen, ist zwar deprecated aber funktioniert noch super.
				 */
				@SuppressWarnings("deprecation")
				Date date = new Date(Integer.parseInt(jahr), Integer.parseInt(monat), Integer.parseInt(tag), Integer.parseInt(stunde), Integer.parseInt(minute), 0);
				
				/**
				 * Match Objekt mit entsprechenden Informationen anlegen.
				 */
				Match match = new Match(matchID, team1, team2, event, Integer.parseInt(matchType), date, team1Odds, team2Odds);
				
				match.setWinner(Integer.parseInt(winner));
				
				/**
				 * Hinzufuegen des Matches in unsere Archivliste, falls es keine 0er Odds enthaelt.
				 */
				if(!(Integer.parseInt(team1Odds) == 0 || Integer.parseInt(team2Odds) == 0)){
					matchList.add(match);
				}
			} 
			/**
			 * Jegliche Exception die geworfen werden kann, deutet darauf hin, dass die entsprechende
			 * Zeile des Files ein Match enthaelt, welches fehlerhafe Informationen enthaelt. Deshalb
			 * koennen wir dies einfach ueberspringen und nicht in unser Matcharchiv mit aufnehmen. 
			 */
			catch (Exception e) {
				continue;
			}
		}
		
	}
	
	/**
	 * main zum testen / debuggen
	 * @return
	 */
//	public static void main(String[] args) throws IOException{
//		ListenController lctrl = new ListenController();
//		ArrayList<Match> liste = lctrl.getMatches();
//	}
//	
	
	/**
	 * Getter fuer unsere {@link java.util.ArrayList Liste} von {@link Match Matches}
	 * @return Matchliste mit allen aus dem File ausgelesenen, gueltigen Matches.
	 */
	public ArrayList<Match> getMatches(){
		return matchList;
	}
	
	/**
	 * Getter fuer die aktuelle, durch die Suchfunktion gefilterte Liste.
	 * @return Gefilterte Liste, welche Matches enthaelt, die auf die Suchanfrage anspringen.
	 */
	public ArrayList<Match> getAktuelleListe(){
		return aktuelleList;
	}
	
	/**
	 * 	Setzt die aktuelle {@link java.util.ArrayList Liste} auf die {@link java.util.ArrayList Liste} der {@link Match Matches}, die zu den Stichworten passt
	 * -> Fuehrt eine Suche nach {@link Match Matches} mit den gegebenen Begriffen durch
	 * 
	 * @param begriffe  Array mit den Begriffen, nach denen gesucht werden soll
	 */
	public ArrayList<Match> einfSuchListe(String... begriffe){
		ArrayList<Match> temp = new ArrayList<Match>();
		ArrayList<Match> matches = getMatches();
		
		for(int i = 0; i < matches.size(); i++){
			Match match = matches.get(i);
			
			if(match.containsBegriffe(begriffe))
				temp.add(matches.get(i));
		}
		
		aktuelleList = temp;
		return aktuelleList;
	}
	
	
}
