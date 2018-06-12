package com.example.kyshi.finding_lost_kids_application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {

    /* 서버연결 관련 상수 */
    private final static String baseURL = "http://swp3.gonetis.com:8888/";
    public final static int MODE_GET = 0;
    public final static int MODE_POST = 1;
    public final static int MODE_DELETE = 2;
    public final static int MODE_PHOTO = 3;
    private final static String attachmentName = "Photo";

    /* 서버연결 관련 변수 */
    private static String url;
    private static URL urlConnection;
    private static HttpURLConnection httpURLConnection;


    public static Bitmap CONNECTION_map(String addurl, Kid kid, String ANDROID_ID, int mode) {

        InputStream is = null;  // 서버에서 읽어온 데이터 저장
        Bitmap result = null;     // 상태 반환할 값

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

        Log.e("!!!!!!!!!!!!!!!!!!!!", "url: " + url + addurl);
        /* url 값으로 서버에 연결 */

        String json = "";
        switch (mode) {
            case MODE_GET:
                try {
                    httpURLConnection.setDoInput(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case MODE_POST:

                break;

            case MODE_DELETE:

                break;

            default:
                result = null;

        }


        /* 결과값 받기 */
        switch (mode) {
            case MODE_POST:

                break;

            case MODE_GET:
            default:
                try {
                    is = httpURLConnection.getInputStream();

                    if (is != null) {
                        result = BitmapFactory.decodeStream(is);
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

        /* url 값으로 서버에 연결 */

        String json = "";
        switch (mode) {
            case MODE_GET:
                if (url.contains("register") | url.contains("users") | url.contains("map") | url.contains("emerg") | url.contains("noemerg")) {
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
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("name", kid.getName());
                        jsonObject.accumulate("tag", kid.getTag_sn());
                        json = jsonObject.toString();
                        /* 보낼 객체 JSON 화 */

                        /* http 소켓 만들기 */
                        httpURLConnection.setRequestProperty("Content-type", "application/json");
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        /* http 소켓 만들기 */

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

            case MODE_PHOTO:
                if (url.contains("photo")) {
                    try {

                        String filename = kid.getPic_addr();

                        String attachmentName = "photo";

                        httpURLConnection.setRequestProperty("Content-type", "multipart/form-data;boundary=" + "*****");
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);

                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                        httpURLConnection.setRequestProperty("Cache-Control", "no-cache");

                        // http 소켓 만들기 //
                        Log.e("????????????????", "url: " + url + addurl);

                        // DOS로 사진 처리 //
                        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                        dos.writeBytes("--*****\r\n");
                        dos.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + kid.getPic_addr() + "\"\r\n");
                        dos.writeBytes("\r\n");
                        // DOS로 사진 처리 //


                        // Read from FileInputStream and write to OutputStream
                        byte[] buffer = new byte[2048];
                        if (filename != null) {
                            FileInputStream fileInputStream = new FileInputStream(filename);
                            int res = 1;
                            while ((res = fileInputStream.read(buffer)) > 0) {
                                OutputStream os = httpURLConnection.getOutputStream();
                                os.write(buffer, 0, res);
                            }

                        }

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
            case MODE_PHOTO:
            case MODE_POST:
                try {
                    result = httpURLConnection.getResponseMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MODE_GET:

            default:
                try {
                    if (addurl != null) {
                        if (addurl.contains("tagexist")) {
                            result = "" + httpURLConnection.getResponseCode();
                        } else {
                            is = httpURLConnection.getInputStream();
                            result = convertInputStreamToString(is);
                        }
                    } else {
                        is = httpURLConnection.getInputStream();
                        result = convertInputStreamToString(is);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    is = httpURLConnection.getErrorStream();
                    try {
                        result = convertInputStreamToString(is);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
