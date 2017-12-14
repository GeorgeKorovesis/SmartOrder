package com.example.george.smartorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button client, server;
        final Context myContext;

        client = (Button)findViewById(R.id.Client);
        server = (Button)findViewById(R.id.Server);
        myContext = this;

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext,MainActivity.class);
                startActivity(intent);
            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext,OrdersList.class);
                startActivity(intent);
            }
        });
}
}
