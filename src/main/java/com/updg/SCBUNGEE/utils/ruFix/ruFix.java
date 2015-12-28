package com.updg.SCBUNGEE.utils.ruFix;

/**
 * Created by Alex
 * Date: 15.12.13  17:55
 */
public class ruFix {
    public static String f(String str) {
        String nstr = str;
        if (!str.isEmpty()) {
            String cp_from = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ¸¨";
            String cp_to = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяёЁ";
            for (int i = 0; i < cp_from.length(); i++)
                nstr = nstr.replace(cp_from.charAt(i), cp_to.charAt(i));
        }
        return nstr;
    }

}
