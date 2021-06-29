package visual.camp.sample.app.activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import visual.camp.sample.app.HTTPConnect;

public class Papago extends MyAsyncTask<Void,Void,String> {
    final static String clientId = "nLV74yJl7C7lSakvpzhG"; //애플리케이션 클라이언트 아이디값
    final static String clientSecret = "uUpZL9assV"; //애플리케이션 클라이언트 시크릿값

    String query, source, target;


    public Papago(String query, String source, String target){
        this.query = query;
        this.source = source;
        this.target = target;
    }

    protected void onPreExecute() {   }

    protected String doInBackground(Void args) {
        String result = "";
        try {
            String url = "https://openapi.naver.com/v1/papago/n2mt";
            String data = "source=" + source + "&target=" + target + "&text=" + URLEncoder.encode(query, "UTF-8")
                    .replaceAll("\\+", "%20").replaceAll("%0A", "%5Cn");

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Naver-Client-Id", clientId);
            headers.put("X-Naver-Client-Secret", clientSecret);

            result = HTTPConnect.post(url, headers, data);

            return new JSONObject(result).getJSONObject("message").getJSONObject("result").getString("translatedText");
        } catch(JSONException e){
            e.printStackTrace();
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onProgressUpdate(Void progress) {   }

    protected void onPostExecute(String result) {   }

    @Override
    protected void onCancelled(String result) {

    }
}
