package visual.camp.sample.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HTTPConnect {
    public static String post(String url, Map<String, String> headers, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        for(Map.Entry<String, String> header :headers.entrySet()){
            con.setRequestProperty(header.getKey(), header.getValue());
        }
        con.setDoOutput(true);
        try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())){
            wr.writeBytes(data); wr.flush();
        }
        if( con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readBody(con.getInputStream());
        } else{
            return readBody(con.getErrorStream());
        }
    }

    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        for(Map.Entry<String, String> header :headers.entrySet()){
            con.setRequestProperty(header.getKey(), header.getValue());
        }
        if( con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readBody(con.getInputStream());
        } else{
            return readBody(con.getErrorStream());
        }
    }

    private static String readBody(InputStream body) throws IOException {
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(body));
        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = lineReader.readLine()) != null) {
            responseBody.append(line);
        }
        return responseBody.toString();
    }
}


