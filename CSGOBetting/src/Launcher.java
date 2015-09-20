import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;


public class Launcher {
	public static void main(String[] args){
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
		//System.out.println(matchString);
		String[] matchArray = matchString.split("\\{");
		System.out.println(""+matchArray.length);
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
		//System.out.println(matchString);
		String[] matchArray2 = matchString.split("\\{");
		System.out.println(""+matchArray2.length+" "+matchArray2[matchArray2.length-1]);
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
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:"+File.separator+"csgobetting"+File.separator+"CSGOLoungeData.txt", true)))) {
			if(!fileExists)
				out.println("ID\tY\tM\tD\tH\tM\tTeam1\tTeam2\tWinner\tClosed\tEvent\tFormat");
			for(int i=1; i<matchArray.length; i++){
		    	String matchLine = matchArray[i].substring(matchArray[i].indexOf("\"match\"")+9, matchArray[i].indexOf("\",\"when")) + ";";
		    	int matchId = Integer.parseInt(matchLine.substring(0, matchLine.length()-1));
		    	String temp = matchArray[i].substring(matchArray[i].indexOf("when")+7, matchArray[i].indexOf("\",\"a"));
		    	matchLine += temp.substring(0, 4) +";"+ temp.substring(5, 7) +";"+ temp.substring(8, 10) +";"+ temp.substring(11, 13) +";" + temp.substring(14, 16) +";";
		    	temp = matchArray[i].substring(matchArray[i].indexOf("a\":\"")+4, matchArray[i].indexOf(",\"b\"")-1);
		    	matchLine += temp + ";";
		    	
		    	//get odds for team a, dummy variable wasSucc indicates if odds are availible for this match
		    	int wasSucc = 0;
		    	for(int j=1; j<matchArray2.length; j++){
		    		if(Integer.parseInt(matchArray2[j].substring(matchArray2[j].indexOf("match")+8, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("match")+8))) == matchId){
		    			temp = matchArray2[j].substring(matchArray2[j].indexOf("a\":\"")+4, matchArray2[j].indexOf(",\"b")-1);
		    			matchLine+=temp+";";
		    			wasSucc = 1;
		    		}
		    	}
		    	if(wasSucc == 0)
		    		matchLine += "0;";
		    	wasSucc = 0;
		    	
		    	temp = matchArray[i].substring(matchArray[i].indexOf("b\":\"")+4, matchArray[i].indexOf(",\"winner")-1);
		    	matchLine += temp + ";";
		    	
		    	//get odds for team b, dummy variable wasSucc indicates if odds are availible for this match
		    	wasSucc = 0;
		    	for(int j=1; j<matchArray2.length; j++){
		    		if(Integer.parseInt(matchArray2[j].substring(matchArray2[j].indexOf("match")+8, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("match")+8))) == matchId){
		    			temp = matchArray2[j].substring(matchArray2[j].indexOf("b\":\"")+4, matchArray2[j].indexOf("\"", matchArray2[j].indexOf("b\":\"")+4));
		    			matchLine+=temp+";";
		    			wasSucc = 1;
		    		}
		    	}
		    	if(wasSucc == 0)
		    		matchLine += "0;";
		    	wasSucc = 0;
		    	
		    	temp = matchArray[i].substring(matchArray[i].indexOf("winner")+9, matchArray[i].indexOf("winner")+10);
		    	switch(temp){
		    	case "a":matchLine += 1 + ";";break;
		    	case "b":matchLine += 2 + ";";break;
		    	case "c":matchLine += 0 + ";";break;
		    	case "\"":matchLine += -1 + ";";break;
		    	}
		    	temp = matchArray[i].substring(matchArray[i].indexOf("closed")+9, matchArray[i].indexOf("closed")+10);
		    	matchLine += temp + ";";
		    	temp = matchArray[i].substring(matchArray[i].indexOf("event")+8, matchArray[i].indexOf(",\"format")-1);
		    	matchLine += temp + ";";
		    	temp = matchArray[i].substring(matchArray[i].indexOf("format")+9, matchArray[i].indexOf("format")+10);
		    	matchLine += temp;
		    	out.println(matchLine);
		    }
	    	System.out.println("fertig");
			out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Error");
		}
	}
}
