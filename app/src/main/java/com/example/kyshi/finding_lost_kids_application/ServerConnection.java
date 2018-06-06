package com.example.kyshi.finding_lost_kids_application;

import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerConnection {

    /* 서버연결 관련 상수 */
    private final static String baseURL = "http://swp3.gonetis.com:8888/";
    public final static int MODE_GET = 0;
    public final static int MODE_POST = 1;
    public final static int MODE_DELETE = 2;
    private final static String attachmentName = "Photo";

    /* 서버연결 관련 변수 */
    private static String url;
    private static URL urlConnection;
    private static HttpURLConnection httpURLConnection;


    public static String CONNECTION(String addurl, Kid kid, String ANDROID_ID, int mode) {

        InputStream is = null;  // 서버에서 읽어온 데이터 저장
        String result = "";     // 상태 반환할 값

        /* url 값으로 서버에 연결 */
        try {
            if (addurl == null) {
                url = baseURL;
            } else {
                url = baseURL + addurl;
            }
            urlConnection = new URL(url);
            httpURLConnection = (HttpURLConnection) urlConnection.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Log.e("!!!!!!!!!!!!!!!!!!!!","url: " + url + addurl);
        /* url 값으로 서버에 연결 */

        String json = "";
        switch (mode) {
            case MODE_GET:
                if (url.contains("register") | url.contains("users") | url.contains("map")) {
                    try {
                        httpURLConnection.setDoInput(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    result = null;
                }
                break;

            case MODE_POST:
                if (url.contains("users")) {
                    try {
                        /* 보낼 객체 JSON 화 */
/*
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        kid.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bab = bos.toByteArray();
*/
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("name", kid.getName());
                        jsonObject.accumulate("tag", kid.getTag_sn());


                        Log.e("!!!!!!!!!!!!!!!!!!!!","kid: " + kid.getName() + kid.getTag_sn());

                        json = jsonObject.toString();
                        /* 보낼 객체 JSON 화 */

                        /* http 소켓 만들기 */
                        httpURLConnection.setRequestProperty("Content-type", "application/json");
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        /* http 소켓 만들기 */

                        /* DOS로 사진 처리 */
                        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                        dos.writeBytes("--*****\r\n");
                        dos.writeBytes("Content-Disposition: form-data; name=\""+attachmentName+"\";filename=\"" + kid.getPic_addr()+"\"\r\n");
                        dos.writeBytes("\r\n");
                        /* DOS로 사진 처리 */

                        //////////////////////////////////// 위랑아래가 동시에 될지 의문

                        /* 서버에 보내기 */
                        OutputStream os = httpURLConnection.getOutputStream();
                        os.write(json.getBytes("UTF-8"));
                        os.flush();
                        /* 서버에 보내기 */
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    result = null;
                }
                break;

            case MODE_DELETE:
                if (url.contains("users")) {
                    try {
                        /* http 소켓 만들기 */
                        httpURLConnection.setRequestMethod("DELETE");
                        httpURLConnection.setDoInput(true);
                        /* http 소켓 만들기 */
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    result = null;
                }
                break;

            default:
                result = null;

        }


        /* 결과값 받기 */
        switch (mode) {
            case MODE_POST:
                try {
                    result = httpURLConnection.getResponseMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
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
                }
                break;

        }
        /* 결과값 받기 */

        httpURLConnection.disconnect();     // 서버 연결 해제
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
