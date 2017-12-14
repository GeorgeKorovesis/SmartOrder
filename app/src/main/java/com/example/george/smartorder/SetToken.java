package com.example.george.smartorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by George on 12/18/2016.
 */

public class SetToken extends Activity {

    private Button setTokenBtn;
    private TextView token;
    private EditText newToken;
     SharedPreferences keyPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settoken);

        setTokenBtn = (Button)findViewById(R.id.upd_token);
        token = (TextView)findViewById(R.id.tokenTextView);
        newToken = (EditText)findViewById(R.id.new_token);

        keyPref = getSharedPreferences("key", 0);
        String key = keyPref.getString("key","");
        token.setText(getResources().getString(R.string.exist_key)+" "+key);


        setTokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = newToken.getText().toString();
                token.setText(getResources().getString(R.string.exist_key)+" : "+key);
                keyPref = getSharedPreferences("key", 0);
                SharedPreferences.Editor editor = keyPref.edit();
                editor.putString("key", key);
            }
        });
    }


}
