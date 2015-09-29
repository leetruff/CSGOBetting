package MatchInformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class that contains all needed functions to gather match information and to update files. To use it, just create an object and call the
 * required functions.
 * @author Tobi
 *
 */
public class MatchInformation {
	/**
	 * Creates and/or updates a file in "C:\csgobetting\CSGOLoungeData.txt" with all saved matches from CSGO Lounge. For every match
	 * a line in the text file is created and formatted like this:
	 * "ID;Y;M;D;H;M;Team1;T1Odds;Team2;T2Odds;Winner;Closed;Event;Format;" <br>
	 * The last line is just a number indicating the LINE NUMBER IN THE FILE with the last unclosed match and doesn't contain a ";".
	 */
	public void createLoungeFile(){
		//URL auslesen, bei CSGOL einfach nur scanner der den string aus der URL liest
		Scanner s = null;
		String matchString = null;
		URL url = null;
		try {
			url = new URL("http://csgolounge.com/api/matches");
			s = new Scanner(url.openStream());
			if(s != null){
				matchString = s.nextLine();
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Höchstwahrscheinlich Timeout bei Serveranfrage, stackTrace printen lassen");
			return;
		}
		
		//matchArray enth�lt den nicht formattierten Info String zu jedem Match
		String[] matchArray = matchString.split("\\{");
		
		//selbes Spiel mit den Odds, welche von matches_stats kommen
		try {
			url = new URL("http://csgolounge.com/api/matches_stats");
			s = new Scanner(url.openStream());
			if(s != null){
				matchString = s.nextLine();
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Höchstwahrscheinlich Timeout bei Serveranfrage, stackTrace printen lassen");
			s.close();
			return;
		}
		
		//matchArray2 enth�lt zu jedem Match die Odds, Ids stimmen mit matchArray �berein
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
		
		//updateStartingId wird daf�r benutzt, nicht jeden eintrag aktualisieren zu m�ssen, sondern nur neue matches
		int updateStartingId = 1;
		int lastUnclosedMatch = 1;
		
		long timer = System.currentTimeMillis();
		
		//falls das File erst angelegt wurde, Kopfzeile einf�gen, ansonsten checken ab wann aktualisiert werden muss
		if(!fileExists){
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", true)))) {
				out.println("ID;Y;M;D;H;M;Team1;T1Odds;Team2;T2Odds;Winner;Closed;Event;Format;");
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Schreiben von CSGOLounge Data fehlgeschlagen");
			}
			/*for(int i=1; i<matchArray.length; i++){
				//suche erstes nicht geschlossenes match, ab da soll aktualisiert werden
				if(Integer.parseInt(matchArray[i].substring(matchArray[i].indexOf("closed")+9, matchArray[i].indexOf("closed")+10)) == 0){
					lastUnclosedMatch = i;
					//System.out.println(matchArray[i]);
					break;
				}
			}*/
		}else{
			try{
				File loungeFile = new File("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt");
				FileReader fReader = new FileReader(loungeFile);
				BufferedReader bReader = new BufferedReader(fReader);
				FileWriter fWriter = new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeDataNew.txt", true);
				BufferedWriter bWriter = new BufferedWriter(fWriter);
				PrintWriter out = new PrintWriter(bWriter);
				//Schreibe erste Zeile die nur Kopfdaten enthält manuell
				String lineT = bReader.readLine();
				out.println(lineT);
				for(String line; (line = bReader.readLine()) != null; ) {
					if(line.split(";")[11].equals("0")){
						lastUnclosedMatch = Integer.parseInt(line.split(";")[0]);
						break;
					}
					out.println(line);
				}
				out.close();
				bWriter.close();
				fWriter.close();
				bReader.close();
				fReader.close();
				loungeFile = null;
			}catch(Exception e){
				System.out.println("Bei der aktualisierung von CSGOloungefile ist etwas passiert");
				e.printStackTrace();
			}
			//TempFile zum Main File machen und main file löschen
			File loungeFile = new File("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt");
			File tempFile = new File("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeDataNew.txt");
			if(loungeFile.exists()){
				System.gc();
				//System.out.println("LoungeData (alte datei) löschen erfolgreich: "+loungeFile.delete());
				try {
					Files.delete(FileSystems.getDefault().getPath("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("LoungeDataNew in LoungeData umbenennen erfolgreich: " + tempFile.renameTo(loungeFile));
			
			for(int i=1; i<matchArray.length; i++){
				//suche erstes nicht geschlossenes match
				if(Integer.parseInt(matchArray[i].substring(9,matchArray[i].indexOf("\",\"", 9))) == lastUnclosedMatch){
					System.out.println("Check für Neueinstiegspunkt: " + lastUnclosedMatch);
					updateStartingId = i;
					break;
				}
			}
			
			//check for new matches
			/*RandomAccessFile newFile = new RandomAccessFile("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", "rw");
			long length = newFile.length() - 1;
			byte b;
			do {                     
			  length -= 1;
			  newFile.seek(length);
			  b = newFile.readByte();
			} while(b != 10 && length > 0);
			byte[] byteTemp = new byte[(int) (newFile.length()-length-3)];
			newFile.readFully(byteTemp);
			newFile.close();
			System.out.println("Letzte Zeile in CSGOLoungeData falls schon existent: " + new String(byteTemp, "UTF-8"));
			updateStartingId = Integer.parseInt(new String(byteTemp, "UTF-8"));
			
			//delete last N lines, where N is the amount of matches that have to be updated
			newFile = new RandomAccessFile("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", "rw");
			length = newFile.length() - 1;
			//Setzt den filePointer genau N zeilen zur�ck, N = matchArray.length - updateStartingId+1
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
			}*/
			
		}
		
		//erstellen eines PrintWriters im append=true modus um neue Games ins file einzutragen
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", true)))) {
			
			
			//ab updateStartingId beginnen die matches zu aktualisieren
			for(int i=updateStartingId; i<matchArray.length; i++){
				//matchLine ist die formatierte Info Zeile die ins File eingetragen wird, alle Infos werden mit ; getrennt
				//Ausschneiden der Infos mit substring(indexof(anfang info), indexof(ende info))
				//beginne matchLine mit der matchId welche zwischen "match" und ","when" steht
		    	String matchLine = matchArray[i].substring(matchArray[i].indexOf("\"match\"")+9, matchArray[i].indexOf("\",\"when")) + ";";
		    	//lese einen Integer f�r die Id aus um in dem Odds array danach zu suchen
		    	int matchId = Integer.parseInt(matchLine.substring(0, matchLine.length()-1));
		    	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
		    	String temp = matchArray[i].substring(matchArray[i].indexOf("when")+7, matchArray[i].indexOf("\",\"a"));
		    	//teile das Datum in die Komponenten und h�nge sie einzeln an
		    	matchLine += temp.substring(0, 4) +";"+ temp.substring(5, 7) +";"+ temp.substring(8, 10) +";"+ temp.substring(11, 13) +";" + temp.substring(14, 16) +";";
		    	//Name des ersten Teams
		    	temp = matchArray[i].substring(matchArray[i].indexOf("a\":\"")+4, matchArray[i].indexOf(",\"b\"")-1);
		    	matchLine += temp + ";";
		    	
		    	//get odds for team a, dummy variable wasSucc indicates if odds are availible for this match
		    	boolean wasSucc = false;
		    	for(int j=1; j<matchArray2.length; j++){
		    		//in matchArray2 stehen die Odds, aber nicht f�r jedes match gibt es odds deshalb die Kontrolle mit wasSucc
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
		    	//info �ber das Turnier
		    	temp = matchArray[i].substring(matchArray[i].indexOf("event")+8, matchArray[i].indexOf(",\"format")-1);
		    	matchLine += temp + ";";
		    	//info �ber Bo1,3,5 etc
		    	temp = matchArray[i].substring(matchArray[i].indexOf("format")+9, matchArray[i].indexOf("format")+10);
		    	matchLine += temp;
		    	//schreiben der Zeile in die Datei
		    	out.println(matchLine+";");
		    }
			//Schreiben des aktuellsten Stands in die letzte Zeile der Datei um sp�ter von dort aus weiter machen zu k�nnen
			out.println(lastUnclosedMatch);
	    	System.out.println("Lounge File erstellung+update fertig");
			out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Error");
		}
		
		System.out.println("Dauer des File aktualisierens: " + (System.currentTimeMillis() - timer) + "ms");
	}

	/**
	 * Creates and/or updates a file in "C:\csgobetting\EGBData.txt" with all saved matches from EGB.com. For every match
	 * a line in the text file is created and formatted like this:
	 * "ID;Y;M;D;H;M;Team1;T1Odds;Team2;T2Odds;Winner;Closed;Event;" <br>
	 * The last line is just a number indicating the LAST CHECKED API ID from EGB.com and doesn't contain a ";".
	 */
	public void createEGBFile(){
		//erstellen eines Files in C:\csgobetting\CSGOLoungeData.txt
		String path = "C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt";
		String path2 = "C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt";
		//(use relative path for Unix systems)
		File f = new File(path);
		//(works for both Windows and Linux)
		f.getParentFile().mkdirs();
		boolean fileExists=false;
		if(f.exists())
			fileExists = true;
		//(use relative path for Unix systems)
		f = new File(path2);
		//(works for both Windows and Linux)
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//falls das File erst angelegt wurde, Kopfzeile einf�gen
		int ID = 0;
		if(!fileExists){
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt", true)))) {
				out.println("ID;Y;M;D;H;M;Team1;T1Odds;Team2;T2Odds;Winner;Closed;Event;");
				out.close();}
			catch(IOException e){
				System.out.println("Error");
			}
			ID = 0;
		}
		
		//unclosed matches aktualisieren
		File inputFile = new File("C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt");
		File tempFile = new File("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt");
		if(fileExists){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

				String currentLine;
				while((currentLine = reader.readLine()) != null) {
				    // trim newline when comparing with lineToRemove
					currentLine.trim();
					String[] currentLineArray = currentLine.split(";");
					//führe für offene Wette durch
					if(currentLineArray.length > 2 && !currentLineArray[11].equals("Closed") &&Integer.parseInt(currentLineArray[11]) == 0){
						URL url = null;
						try {
							url = new URL("http://egb.com/ajax.php?key=modules_home_view_ViewBets&type=modules&ind=home&ajax=view&act=ViewBets&id="+currentLineArray[0]+"&is_navi=0");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						URLConnection connection = null;
						try {
							connection = url.openConnection();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
						InputStream is = null;
						while(true){
							try {
								is = connection.getInputStream();
								break;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								continue;
							}
						}
						BufferedReader buffreader = new BufferedReader( new InputStreamReader( is )  );
						String line = null;
						String matchInfo = null;
				        try {
				            while( ( line = buffreader.readLine() ) != null )  {
				            	//debug
				            	//System.out.println(line);
				            	matchInfo = line;
				            	String matchLine = matchInfo.substring(matchInfo.indexOf("id\":")+5, matchInfo.indexOf("\",", matchInfo.indexOf("id\":")+5)) + ";";
						    	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
						    	String temp = matchInfo.substring(matchInfo.indexOf("bet_time")+11, matchInfo.indexOf("\",", matchInfo.indexOf("bet_time")+11));
						    	String monthTemp = null;
						    	switch(temp.substring(0, 3)){
						    	case "Jan":monthTemp = "01";break;
						    	case "Feb":monthTemp = "02";break;
						    	case "Mar":monthTemp = "03";break;
						    	case "Apr":monthTemp = "04";break;
						    	case "May":monthTemp = "05";break;
						    	case "Jun":monthTemp = "06";break;
						    	case "Jul":monthTemp = "07";break;
						    	case "Aug":monthTemp = "08";break;
						    	case "Sep":monthTemp = "09";break;
						    	case "Oct":monthTemp = "10";break;
						    	case "Nov":monthTemp = "11";break;
						    	case "Dec":monthTemp = "12";break;
						    	}
						    	//teile das Datum in die Komponenten und h�nge sie einzeln an
						    	matchLine += temp.substring(7, 11) +";"+ monthTemp +";"+ temp.substring(4, 6) +";"+ temp.substring(12, 14) +";" + temp.substring(15, 17) +";";
						    	//Name des ersten Teams
						    	temp = matchInfo.substring(matchInfo.indexOf("gamer_1")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_1")+10));
						    	matchLine += temp + ";";
						    	//odds des ersten Teams
						    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6));
						    	if(temp.equals(""))
						    		temp = "0";
						    	matchLine += temp + ";";
						    	
						    	//Name des zweiten Teams
						    	temp = matchInfo.substring(matchInfo.indexOf("gamer_2")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_2")+10));
						    	matchLine += temp + ";";
						    	//odds des zweiten Teams
						    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6));
						    	if(temp.equals(""))
						    		temp = "0";
						    	matchLine += temp + ";";
						    	
						    	//winner checken und eintragen
						    	if(matchInfo.indexOf("Match is over") == -1 && matchInfo.indexOf("Match was cancelled") == -1){
						    		//wette ist noch offen
						    		matchLine +="-1;0;";
						    	}else{
						    		//wette ist closed
						    		if(matchInfo.indexOf("Match was cancelled") != -1){
						    			matchLine +="0;1;";
						    		}else{
						    			String score = matchInfo.substring(matchInfo.indexOf("["), matchInfo.indexOf("]")+1);
						    			if(!score.contains(":")){
						    				//System.out.println(""+matchInfo.indexOf("[", matchInfo.indexOf("[")+1));
						    				score = matchInfo.substring(matchInfo.indexOf("[", matchInfo.indexOf("[")+1), matchInfo.indexOf("]", matchInfo.indexOf("[", matchInfo.indexOf("[")+1))+1);
						    			};
						    			//System.out.println(score);
						    			int pointsTeam2 = Integer.parseInt(score.substring(score.indexOf(": ")+2, score.indexOf("]")));
						    			int pointsTeam1 = Integer.parseInt(score.substring(1, score.indexOf(" : ")));
						    			if(pointsTeam1 > pointsTeam2){
						    				matchLine +="1;1;";
						    			}else if(pointsTeam1 == pointsTeam2){
						    				matchLine +="0;1;";
						    			}else{
						    				matchLine +="2;1;";
						    			}
						    		}
						    	}
						    	
						    	//tournament
						    	temp = matchInfo.substring(matchInfo.indexOf("tour")+7, matchInfo.indexOf("\",",matchInfo.indexOf("tour")+7));
						    	matchLine += temp + ";";
							    writer.write(matchLine + System.getProperty("line.separator"));
						    	
						    	//System.out.println(matchLine);
				            }
				        } catch (IOException e) {
				            // TODO Auto-generated catch block
				        	System.out.println("Error beim Zeilen auslesen");
				        	e.printStackTrace();
				        	//break;
				        }
				    	continue;
				    }
					//kopiere infos einer geschlossenen Wette
				    writer.write(currentLine + System.getProperty("line.separator"));
				    if(currentLineArray.length > 2 && !currentLineArray[0].equals("ID")){
				    	ID = Integer.parseInt(currentLineArray[0]);
				    }else if(!currentLine.contains(";")){
				    	ID=Integer.parseInt(currentLine.trim());
				    }
				}
				writer.close(); 
				reader.close(); 
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("Offene matches aktualisiert");
		}
		if(fileExists){
			//delete last line when file wasnt just created
			RandomAccessFile newFile;
			try {
				newFile = new RandomAccessFile("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt", "rw");
				long length = newFile.length() - 1;
				byte b;
				do {                     
				  length -= 1;
				  newFile.seek(length);
				  b = newFile.readByte();
				} while(b != 10 && length > 0);
				newFile.setLength(length+1);
				newFile.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error");
				e.printStackTrace();
			}
		}
		
		//Suche nach neuen Files
		while(true){
			ID += 1;
			//ajax here
			//http://egb.com/ajax.php?key=modules_home_update_UpdateBetsInfo&type=modules&ind=home&ajax=update&act=UpdateBetsInfo&id=99208
			URL url = null;
			try {
				url = new URL("http://egb.com/ajax.php?key=modules_home_view_ViewBets&type=modules&ind=home&ajax=view&act=ViewBets&id="+ID+"&is_navi=0");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URLConnection connection = null;
			try {
				connection = url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
			InputStream is = null;
			try {
				is = connection.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ID -= 1;
				e.printStackTrace();
				continue;
			}
			BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
			String line = null;
			String matchInfo = null;
	        try {
	            while( ( line = reader.readLine() ) != null )  {
	            	//debug
	            	//System.out.println(line);
	            	matchInfo = line;
	            }
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	        	System.out.println("Error beim Zeilen auslesen");
	        	e.printStackTrace();
	        	//break;
	        }
	        
	        //wenn "success":false dann mit der loop abbrechen -> fertig
	        if(matchInfo.substring(matchInfo.indexOf("success")+9, matchInfo.indexOf(",",matchInfo.indexOf("success")+9)).equals("false")){
	        	//es k�nnen auch zwischendrin falses sein, MUSS ERKANNT WERDEN
	        	boolean doesContinue = false;
	        	int sizeOfGap = 0;
	        	for(int k=1;k<20;k++){
	        		URL urlt = null;
					try {
						urlt = new URL("http://egb.com/ajax.php?key=modules_home_view_ViewBets&type=modules&ind=home&ajax=view&act=ViewBets&id="+(ID+k)+"&is_navi=0");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					URLConnection connectiont = null;
					try {
						connectiont = urlt.openConnection();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					connectiont.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
					InputStream ist = null;
					while(true){
						try {
							ist = connectiont.getInputStream();
							break;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
		        	}
					BufferedReader readert = new BufferedReader( new InputStreamReader( ist )  );
					String linet = null;
					String matchInfot = null;
			        try {
			            while( ( linet = readert.readLine() ) != null )  {
			            	//debug
			            	System.out.print("gap. ");
			            	matchInfot = linet;
			            	if(!matchInfot.substring(matchInfot.indexOf("success")+9, matchInfot.indexOf(",",matchInfot.indexOf("success")+9)).equals("false")){
			            		doesContinue = true;
			            		sizeOfGap = k -1;
			            	}
			            }
			        } catch (IOException e) {
			            // TODO Auto-generated catch block
			        	System.out.println("Error beim Zeilen auslesen");
			        	e.printStackTrace();
			        	break;
			        }
			        if(doesContinue)
			        	break;
	        	}
	        	if(doesContinue){
	        		ID += sizeOfGap;
	        		continue;
	        	}else{
	        		System.out.println("ende erreicht");
	        		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt", true)))) {
						out.println((ID-1));
						out.close();}
					catch(IOException e){
						System.out.println("Error");
					}
	        		break;
	        	}
	        }
			
			//erstellen eines PrintWriters im append=true modus um neue Games ins file einzutragen
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt", true)))) {

		        
		        if(!matchInfo.substring(matchInfo.indexOf("game\"")+7, matchInfo.indexOf("\",", matchInfo.indexOf("game\"")+7)).equals("Counter-Strike")){
		        	continue;
		        }
		        if(matchInfo.contains("s6")){
		        	//System.out.println("Hilfswette");
		        	continue;
		        }
		        
				//ab updateStartingId beginnen die matches zu aktualisieren
				//matchLine ist die formatierte Info Zeile die ins File eingetragen wird, alle Infos werden mit ; getrennt
				//Ausschneiden der Infos mit substring(indexof(anfang info), indexof(ende info))
				//beginne matchLine mit der matchId welche zwischen "match" und ","when" steht
		    	String matchLine = matchInfo.substring(matchInfo.indexOf("id\":")+5, matchInfo.indexOf("\",", matchInfo.indexOf("id\":")+5)) + ";";
		    	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
		    	String temp = matchInfo.substring(matchInfo.indexOf("bet_time")+11, matchInfo.indexOf("\",", matchInfo.indexOf("bet_time")+11));
		    	String monthTemp = null;
		    	switch(temp.substring(0, 3)){
		    	case "Jan":monthTemp = "01";break;
		    	case "Feb":monthTemp = "02";break;
		    	case "Mar":monthTemp = "03";break;
		    	case "Apr":monthTemp = "04";break;
		    	case "May":monthTemp = "05";break;
		    	case "Jun":monthTemp = "06";break;
		    	case "Jul":monthTemp = "07";break;
		    	case "Aug":monthTemp = "08";break;
		    	case "Sep":monthTemp = "09";break;
		    	case "Oct":monthTemp = "10";break;
		    	case "Nov":monthTemp = "11";break;
		    	case "Dec":monthTemp = "12";break;
		    	}
		    	//teile das Datum in die Komponenten und h�nge sie einzeln an
		    	matchLine += temp.substring(7, 11) +";"+ monthTemp +";"+ temp.substring(4, 6) +";"+ temp.substring(12, 14) +";" + temp.substring(15, 17) +";";
		    	//Name des ersten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("gamer_1")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_1")+10));
		    	matchLine += temp + ";";
		    	//odds des ersten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6));
		    	if(temp.equals(""))
		    		temp = "0";
		    	matchLine += temp + ";";
		    	
		    	//Name des zweiten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("gamer_2")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_2")+10));
		    	matchLine += temp + ";";
		    	//odds des zweiten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6));
		    	if(temp.equals(""))
		    		temp = "0";
		    	matchLine += temp + ";";
		    	
		    	//winner checken und eintragen
		    	if(matchInfo.indexOf("Match is over") == -1 && matchInfo.indexOf("Match was cancelled") == -1){
		    		//wette ist noch offen
		    		matchLine +="-1;0;";
		    	}else{
		    		//wette ist closed
		    		if(matchInfo.indexOf("Match was cancelled") != -1){
		    			matchLine +="0;1;";
		    		}else{
		    			String score = matchInfo.substring(matchInfo.indexOf("["), matchInfo.indexOf("]")+1);
		    			if(!score.contains(":")){
		    				//System.out.println(""+matchInfo.indexOf("[", matchInfo.indexOf("[")+1));
		    				score = matchInfo.substring(matchInfo.indexOf("[", matchInfo.indexOf("[")+1), matchInfo.indexOf("]", matchInfo.indexOf("[", matchInfo.indexOf("[")+1))+1);
		    			};
		    			//System.out.println(score);
		    			int pointsTeam2 = Integer.parseInt(score.substring(score.indexOf(": ")+2, score.indexOf("]")));
		    			int pointsTeam1 = Integer.parseInt(score.substring(1, score.indexOf(" : ")));
		    			if(pointsTeam1 > pointsTeam2){
		    				matchLine +="1;1;";
		    			}else if(pointsTeam1 == pointsTeam2){
		    				matchLine +="0;1;";
		    			}else{
		    				matchLine +="2;1;";
		    			}
		    		}
		    	}
		    	
		    	//tournament
		    	temp = matchInfo.substring(matchInfo.indexOf("tour")+7, matchInfo.indexOf("\",",matchInfo.indexOf("tour")+7));
		    	matchLine += temp + ";";
		    	out.println(matchLine);
		    	
		    	//System.out.println(matchLine);
		    	
		    	/*//get odds for team b, dummy variable wasSucc indicates if odds are availible for this match
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
		    	//info �ber das Turnier
		    	temp = matchArray[i].substring(matchArray[i].indexOf("event")+8, matchArray[i].indexOf(",\"format")-1);
		    	matchLine += temp + ";";
		    	//info �ber Bo1,3,5 etc
		    	temp = matchArray[i].substring(matchArray[i].indexOf("format")+9, matchArray[i].indexOf("format")+10);
		    	matchLine += temp;
		    	//schreiben der Zeile in die Datei
		    	out.println(matchLine);
				//Schreiben des aktuellsten Stands in die letzte Zeile der Datei um sp�ter von dort aus weiter machen zu k�nnen
				out.println(lastUnclosedMatch);
		    	System.out.println("fertig");*/
				out.close();
			}catch (IOException e) {
			    //exception handling left as an exercise for the reader
				System.out.println("Error");
			}
		}
		//TempFile zum Main File machen und main file löschen
		inputFile = new File("C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt");
		tempFile = new File("C:"+File.separator+"csgobetting"+File.separator+"EGBDataNew.txt");
		if(inputFile.exists()){
			System.gc();
			//System.out.println("EGBData (alte datei) löschen erfolgreich: "+inputFile.delete());
			try {
				Files.delete(FileSystems.getDefault().getPath("C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("EGBDataNew in EGBData umbenennen erfolgreich: " + tempFile.renameTo(inputFile));
	}

	/**
	 * Creates a text file in "C:\csgobetting\linklistopen.txt" with ID pairs from Lounge and EGB.com that are representing the same match
	 * according to name comparison with a team pseudonym list. This list should be updated regularly to increase the detection rate. <br>
	 * The format is "IDLounge;IDEGB;isMatchSwitched;" where the last number indicates if Team1 on Lounge is also Team1 on EGB (=0 if it is). <br>
	 * This function uses a helper function called checkForMatch(String[], String[], List<String[]>) which performs the actual comparison.
	 */
	public void createOpenBetLinkList(){
		List<String[]> pseudonyme = new ArrayList<String[]>();
		
		//erstellen eines Files in C:\csgobetting\linklist.txt
		String path = "C:"+File.separator+"csgobetting"+File.separator+"linklistopen.txt";
		//(use relative path for Unix systems)
		File f = new File(path);
		if(f.exists()){
			System.gc();
			System.out.println("linklistopen.txt löschen erfolgreich: " + f.delete());
		}
		//(works for both Windows and Linux)
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Lege weitere ArrayLists um die verarbeitung schneller zu gestalten
		List<String[]> egblist = new ArrayList<String[]>();
		List<String[]> csgollist = new ArrayList<String[]>();
		//Erstelle ArrayList mit wichtigen match Daten von EGB
		try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt")))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.contains("T1Odds") || line.contains("(") || line.contains(")"))
		    		continue;
		    	if(!line.contains(";"))
		    		break;
		    	String[] tempStringArray = line.split(";");
		    	if(!tempStringArray[11].equals("0"))
		    		continue;
		    	egblist.add(new String[]{tempStringArray[0],tempStringArray[1],tempStringArray[2],tempStringArray[3],tempStringArray[6].toLowerCase(),
		    			tempStringArray[8].toLowerCase()});
		    }
		    br.close();
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.out.println("Error link file file read");
			e.printStackTrace();
		}
		//Erstelle ArrayList mit wichtigen match Daten von CSGOL
		try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt")))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.contains("T1Odds"))
		    		continue;
		    	if(!line.contains(";"))
		    		break;
		    	String[] tempStringArray = line.split(";");
		    	if(!tempStringArray[11].equals("0"))
		    		continue;
		    	csgollist.add(new String[]{tempStringArray[0],tempStringArray[1],tempStringArray[2],tempStringArray[3],tempStringArray[6].toLowerCase(),
		    			tempStringArray[8].toLowerCase()});
		    }
		    br.close();
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.out.println("Error link file file read");
			e.printStackTrace();
		}
		
		//erstelle das Linklist file
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"linklistopen.txt", true)))) {
			//Erstelle ArrayList mit pseudonymarrays für Teams mit mehr als einem Namen
			try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"pseudonymfinal.txt")))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	String[] tempString = line.split(";");
			    	if(tempString.length > 1){
			    		pseudonyme.add(tempString);
			    	}
			    }
			    br.close();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Fehler im pseudonymfinal");
			}
			
