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
	ArrayList<Match> list;
	
	
	
	public ListenController() throws IOException{
		file = new File(filepath);
		
		
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Reader erstellen hats verkackt!");
		}
		
		//tokenizer = new StringTokenizer(str, ";");
		list = new ArrayList<Match>();
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
				@SuppressWarnings("deprecation")
				Date date = new Date(Integer.parseInt(jahr), Integer.parseInt(monat), Integer.parseInt(tag), Integer.parseInt(stunde), Integer.parseInt(minute), 0);
				Match match = new Match(matchID, team1, team2, event, Integer.parseInt(matchType), date, team1Odds, team2Odds);
				
				list.add(match);
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
		return list;
	}
	
	
}
