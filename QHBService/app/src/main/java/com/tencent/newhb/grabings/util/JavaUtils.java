package com.tencent.newhb.grabings.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUtils {

    public static String getNumberFromString(String string) {

        if (string == null || (string != null && string.isEmpty())) {
            return "0";
        }

        String regEx="[^0-9]";
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(string);

        return matcher.replaceAll("").trim();
    }
}
