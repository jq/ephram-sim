/*
* Util.java
* some functions
*/
package webdb;

public class Util implements java.io.Serializable {    public static int toInteger (String s, int def)
    {
        int ret = def;
        if (s == null)
            return ret;
        try {
            ret = Integer.parseInt (s);
        }
        catch (Throwable ignore) {
        }
        return ret;
    }
    public static java.util.Date toDate (String str)
    {
    	/**
    	 * set locale to US
    	 */
    	java.util.Locale locale = java.util.Locale.US;
    	java.util.Date date = null;
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("dd MMM yyyy HH:mm",locale);
        try {
            date = formatter.parse (str);
        }
        catch (Throwable ignore) {
        	ignore.printStackTrace();

        }
        return date;
    }
    /**
     * Format sql timestamp into string
     */
    public static String formatTimestamp (java.util.Date timestamp)
    {
        if (timestamp == null)
            return null;
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("dd MMM yyyy HH:mm");
        return formatter.format (timestamp);
    }
//    public static void main(String[] args) {
//		String dd = "21 Oct 2009 00:00";
//		java.util.Date  date = new java.util.Date();
//		date = toDate(dd.trim());
//		System.out.println(date);
//	}
}
