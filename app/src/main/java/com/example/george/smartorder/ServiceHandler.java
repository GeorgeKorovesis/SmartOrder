package com.example.george.smartorder;

/**
 * Created by George on 10/11/2015.
 */



        import android.util.Log;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.utils.URLEncodedUtils;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.util.EntityUtils;
        import org.json.JSONArray;
        import org.json.JSONException;

public class ServiceHandler {

    static JSONArray response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    JSONArray jsonArray = null;

    public ServiceHandler() {

    }


    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public JSONArray makeServiceCall(String url, int method) {
        try {


            URL URL = new URL(url);
            Log.i("Inside MakeServiceCall1", "Inside service call");
            HttpURLConnection urlConnection = (HttpURLConnection) URL.openConnection();
            Log.i("Inside MakeServiceCall2", "Inside service call");

            if (method == GET) {
                Log.i("Inside Get", "Inside Get");
                //System.out.println("Response Code: " + urlConnection.getResponseCode());
                //Log.i("Inside MakeServiceCall3", ""+urlConnection.getResponseCode());

                InputStream is = urlConnection.getInputStream();
                Log.i("InputStream ok", "Inside Get");

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                Log.i("streamReader ok", "Inside Get");

                StringBuilder responseStrBuilder = new StringBuilder();
                Log.i("responseStrBuilder ok", "Inside Get");

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                Log.i("ResponseBuilder",""+responseStrBuilder);
                jsonArray = new JSONArray(responseStrBuilder.toString());
            }}

            catch (IOException  | JSONException e) {
                e.printStackTrace();
            }

        return jsonArray;
    }
}