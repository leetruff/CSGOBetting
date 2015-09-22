package Comparators;

import java.util.Comparator;

public class DoubleComparator implements Comparator<String>{
	@Override
    public int compare(String a, String b) {
		double a_new = Double.parseDouble(a.split("%")[0]);
		double b_new = Double.parseDouble(b.split("%")[0]);
		return a_new < b_new ? -1 : a_new == b_new ? 0 : 1;
    }
}
