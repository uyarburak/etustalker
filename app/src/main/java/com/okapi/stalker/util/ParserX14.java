package com.okapi.stalker.util;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by burak on 8/25/2017.
 */

public class ParserX14 {
    public JSONObject parseIt(String html) throws JSONException {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");
        JSONObject obj = getKunye(elements.get(1));
        String[] secondAtts = getAtts(elements.get(2));
        if(secondAtts != null){
            JSONArray arr = (JSONArray) obj.get("departments");
            appendArray(arr, secondAtts);
        }

        String imgVal = doc.getElementsByAttributeValueStarting("src", "resim.php").attr("src");
        imgVal = imgVal.substring(imgVal.lastIndexOf("=")+1);
        obj.put("image", imgVal);
        return obj;
    }

    private JSONObject getKunye(Element element) throws JSONException {
        String[] attrs = getAtts(element);
        JSONObject obj = new JSONObject();
        //System.out.println(Arrays.toString(attrs));
        obj.put("id", attrs[0]);
        obj.put("name", attrs[1]);
        obj.put("mail", attrs[3]);
        JSONArray arr = new JSONArray();
        obj.put("departments", arr);
        appendArray(arr, attrs);
        return obj;
    }
    private String[] getAtts(Element element){
        Elements elements = element.getElementsByTag("td");
        if(elements.size() < 7)
            return null;
        String[] attrs = new String[7];
        for (int i = 0; i < 7; i++) {
            attrs[i] = elements.get(i).text();
        }
        return attrs;
    }
    private void appendArray(JSONArray arr, String[] attrs) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("department", attrs[2]);
        obj.put("gpa", attrs[4]);
        obj.put("credit", attrs[5]);
        obj.put("class", attrs[6]);
        arr.add(obj);
    }
}
