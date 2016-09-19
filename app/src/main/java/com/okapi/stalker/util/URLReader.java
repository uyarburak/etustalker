package com.okapi.stalker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLReader {
    public static String readURL(String path){
        StringBuilder html = new StringBuilder();
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                html.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html.toString();
    }
}
