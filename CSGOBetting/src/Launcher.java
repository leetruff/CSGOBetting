import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Scanner;


public class Launcher {
	public static void main(String[] args){
		//URL auslesen, bei CSGOL einfach nur scanner der den string aus der URL liest
		Scanner s = null;
		String matchString = null;
		URL url = null;
		try {
			url = new URL("http://csgolounge.com/api/matches");
			s = new Scanner(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(s != null){
			matchString = s.nextLine();
		}
		
		//matchArray enthält den nicht formattierten Info String zu jedem Match
		String[] matchArray = matchString.split("\\{");
		
		//selbes Spiel mit den Odds, welche von matches_stats kommen
		try {
			url = new URL("http://csgolounge.com/api/matches_stats");
			s = new Scanner(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(s != null){
			matchString = s.nextLine();
		}
		
		//matchArray2 enthält zu jedem Match die Odds, Ids stimmen mit matchArray überein
		String[] matchArray2 = matchString.split("\\{");
		
		//erstellen eines Files in C:\csgobetting\CSGOLoungeData.txt
		String path = "C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt";
		//(use relative path for Unix systems)
		File f = new File(path);
		//(works for both Windows and Linux)
		f.getParentFile().mkdirs();
		boolean fileExists=false;
		if(f.exists())
			fileExists = true;
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//erstellen eines PrintWriters im append=true modus um neue Games ins file einzutragen
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", true)))) {
			//updateStartingId wird dafür benutzt, nicht jeden eintrag aktualisieren zu müssen, sondern nur neue matches
			int updateStartingId = 1;
			int lastUnclosedMatch = 1;
			
			//falls das File erst angelegt wurde, Kopfzeile einfügen, ansonsten checken ab wann aktualisiert werden muss
			if(!fileExists){
				out.println("ID\tY\tM\tD\tH\tM\tTeam1\tT1Odds\tTeam2\tT2Odds\tWinner\tClosed\tEvent\tFormat;");
				for(int i=1; i<matchArray.length; i++){
					//suche erstes nicht geschlossenes match, ab da soll aktualisiert werden
					if(Integer.parseInt(matchArray[i].substring(matchArray[i].indexOf("closed")+9, matchArray[i].indexOf("closed")+10)) == 0){
						lastUnclosedMatch = i;
						System.out.println(matchArray[i]);
						break;
					}
				}
			}else{
				//check for new matches
				RandomAccessFile newFile = new RandomAccessFile("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", "rw");
				long length = f.length() - 1;
				byte b;
				do {                     
				  length -= 1;
				  newFile.seek(length);
				  b = newFile.readByte();
				} while(b != 10 && length > 0);
				byte[] byteTemp = new byte[(int) (newFile.length()-length-3)];
				newFile.readFully(byteTemp);
				newFile.close();
				updateStartingId = Integer.parseInt(new String(byteTemp, "UTF-8"));
				
				//delete last N lines, where N is the amount of matches that have to be updated
				newFile = new RandomAccessFile("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", "rw");
				length = f.length() - 1;
				//Setzt den filePointer genau N zeilen zurück, N = matchArray.length - updateStartingId+1
				for(int i=0; i<matchArray.length - updateStartingId+1;i++){
					do {                     
					  length -= 1;
					  newFile.seek(length);
					  b = newFile.readByte();
					} while(b != 10 && length > 0);
				}
				newFile.setLength(length+1);
				newFile.close();
				
				//Aktualisiere ab dem ersten nicht geschlossenem Match im alten Stand (updateStartingId) und trage das letzte
				//nicht geschlossene match nach dem neuen Stand in die Datei
				System.out.println(updateStartingId);
				for(int i=1; i<matchArray.length; i++){
					//suche erstes nicht geschlossenes match
					if(Integer.parseInt(matchArray[i].substring(matchArray[i].indexOf("closed")+9, matchArray[i].indexOf("closed")+10)) == 0){
						lastUnclosedMatch = i;
						System.out.println(lastUnclosedMatch);
						break;
					}
				}
				
			}
			
			//ab updateStartingId beginnen die matches zu aktualisieren
			for(int i=updateStartingId; i<matchArray.length; i++){
				//matchLine ist die formatierte Info Zeile die ins File eingetragen wird, alle Infos werden mit ; getrennt
				//Ausschneiden der Infos mit substring(indexof(anfang info), indexof(ende info))
				//beginne matchLine mit der matchId welche zwischen "match" und ","when" steht
		    	String matchLine = matchArray[i].substring(matchArray[i].indexOf("\"match\"")+9, matchArray[i].indexOf("\",\"when")) + ";";
		    	//lese einen Integer für die Id aus um in dem Odds array danach zu suchen
		    	int matchId = Integer.parseInt(matchLine.substring(0, matchLine.length()-1));
		    	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
		    	String temp = matchArray[i].substring(matchArray[i].indexOf("when")+7, matchArray[i].indexOf("\",\"a"));
		    	//teile das Datum in die Komponenten und hänge sie einzeln an
		    	matchLine += temp.substring(0, 4) +";"+ temp.substring(5, 7) +";"+ temp.substring(8, 10) +";"+ temp.substring(11, 13) +";" + temp.substring(14, 16) +";";
		    	//Name des ersten Teams
		    	temp = matchArray[i].substring(matchArray[i].indexOf("a\":\"")+4, matchArray[i].indexOf(",\"b\"")-1);
		    	matchLine += temp + ";";
		    	
		    	//get odds for team a, dummy variable wasSucc indicates if odds are availible for this match
		    	boolean wasSucc = false;
		    	for(int j=1; j<matchArray2.length; j++){
		    		//in matchArray2 stehen die Odds, aber nicht für jedes match gibt es odds deshalb die Kontrolle mit wasSucc
		    		if(Integer.parseInt(matchArray2[j].substring(matchArray2[j].indexOf("match")+8, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("match")+8))) == matchId){
		    			temp = matchArray2[j].substring(matchArray2[j].indexOf("a\":\"")+4, matchArray2[j].indexOf(",\"b")-1);
		    			matchLine+=temp+";";
		    			wasSucc = true;
		    		}
		    	}
		    	if(!wasSucc)
		    		matchLine += "0;";
		    	wasSucc = false;
		    	
		    	//Name des zweiten Teams
		    	temp = matchArray[i].substring(matchArray[i].indexOf("b\":\"")+4, matchArray[i].indexOf(",\"winner")-1);
		    	matchLine += temp + ";";
		    	
		    	//get odds for team b, dummy variable wasSucc indicates if odds are availible for this match
		    	wasSucc = false;
		    	for(int j=1; j<matchArray2.length; j++){
		    		//siehe Oben
		    		if(Integer.parseInt(matchArray2[j].substring(matchArray2[j].indexOf("match")+8, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("match")+8))) == matchId){
		    			temp = matchArray2[j].substring(matchArray2[j].indexOf("b\":\"")+4, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("b\":\"")+4));
		    			matchLine+=temp+";";
		    			wasSucc = true;
		    		}
		    	}
		    	if(!wasSucc)
		    		matchLine += "0;";
		    	wasSucc = false;
		    	
		    	//suche nach dem Winner string
		    	temp = matchArray[i].substring(matchArray[i].indexOf("winner")+9, matchArray[i].indexOf("winner")+10);
		    	//umformatierung von a,b,c nach 0,1,2 und -1 falls noch kein Ergebnis existiert
		    	switch(temp){
		    	case "a":matchLine += 1 + ";";break;
		    	case "b":matchLine += 2 + ";";break;
		    	case "c":matchLine += 0 + ";";break;
		    	case "\"":matchLine += -1 + ";";break;
		    	}
		    	//info ob das match geschlossen ist
		    	temp = matchArray[i].substring(matchArray[i].indexOf("closed")+9, matchArray[i].indexOf("closed")+10);
		    	matchLine += temp + ";";
		    	//info über das Turnier
		    	temp = matchArray[i].substring(matchArray[i].indexOf("event")+8, matchArray[i].indexOf(",\"format")-1);
		    	matchLine += temp + ";";
		    	//info über Bo1,3,5 etc
		    	temp = matchArray[i].substring(matchArray[i].indexOf("format")+9, matchArray[i].indexOf("format")+10);
		    	matchLine += temp;
		    	//schreiben der Zeile in die Datei
		    	out.println(matchLine);
		    }
			//Schreiben des aktuellsten Stands in die letzte Zeile der Datei um später von dort aus weiter machen zu können
			out.println(lastUnclosedMatch);
	    	System.out.println("fertig");
			out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Error");
		}
	}
}
