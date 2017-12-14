package com.example.george.smartorder;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2/11/2016.
 */
public class NetworkAsyncTask extends AsyncTask<ArrayList<String>, Void, Void> {
    @Override
    protected Void doInBackground(ArrayList<String>... params) {
        int length = params[0].size();
        int time = (int) (System.currentTimeMillis());
        String table_no;
        Timestamp tsTemp = new Timestamp(time);
        String ts =  tsTemp.toString();
        Log.i("Params[0].size()",""+params[0].size());
        Log.i("Params[0]",""+params[0].get(0));
        ArrayList<String> ord = params[0];
        table_no = ord.get(0);
        ord.remove(0);
        String jSonObjString = stringToJSON(ord, table_no);

        System.out.println("Json Object is->>>>"+jSonObjString);
        //for (int i=0;i<length;i++) {
            try {
                Log.i("in asynctask","asynctask");
                URL url = new URL("http://panagiotiskorovessis.gr/gcm_main.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader;
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                //conn.setRequestProperty("Content-Type", "text/html");
                conn.setAllowUserInteraction(false);

                Log.i("check2","check2");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                Log.i("check3","check3");

                PrintWriter out = new PrintWriter(conn.getOutputStream());
                //out.print(jSonObjString.getBytes("UTF-8"));
                out.print(jSonObjString);
                Log.i("check4","check4");

                out.close();


                //os.close();
                Log.i("@@@@@@ck1","@@ck1");

                // Get the server response
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println("" + sb.toString());


                //in.close();
                conn.disconnect();

                // Read Server Response
                //while ((line = reader.readLine()) != null) {
                    // Append server response in string
                //    sb.append(line + "\n");
                //}
                //Log.i("Response = " + line, "");
                //Log.i("response","response");

                //text = sb.toString();
            } catch (Exception ex) {
                Log.i("Post failed",""+ex);
            }


        //}
        return null;
    }

    String stringToJSON(ArrayList<String> Orders, String table_no) {
        JSONObject jsonObj = new JSONObject();
        //JSONObject jsonObj1 = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        //String arr[] = new String[Orders.size()];
        try {
            // Here we convert Java Object to JSON
            for (String order : Orders) {
                jsonArr.put(order);
            }
            jsonObj.put("table", table_no);
            jsonObj.put("products", jsonArr);
            //jsonObj.put("products", "freddo");

            // We add the object to the main object
            //jsonObj.put("order", jsonArr);
            //Log.i("Order@@",""+jsonObj.toString());
            //jsonObj1.put("",jsonObj);
            System.out.println("Json object="+jsonObj.toString());
            return jsonObj.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;

    }


    }

