package wallet.util;

import org.apache.log4j.Logger;

import scala.Function1;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.runtime.AbstractFunction1;

/**
 * Utility and constants class, for manipulating strings. 
 */
public final class Str {
	
	private Str() {
		throw new UnsupportedOperationException();
	}
		
	private static Logger logger = Logger.getLogger(Str.class);
	
	// General artifacts for String conversions 
	public static final String NULL = "NuLL";
	public static final String EMPTY = "\"\"";
	public static final String QUOTE = "\"";
	public static final String COMMA = ",";	
	
	// Artifacts for String conversions concerning arrays
	public static final String ARRAY_SEPARATOR = ",";
	public static final String ARRAY_PREFIX = "{";
	public static final String ARRAY_SUFFIX = "}";
		
		
	/** 
	 * Returns string representation of <code>o</code>, otherwise empty string
	 */	
	public static String str(Object o) {
        return str(o, "");
	}	
	
	/** 
	 * Returns string representation of <code>o</code>, otherwise <code>v</code>
	 */
	public static String str(Object o, String v) {
		if (o != null) {
			String s = null;
			try {
				s = o.toString();
			} catch (Exception e) {
				logger.error("Error while processing toString()", e);
			}
			return s != null ? s : v;
		} 		
		return v;
	}
	
	public static void test() {
		logger.error("Do it");
		new FunProc().doIt();
	}
	
	static class FunProc {
	    List nil = Nil$.MODULE$;                      // the empty list
	    List<Integer> list1 = nil.$colon$colon(1);    // append 1 to the empty list
	    List<Integer> list2 = list1.$colon$colon(2);  // append 2 to List(1)
	    List<Integer> list3 = list2.$colon$colon(3).$colon$colon(14).$colon$colon(8); // List(1, 2, 3, 14, 8)

	    Function1<Integer, Boolean> filterFn = new AbstractFunction1<Integer, Boolean>() {
	        @Override public Boolean apply(Integer value) { return value<10; }
	    };

	    List<Integer> list4 = (List<Integer>)(((scala.collection.TraversableLike) list3).filter(filterFn)); // List(1, 2, 3, 8)

	    public void doIt() {
	        logger.error("Filtered List is " + list4);
	    }
	}	

	
	/** 
	 * Returns trimmed string representation of <code>o</code>, otherwise <code>null</code>
	 */
	public static String nil(Object o) {
	    if (o != null) {
	    	String s = str(o, null);
	    	if (s != null) {
	    		s = s.trim();
	    		return s.length() > 0 ? s : null;
	    	}
	    	return null;
	    } 
	    return null;
	}
	
	/** 
	 * Returns trimmed string representation of <code>o</code>, otherwise <code>v</code>
	 */
	public static String nil(Object o, String v) {
        String s = nil(o);
        return s != null ? s : v;
	}
	
	/** 
	 * Returns string representation of <code>o</code>, optimized for printing purpose  
	 */		
	public static String prn(Object o) {
		if (o != null) {
			String s = null;
			try {
				s = o.toString();
			} catch (Exception e) {
				logger.error("Error while processing toString()", e);
			}
			return s != null
			    ? (Str.nil(s) != null ? s : (QUOTE + s + QUOTE))
			    : NULL;
		}
        return NULL;		
	}	
	
	public static String arrayToString(Object[] arr) {
		StringBuffer buf = new StringBuffer("[");
		for (int i = 0; i < arr.length; i++) {
			buf.append(i).append(":").append(arr[i].toString()).append("|");
		}
		buf.append("]");		
		return buf.toString();
	}
	
}
