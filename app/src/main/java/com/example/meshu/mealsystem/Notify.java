package com.example.meshu.mealsystem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

public class Notify extends ActionBarActivity  {

    private CheckBox cb1,cb2;

    AfterLogin com;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

         cb1 = (CheckBox) findViewById(R.id.checkBoxYes);
         cb1 = (CheckBox) findViewById(R.id.checkBoxNo);

         com = new AfterLogin();

        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;

                if (c.isChecked()) {
                    com.response("YES");
                    finish();
                }
            }
        });
        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;

                if (c.isChecked()) {
                  com.response("NO");
                    finish();
                }
            }
        });
    }


}
