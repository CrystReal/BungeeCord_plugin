package com.updg.SCBUNGEE.utils;


public class StringUtil {

    /**
     * This method return plural form of word based on int value.
     *
     * @param number Int value
     * @param form1  First form of word (i == 1)
     * @param form2  Second form of word (i > 1 && i < 5)
     * @param form3  Third form of word (i > 10 && i < 20)
     * @return string
     */
    public static String plural(int number, String form1, String form2, String form3) {
        int n1 = Math.abs(number) % 100;
        int n2 = number % 10;
        if (n1 > 10 && n1 < 20) return form3;
        if (n2 > 1 && n2 < 5) return form2;
        if (n2 == 1) return form1;
        return form3;
    }

    public static String combineSplit(int startIndex, String[] string, String seperator) {
        StringBuilder builder = new StringBuilder();
        if (string.length >= 1) {
            for (int i = startIndex; i < string.length; i++) {
                builder.append(string[i]);
                builder.append(seperator);
            }

            if (builder.length() > seperator.length()) {
                builder.deleteCharAt(builder.length() - seperator.length()); // remove
                return builder.toString();
            }
        }
        return "";
    }
}