			int einstiegsPunktArrayId = 0;
			//suche nach verbindung ab dem einstiegspunkt
			for(int i = einstiegsPunktArrayId; i < csgollist.size(); i++){
				for(int j = 0; j<egblist.size(); j++){
					int[] matchedDetails = new int[4];
					matchedDetails = checkForMatch(csgollist.get(i),egblist.get(j), pseudonyme);
					if(matchedDetails[0] == 1){
						//Write the details (index 1 and 2) in linklist.txt
						out.println(""+matchedDetails[1]+";"+matchedDetails[2]+";"+matchedDetails[3]);
					}
				}
			}
			
			out.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("error");
		}
	}

	/**
	 * Creates a text file in "C:\csgobetting\linklistclosed.txt" with ID pairs from Lounge and EGB.com that are representing the same match
	 * according to name comparison with a team pseudonym list. This list should be updated regularly to increase the detection rate. <br>
	 * The format is "IDLounge;IDEGB;isMatchSwitched;" where the last number indicates if Team1 on Lounge is also Team1 on EGB (=0 if it is). <br>
	 * This function uses a helper function called checkForMatch(String[], String[], List<String[]>) which performs the actual comparison.
	 */
	public void createClosedBetLinkList(){
		List<String[]> pseudonyme = new ArrayList<String[]>();
		
		//erstellen eines Files in C:\csgobetting\linklist.txt
		String path = "C:"+File.separator+"csgobetting"+File.separator+"linklistclosed.txt";
		//(use relative path for Unix systems)
		File f = new File(path);
		//(works for both Windows and Linux)
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Lege weitere ArrayLists um die verarbeitung schneller zu gestalten
		List<String[]> egblist = new ArrayList<String[]>();
		List<String[]> csgollist = new ArrayList<String[]>();
		//Erstelle ArrayList mit wichtigen match Daten von EGB
		try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"EGBData.txt")))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.contains("T1Odds") || line.contains("(") || line.contains(")"))
		    		continue;
		    	if(!line.contains(";"))
		    		break;
		    	String[] tempStringArray = line.split(";");
		    	if(!tempStringArray[11].equals("1"))
		    		continue;
		    	egblist.add(new String[]{tempStringArray[0],tempStringArray[1],tempStringArray[2],tempStringArray[3],tempStringArray[6].toLowerCase(),
		    			tempStringArray[8].toLowerCase()});
		    }
		    br.close();
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.out.println("Error link file file read");
			e.printStackTrace();
		}
		//Erstelle ArrayList mit wichtigen match Daten von CSGOL
		try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt")))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.contains("T1Odds"))
		    		continue;
		    	if(!line.contains(";"))
		    		break;
		    	String[] tempStringArray = line.split(";");
		    	if(!tempStringArray[11].equals("1"))
		    		continue;
		    	csgollist.add(new String[]{tempStringArray[0],tempStringArray[1],tempStringArray[2],tempStringArray[3],tempStringArray[6].toLowerCase(),
		    			tempStringArray[8].toLowerCase()});
		    }
		    br.close();
		}catch(Exception e){
			// TODO Auto-generated catch block
			System.out.println("Error link file file read");
			e.printStackTrace();
		}
		
		//suche nach neuem Einstiegspunkt um die liste zu aktualisieren
		int einstiegsPunkt = 1;
		try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"linklistclosed.txt")))) {
			for(String line; (line = br.readLine()) != null; ) {
		    	einstiegsPunkt = Integer.parseInt(line.split(";")[0]);
		    }
		    br.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Fehler im linklistclosed.txt");
		}
		
		//erstelle das Linklist file
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"linklistclosed.txt", true)))) {
			//Erstelle ArrayList mit pseudonymarrays für Teams mit mehr als einem Namen
			try(BufferedReader br = new BufferedReader(new FileReader(new File("C:"+File.separator+"csgobetting"+File.separator+"pseudonymfinal.txt")))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	String[] tempString = line.split(";");
			    	if(tempString.length > 1){
			    		pseudonyme.add(tempString);
			    	}
			    }
			    br.close();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Fehler im pseudonymfinal");
			}
			
			//suche ArrayListId von dem Einstiegspunkt
			int einstiegsPunktArrayId = 0;
			for(int i = 0; i<csgollist.size(); i++){
				if(einstiegsPunkt == Integer.parseInt(csgollist.get(i)[0])){
					System.out.println("Einstiegspunkt "+einstiegsPunkt+" csgolistid "+csgollist.get(i)[0]);
					einstiegsPunktArrayId = i+1;
					break;
				}
			}
			//suche nach verbindung ab dem einstiegspunkt
			for(int i = einstiegsPunktArrayId; i < csgollist.size(); i++){
				for(int j = 0; j<egblist.size(); j++){
					int[] matchedDetails = new int[4];
					matchedDetails = checkForMatch(csgollist.get(i),egblist.get(j), pseudonyme);
					if(matchedDetails[0] == 1){
						//Write the details (index 1 and 2) in linklist.txt
						out.println(""+matchedDetails[1]+";"+matchedDetails[2]+";"+matchedDetails[3]);
					}
				}
			}
			
			out.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("error");
		}
	}

	/**
	 * Fetches a single match from Lounge and returns a String with the formatted match info. Format is exactly like with createLoungeFile().
	 * 
	 * @param requestedMatchID ID of the required match
	 */
	public String getLoungeMatchInfo(int requestedMatchID){
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
		
		//matchArray enth�lt den nicht formattierten Info String zu jedem Match
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
			s.close();
		}else{
			System.out.println("Scanner is null");
			return "Error";
		}
		int arrayLine = 0;
		//matchArray2 enth�lt zu jedem Match die Odds, Ids stimmen mit matchArray �berein
		String[] matchArray2 = matchString.split("\\{");
		for(int i=1;i<matchArray.length;++i){
			if(Integer.parseInt(matchArray[i].substring(matchArray[i].indexOf("match")+8, matchArray[i].indexOf("\",\"", matchArray[i].indexOf("match")+8))) == requestedMatchID){
				arrayLine = i;
			}
		}
		//beginne matchLine mit der matchId welche zwischen "match" und ","when" steht
	   	String matchLine = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("\"match\"")+9, matchArray[arrayLine].indexOf("\",\"when")) + ";";
	   	//lese einen Integer f�r die Id aus um in dem Odds array danach zu suchen
	   	int matchId = Integer.parseInt(matchLine.substring(0, matchLine.length()-1));
	   	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
	   	String temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("when")+7, matchArray[arrayLine].indexOf("\",\"a"));
	   	//teile das Datum in die Komponenten und h�nge sie einzeln an
	   	matchLine += temp.substring(0, 4) +";"+ temp.substring(5, 7) +";"+ temp.substring(8, 10) +";"+ temp.substring(11, 13) +";" + temp.substring(14, 16) +";";
	   	//Name des ersten Teams
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("a\":\"")+4, matchArray[arrayLine].indexOf(",\"b\"")-1);
	   	matchLine += temp + ";";
		    	
	   	//get odds for team a, dummy variable wasSucc indicates if odds are availible for this match
	   	boolean wasSucc = false;
	   	for(int j=1; j<matchArray2.length; j++){
	   		//in matchArray2 stehen die Odds, aber nicht f�r jedes match gibt es odds deshalb die Kontrolle mit wasSucc
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
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("b\":\"")+4, matchArray[arrayLine].indexOf(",\"winner")-1);
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
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("winner")+9, matchArray[arrayLine].indexOf("winner")+10);
	   	//umformatierung von a,b,c nach 0,1,2 und -1 falls noch kein Ergebnis existiert
	   	switch(temp){
	   	case "a":matchLine += 1 + ";";break;
	   	case "b":matchLine += 2 + ";";break;
	   	case "c":matchLine += 0 + ";";break;
	   	case "\"":matchLine += -1 + ";";break;
	   	}
	   	//info ob das match geschlossen ist
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("closed")+9, matchArray[arrayLine].indexOf("closed")+10);
	   	matchLine += temp + ";";
	   	//info �ber das Turnier
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("event")+8, matchArray[arrayLine].indexOf(",\"format")-1);
	   	matchLine += temp + ";";
	   	//info �ber Bo1,3,5 etc
	   	temp = matchArray[arrayLine].substring(matchArray[arrayLine].indexOf("format")+9, matchArray[arrayLine].indexOf("format")+10);
	   	matchLine += temp;
		return matchLine +";";
	}

	/**
	 * Fetches a single match from EGB.com and returns a String with the formatted match info. Format is exactly like with createEGBFile().
	 * 
	 * @param requestedMatchID ID of the required match
	 */
	public String getEGBMatchInfo(int requestedMatchID){
		URL url = null;
		try {
			url = new URL("http://egb.com/ajax.php?key=modules_home_view_ViewBets&type=modules&ind=home&ajax=view&act=ViewBets&id="+requestedMatchID+"&is_navi=0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
		InputStream is = null;
		while(true){
			try {
				is = connection.getInputStream();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		BufferedReader buffreader = new BufferedReader( new InputStreamReader( is )  );
		String line = null;
		String matchInfo = null;
        try {
            while( ( line = buffreader.readLine() ) != null )  {
            	//debug
            	System.out.println(line);
            	matchInfo = line;
            	String matchLine = matchInfo.substring(matchInfo.indexOf("id\":")+5, matchInfo.indexOf("\",", matchInfo.indexOf("id\":")+5)) + ";";
		    	//Lese das match datum aus, format "YYYY-MM-DD HH:MM:SS", kommt nach "when"
		    	String temp = matchInfo.substring(matchInfo.indexOf("bet_time")+11, matchInfo.indexOf("\",", matchInfo.indexOf("bet_time")+11));
		    	String monthTemp = null;
		    	System.out.println(temp.substring(0, 3));
		    	switch(temp.substring(0, 3)){
		    	case "Jan":monthTemp = "01";break;
		    	case "Feb":monthTemp = "02";break;
		    	case "Mar":monthTemp = "03";break;
		    	case "Apr":monthTemp = "04";break;
		    	case "May":monthTemp = "05";break;
		    	case "Jun":monthTemp = "06";break;
		    	case "Jul":monthTemp = "07";break;
		    	case "Aug":monthTemp = "08";break;
		    	case "Sep":monthTemp = "09";break;
		    	case "Oct":monthTemp = "10";break;
		    	case "Nov":monthTemp = "11";break;
		    	case "Dec":monthTemp = "12";break;
		    	}
		    	//teile das Datum in die Komponenten und h�nge sie einzeln an
		    	matchLine += temp.substring(7, 11) +";"+ monthTemp +";"+ temp.substring(4, 6) +";"+ temp.substring(12, 14) +";" + temp.substring(15, 17) +";";
		    	//Name des ersten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("gamer_1")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_1")+10));
		    	matchLine += temp + ";";
		    	//odds des ersten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_1"))+6));
		    	if(temp.equals(""))
		    		temp = "0";
		    	matchLine += temp + ";";
		    	
		    	//Name des zweiten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("gamer_2")+10, matchInfo.indexOf("\",",matchInfo.indexOf("gamer_2")+10));
		    	matchLine += temp + ";";
		    	//odds des zweiten Teams
		    	temp = matchInfo.substring(matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6, matchInfo.indexOf("<\\/div>",matchInfo.indexOf("Rate:", matchInfo.indexOf("view_coef_2"))+6));
		    	if(temp.equals(""))
		    		temp = "0";
		    	matchLine += temp + ";";
		    	
		    	//winner checken und eintragen
		    	if(matchInfo.indexOf("Match is over") == -1 && matchInfo.indexOf("Match was cancelled") == -1){
		    		//wette ist noch offen
		    		matchLine +="-1;0;";
		    	}else{
		    		//wette ist closed
		    		if(matchInfo.indexOf("Match was cancelled") != -1){
		    			matchLine +="0;1;";
		    		}else{
		    			String score = matchInfo.substring(matchInfo.indexOf("["), matchInfo.indexOf("]")+1);
		    			if(!score.contains(":")){
		    				//System.out.println(""+matchInfo.indexOf("[", matchInfo.indexOf("[")+1));
		    				score = matchInfo.substring(matchInfo.indexOf("[", matchInfo.indexOf("[")+1), matchInfo.indexOf("]", matchInfo.indexOf("[", matchInfo.indexOf("[")+1))+1);
		    			};
		    			//System.out.println(score);
		    			int pointsTeam2 = Integer.parseInt(score.substring(score.indexOf(": ")+2, score.indexOf("]")));
		    			int pointsTeam1 = Integer.parseInt(score.substring(1, score.indexOf(" : ")));
		    			if(pointsTeam1 > pointsTeam2){
		    				matchLine +="1;1;";
		    			}else if(pointsTeam1 == pointsTeam2){
		    				matchLine +="0;1;";
		    			}else{
		    				matchLine +="2;1;";
		    			}
		    		}
		    	}
		    	
		    	//tournament
		    	temp = matchInfo.substring(matchInfo.indexOf("tour")+7, matchInfo.indexOf("\",",matchInfo.indexOf("tour")+7));
		    	matchLine += temp + ";";
		    	return matchLine;
            }
            return "Unexpected error";
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        	return "Error beim Zeilen auslesen";
        	//break;
        }
	}
	
	/**
	 * This function checks if two matches, one on CSGOL and one on EGB, are the same. It uses an ArrayList with team pseudonyms in case that
	 * the team names arent spelled the same way. It doesn't check for staff games but implementation is simple but increased the runtime heavily.
	 * On CSGOL a staff team is called "TEAMNAME.staff" and on EGB "TEAMNAME staff" so to check for this is easy.
	 * @param csgol String Array with the CSGOL matchinfos
	 * @param egbl String Array with the EGB matchinfos
	 * @param pseudonyme ListArray with team pseudonyms
	 * @return Returns an array with 4 Integers: isMatched(1 for yes), CSGOL matchID, EGB matchID, isSwitched(1 for yes)
	 */
 	private int[] checkForMatch(String[] csgol, String[] egbl, List<String[]> pseudonyme){
		int[] details = new int[4];
		int[] pseudonymId = new int[]{0,0,0,0};
		//checkformatch
		//first check date
		if(csgol[1].equals(egbl[1]) && csgol[2].equals(egbl[2]) && csgol[3].equals(egbl[3])){
			//check for exact name 
			if((csgol[4].equals(egbl[4]) && csgol[5].equals(egbl[5]))){
				//names are exactly the same
				details[0] = 1;
				details[1] = Integer.parseInt(csgol[0]);
				details[2] = Integer.parseInt(egbl[0]);
				details[3] = 0;
				return details;
			}else if((csgol[4].equals(egbl[5]) && csgol[5].equals(egbl[4]))){
				//names are exactly the same
				details[0] = 1;
				details[1] = Integer.parseInt(csgol[0]);
				details[2] = Integer.parseInt(egbl[0]);
				details[3] = 1;
				return details;
			}
			//check for pseudonym comparison
			//4er boolean array ob pseudonym vorhanden, id 0 und 1 für csgol teams, 2 und 3 für egb teams
			for(int k=0; k<pseudonyme.size(); k++){
				for(int l=0; l<pseudonyme.get(k).length; l++){
					if(csgol[4].equals(pseudonyme.get(k)[l])){
						pseudonymId[0] = k;
					}else if(csgol[5].equals(pseudonyme.get(k)[l])){
						pseudonymId[1] = k;
					}else if(egbl[4].equals(pseudonyme.get(k)[l])){
						pseudonymId[2] = k;
					}else if(egbl[5].equals(pseudonyme.get(k)[l])){
						pseudonymId[3] = k;
					}
				}
			}
			//1. fall: csgol 1. team ist egb 1. team
				//erst check ob 0 und 2 des boolean arrays true (Müssen true sein wenns das gleiche team sein soll
				//oder beide false aber das wäre der exakte fall und wurde schon abgehandelt)
				//dann check ob die ids gleich sind, dann 1 und 3 boolean, dann ids gleich
			if((csgol[4].equals(egbl[4]) && pseudonymId[1] != 0 && pseudonymId[3] != 0 && pseudonymId[1] == pseudonymId[3]) || 
					(csgol[5].equals(egbl[5]) && pseudonymId[0] != 0 && pseudonymId[2] != 0 && pseudonymId[0] == pseudonymId[2]) ||
					(pseudonymId[0] != 0 && pseudonymId[2] != 0 && pseudonymId[0] == pseudonymId[2] && pseudonymId[1] != 0 && pseudonymId[3] != 0 && pseudonymId[1] == pseudonymId[3]) ){
				details[0] = 1;
				details[1] = Integer.parseInt(csgol[0]);
				details[2] = Integer.parseInt(egbl[0]);
				details[3] = 0;
				return details;
			}
			//2. fall, analog
			if((csgol[4].equals(egbl[5]) && pseudonymId[1] != 0 && pseudonymId[2] != 0 && pseudonymId[1] == pseudonymId[2]) || 
					(csgol[5].equals(egbl[4]) && pseudonymId[0] != 0 && pseudonymId[3] != 0 && pseudonymId[0] == pseudonymId[3]) ||
					(pseudonymId[0] != 0 && pseudonymId[3] != 0 && pseudonymId[0] == pseudonymId[3] && pseudonymId[1] != 0 && pseudonymId[2] != 0 && pseudonymId[1] == pseudonymId[2]) ){
				details[0] = 1;
				details[1] = Integer.parseInt(csgol[0]);
				details[2] = Integer.parseInt(egbl[0]);
				details[3] = 1;
				return details;
			}
		}
		return new int[]{0,0,0,0};
	}
}
