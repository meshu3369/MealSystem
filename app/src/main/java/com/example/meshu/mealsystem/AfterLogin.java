package com.example.meshu.mealsystem;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AfterLogin extends ActionBarActivity implements MyCommunicator {
    private android.support.v7.widget.Toolbar toolbar;

    private TextView tvPersonName, tvDeposit, tvMeal, tvAdmin;

    private EditText etMeal, etDeposit, etDescription;

    private View layoutForView, checkinglayout, btnSubmit;
    private CheckBox checkBox;

    private boolean edit = false;
    private String name, deposit, meal, email, house_code;
    private int is_admin = 0;

    //for server
    public static final String UrL = "http://meshu.net84.net/meal/updateDepostiMeal.php";
    public static final String URL_LOCAL = "http://192.168.137.1/meal/updateDepostiMeal.php";
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        drawerFragment.setUp(R.id.fragment, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        tvPersonName = (TextView) findViewById(R.id.textViewPersonName);
        tvDeposit = (TextView) findViewById(R.id.textViewDeposit);
        tvMeal = (TextView) findViewById(R.id.textViewMeal);
        tvAdmin = (TextView) findViewById(R.id.admin);

        btnSubmit = findViewById(R.id.btnSubmit);


        MealPerson mealPerson = (MealPerson) getIntent().getSerializableExtra("person");

        house_code = mealPerson.getHouse();
        is_admin = mealPerson.getAdmin();
        email = mealPerson.getEmail();
        name = mealPerson.getName();
        deposit = mealPerson.getDeposit();
        meal = mealPerson.getMeal();

        if (is_admin == 1) {
            tvAdmin.setText("Admin" + "\nFamily: " + house_code);
        } else {
            tvAdmin.setVisibility(View.GONE);
        }
        tvPersonName.setText(name);
        tvDeposit.setText(deposit + " Tk");
        tvMeal.setText(meal);

        etMeal = (EditText) findViewById(R.id.etMeal);
        etDeposit = (EditText) findViewById(R.id.etAmount);
        etDescription = (EditText) findViewById(R.id.etDescription);


        checkBox = (CheckBox) findViewById(R.id.checkBox);
        layoutForView = findViewById(R.id.layoutForView);
        checkinglayout = findViewById(R.id.layoutcheck);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;

                if (c.isChecked()) {
                    checkinglayout.setVisibility(View.GONE);
                    layoutForView.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    edit = true;

                }
            }
        });

        sendDataToFragment();
        sendAdminCheckToFragment();


    }

    public String sendDataToFragment() {
        return email;
    }

    public int sendAdminCheckToFragment() {
        return is_admin;
    }

    public void sbAccount(View v) {
        String meal2, deposit2, description2;
        long a = 0, b = 0, c = 0, d = 0, e = 0;
        if (edit == true) {

            meal2 = etMeal.getText().toString();

            deposit2 = etDeposit.getText().toString();


            if (!etDescription.equals("")) {
                description2 = etDescription.getText().toString();
            } else {
                description2 = "Nil";
            }


            try {
                a = Integer.parseInt(meal2);
            } catch (NumberFormatException esdfsd) {
                a = 0;
            }
            try {
                b = Integer.parseInt(meal);
            } catch (NumberFormatException ed) {
                b = 0;
            }

            c = a + b;

            String m = Long.toString(c);

            meal = meal.replace(meal, m);
            tvMeal.setText(meal);


            try {
                d = Integer.parseInt(deposit2);
            } catch (NumberFormatException edf) {
                d = 0;
            }
            try {
                e = Integer.parseInt(deposit);
            } catch (NumberFormatException effd) {
                e = 0;

            }


            c = d + e;

            m = Long.toString(c);

            deposit = deposit.replace(deposit, m);
            tvDeposit.setText(deposit + " Tk");

            if (isNetworkAvailable()) {
                // start post thread
                pd = ProgressDialog.show(this, "", "Loading...", false, true);

                RequestThread2 p = new RequestThread2(name, meal2, deposit2, description2);
                p.start();

            }
        }
    }

    class RequestThread2 extends Thread {
        String name, meal, deposit, description;

        public RequestThread2(String name, String meal, String deposit, String description) {
            this.name = name;
            this.meal = meal;
            this.deposit = deposit;
            this.description = description;
        }

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(UrL);


            List param = new ArrayList<BasicNameValuePair>();
            param.add(new BasicNameValuePair("name", name));
            param.add(new BasicNameValuePair("meal", meal));
            param.add(new BasicNameValuePair("deposit", deposit));
            param.add(new BasicNameValuePair("description", description));

            try {
                postReq.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse resp = client.execute(postReq);

                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = resp.getEntity();
                    String json = EntityUtils.toString(entity);
                    Log.d("json post:: ", json);

                    JSONObject jOb = new JSONObject(json);

                    int success = jOb.getInt("success");

                    if (success == 1) {
                        h.sendEmptyMessage(success);
                    } else {
                        h.sendEmptyMessage(FAILURE);
                    }


                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    android.os.Handler h = new android.os.Handler() {
        @Override

        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(getApplicationContext(), "update successfully completed.", Toast.LENGTH_LONG).show();
                    checkinglayout.setVisibility(View.VISIBLE);
                    layoutForView.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(), "404:: error not found.", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };


    private boolean isNetworkAvailable() {
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isAvailable() && networkInfo.isConnected()) {

                return true;
            }
        }
        return false;
    }


    //for fragment communication
    public void response(String data) {
        if (data == "result") {
            Intent intent = new Intent(this, Result.class);
            intent.putExtra("HOUSE", house_code);
            startActivity(intent);
        } else if (data == "sheet") {
            Intent intent = new Intent(this, Sheet.class);
            intent.putExtra("HOUSE", house_code);
            startActivity(intent);
        } else if (data == "signout") {
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    finish();
                }

            }.start();
        } else if (data == "finalResult") {
            MyDialogue alartMsg = new MyDialogue();
            alartMsg.name(name);
            alartMsg.show(getSupportFragmentManager(), "my_dialogue");

            // restarting this activity ..

        }

    }

    public void dialogueRespons() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
