package Comparators;

import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<String>{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	@Override
    public int compare(String a, String b) {
		
		try{
			System.out.println("Datum 1: "+a+"  Datum 2: "+ b + "  Milli 1: "+ sdf.parse(a).getTime() + "  Milli 2: "+ sdf.parse(b).getTime());
			long a_new = sdf.parse(a).getTime();
			long b_new = sdf.parse(b).getTime();
			return a_new < b_new ? -1 : a_new == b_new ? 0 : 1;
		}catch(Exception e){
			System.out.println("Fehler bei dem Datumsvergleich");
			return 0;
		}
    }
}