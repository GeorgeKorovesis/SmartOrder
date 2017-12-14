package com.example.george.smartorder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloaderTask extends AsyncTask<Void,Void,JSONArray> {

    private Context context;
    taskCompletionResult taskComResult;
    Object initialActivity;
    Boolean Status = false;
    String URL = "http://poirotcafe.esy.es/Products_JSON.json";
    final static int GET = 1;


    DownloaderTask(Context mcontext, Object initActivity)
    {
        context = mcontext;
        initialActivity = initActivity;
    }

/*
In background, new products are downloaded
*/
    @Override
    protected JSONArray doInBackground(Void... params) {

        Log.i("myApp","myApp");
        //taskComResult = (taskCompletionResult)initialActivity;
        //taskComResult.setClickable(Status);
        Log.i("Before handler","Before handler");
        ServiceHandler sHandler = new ServiceHandler();
        Log.i("before return","inbackground");
        JSONArray result = sHandler.makeServiceCall(URL,GET);

        return result;
    }


/*
After download (successful or unsuccessful) enable button for orderings and issue a Toast message
*/
    @Override
    protected void onPostExecute(JSONArray result) {

        int duration = Toast.LENGTH_SHORT;
        String text;

        if (result != null) {
            text = "Success";
            Status = true;
        }
        else
            text = "Unsuccessful Update";

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        /*Set Button Clickable again, since update has been performed*/
        //taskComResult.setClickable(Status);
        //taskComResult.setProducts(result);
        /*int len = 0;
        if (result != null) {
            len = result.length();
        }
        JSONObject tempObj;
        Log.i("ArrayLength", "" + len);
        for (int i=0;i<len;i++)
            try {
                Log.i("i=",""+i);
                tempObj = result.getJSONObject(i);
                Log.i("ProductName->",""+tempObj.getString("ProductName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        //super.onPostExecute(result);
    }
}
