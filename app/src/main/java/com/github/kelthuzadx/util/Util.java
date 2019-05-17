package com.github.kelthuzadx.util;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static String findFromAcademicPage(String line, String attr){
        String pattern = "<input type=\"hidden\" name=\""+attr+"\" value=\"(.*?)\">";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(line);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static ArrayList<String[]> findFromSkillExamPage(String line){
        String pattern = "<td class=\"center\">(.*?)</td>\\n.*\\n.*<td class=\"center\">(.*?)\\n.*\\n</td>\\n.*\\n\\s*(.*?)</td>";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(line);
        ArrayList<String[]> res = new ArrayList<>();
        while(m.find()) {
            res.add(new String[]{m.group(1),m.group(2),m.group(3)});
        }
        return res;
    }
}
