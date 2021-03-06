/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.george.smartorder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;

//import java.util.ArrayList;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    SQLiteDatabase SmartOrderDB;
    final static String Action_UpdateDB = "UPDATE_DB";
    SharedPreferences sharedPreferences;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("MyGCMListenerService","messageReceived");


        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //String message = order;
        updateOrdersDB(data);
        sendBroadcastToOrdersList();
        sendNotification("YEAHHHH");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean(MyGcmListenerService.Action_UpdateDB, true).apply();
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /*
     *
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, OrdersList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("New Order Received")
                //.setContentText(message)
                //.setContentText("Message Received!!!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


private void updateOrdersDB(Bundle message)
{
    String table = message.getString("table");
    ArrayList<String> productList = new ArrayList<>();
    //for (String key: data.keySet())
    //{
    //    Log.d ("myApplication", key + " is a key in the bundle");
    //}

    String products = message.getString("products");

    products=products.replace("[","");
    products=products.replace("]","");
    products=products.replace("\"","");
    String[] prods = products.split(",");


    SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
    //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
    SmartOrderDB.execSQL("CREATE TABLE IF NOT EXISTS OrderList(tablenum VARCHAR, orders VARCHAR);");


    Integer nextID = getNextID();

    for ( String prod : prods) {
        productList.add(prod);
        nextID++;
//update database
        //String l = "INSERT INTO OrdersList VALUES('" + table + "','" + prod + "');";
        SmartOrderDB.execSQL("INSERT INTO OrderList VALUES('" + table + "','" + prod + "');");
    }
}


private void sendBroadcastToOrdersList()
{
    //send message - action=updatedb
    Intent notifyIntent = new Intent();
    notifyIntent.setAction(Action_UpdateDB);
    notifyIntent.putExtra("DATAPASSED", "1234");
    sendBroadcast(notifyIntent);
    Log.i("Sent Intent for DB","Intent for DB sent");
}
private Integer getNextID() {
    Cursor resultSet;
    String table, order;
    SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
    //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
    resultSet = SmartOrderDB.rawQuery("SELECT * FROM OrderList", null);
    //ordersList = new ArrayList<>();
    Integer id = 0;
    while (resultSet.moveToNext())
        id++;

    resultSet.close();

    return id;
}

}