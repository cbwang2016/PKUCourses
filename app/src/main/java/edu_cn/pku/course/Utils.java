package edu_cn.pku.course;

public class Utils {
    public static String betweenStrings(String str, String leftStr, String rightStr) {
        return str.substring(str.indexOf(leftStr) + leftStr.length(), str.indexOf(rightStr));
    }
}
