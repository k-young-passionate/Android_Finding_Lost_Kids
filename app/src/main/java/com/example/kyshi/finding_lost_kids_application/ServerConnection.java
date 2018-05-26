package com.example.kyshi.finding_lost_kids_application;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerConnection {

    final private static String baseURL = "http://swp3.gonetis.com:8888/";
    private static String url;
    private static URL urlConnection;
    private static HttpURLConnection httpURLConnection;

    public static String CONNECTION(String addurl, ArrayList<Kid> kids, String ANDROID_ID) {

        if (addurl == null) {
            url = baseURL;
        } else {
            url = baseURL + addurl;
        }
        InputStream is = null;
        String result = "";

        try {
            urlConnection = new URL(url);
            httpURLConnection = (HttpURLConnection) urlConnection.openConnection();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        if (url.contains("register")) {
            try {
                String json = "";

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", ANDROID_ID);

                JSONArray jsonArray = new JSONArray();

                if (kids.isEmpty()) {
                    jsonArray = null;
                } else {
                    for (Kid kid : kids) {
                        JSONObject jsonObject_sub = new JSONObject();
                        jsonObject_sub.accumulate("name", kid.getName());
                        jsonObject_sub.accumulate("pic", kid.getPhoto());
                        jsonObject_sub.accumulate("tag", kid.getTag_sn());
                        jsonArray.put(jsonObject_sub);
                    }
                }

                jsonObject.accumulate("children", jsonArray);

                json = jsonObject.toString();

                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-type", "application/json");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream os = httpURLConnection.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(url.contains("users")){
            httpURLConnection.setDoInput(true);
        } else if (url.contains("map")) {
            httpURLConnection.setDoInput(true);
        } else {
            httpURLConnection.setDoInput(true);
        }

        try {
            is = httpURLConnection.getInputStream();
            if (is != null) {
                result = convertInputStreamToString(is);
            } else {
                result = null;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return result;
    }

    /* 서버에서 받는 정보를 String 으로 변환 */
    public static String convertInputStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
