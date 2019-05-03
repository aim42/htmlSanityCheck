package org.aim42;// see end-of-file for license information


import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

public class UnsecureHttpConnection {

    static HttpsURLConnection connection;
//    static String urlstring = "https://google.com";
    static String urlstring = "https://arc42.org";

    public static void main(String[] args) {
        try {
            URL url = new URL(urlstring);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(500);
            connection.setRequestProperty("Referer", "https://aim42.org");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.connect();
            int responseCode = connection.getResponseCode();

            System.out.print("ResponseCode for HEAD-request to " + urlstring + " = " + responseCode);


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


}

