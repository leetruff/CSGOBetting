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
		while(((line = reader.readLine()) != null) && line.contains(";")){
			
			try {
				if(line.startsWith("ID")){
					continue;
				}
				
				
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
				
				//Scheiss auf deprecated, solange es das tut was es tun soll
				jahr = "" + (Integer.parseInt(jahr) - 1900);
				@SuppressWarnings("deprecation")
				Date date = new Date(Integer.parseInt(jahr), Integer.parseInt(monat), Integer.parseInt(tag), Integer.parseInt(stunde), Integer.parseInt(minute), 0);
				Match match = new Match(matchID, team1, team2, event, Integer.parseInt(matchType), date, team1Odds, team2Odds);
				
				if(!(Integer.parseInt(team1Odds) == 0 || Integer.parseInt(team2Odds) == 0)){
					matchList.add(match);
				}
			} catch (Exception e) {
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
	
	public ArrayList<Match> getMatches(){
		return matchList;
	}
	
	/**
	 * 	Setzt die aktuelle {@link java.util.ArrayList Liste} auf die {@link java.util.ArrayList Liste} der {@link Match Matches}, die zu den Stichworten passt
	 * -> Fuehrt eine Suche nach {@link Match Matches} mit den gegebenen Begriffen durch
	 * 
	 * @param begriffe  Array mit den Begriffen, nach denen gesucht werden soll
	 */
	public void einfSuchListe(String... begriffe){
		ArrayList<Match> temp = new ArrayList<Match>();
		ArrayList<Match> matches = getMatches();
		
		for(int i = 0; i < matches.size(); i++){
			Match match = matches.get(i);
			
			if(match.containsBegriffe(begriffe))
				temp.add(matches.get(i));
		}
		
		aktuelleList = temp;
	}
	
	
}
