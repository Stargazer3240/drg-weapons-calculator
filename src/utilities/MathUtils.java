package utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		 
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double log2(double a) {
		return Math.log(a) / Math.log(2);
	}
	
	public static double vectorDotProduct(double[] A, double[] B) {
		if (A.length != B.length) {
			return -1.0;
		}
		
		double sum = 0.0;
		for (int i = 0; i < A.length; i++) {
			sum += A[i] * B[i];
		}
		return sum;
	}
	
	public static double sum(double[] A) {
		if (A.length == 0) {
			return 0;
		}
		
		double sum = 0.0;
		for (int i = 0; i < A.length; i++) {
			sum += A[i];
		}
		return sum;
	}
}
