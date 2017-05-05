package com.example.meshu.mealsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.apache.http.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CreateAccount extends ActionBarActivity  {
    private android.support.v7.widget.Toolbar toolbar;

    private EditText etName,etEmail,etPass,etHouse;
    private CheckBox cbNo,cbYes;
    private Spinner spnHouse;
    View linearLayout1,linearLayout2,linearLayout3,linearLayout4;

    private String[] house ;

    private String etHouseString;
    private ArrayAdapter<String> adapter;


    public static final String POST_URI = "http://meshu.net84.net/meal/getAllHouseCode.php";
    public static final String POST_URI_LOCAL = "http://meshu.net84.net/meal/getAllHouseCode.php";
    public static final String UrL = "http://meshu.net84.net/meal/creatingAccount.php";
    private ArrayList<String> allHouseCode;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    private ProgressDialog pd;

    private  int admin=0;
    private boolean flag = true;
    private boolean is_network_flag=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etHouse = (EditText) findViewById(R.id.etHouse);

        cbNo = (CheckBox) findViewById(R.id.cbNo);
        cbYes = (CheckBox) findViewById(R.id.cbYes);

        linearLayout1 = findViewById(R.id.houseLayout1);

        linearLayout2 = findViewById(R.id.houseLayout2);
        linearLayout3 = findViewById(R.id.houseLayout3);
        linearLayout4 = findViewById(R.id.houseLayout4);

        spnHouse = (Spinner) findViewById(R.id.spinnerHouse);

        getAdapterString();



        cbNo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                CheckBox c =(CheckBox) v;

               if(c.isChecked()){
                   linearLayout1.setVisibility(View.VISIBLE);
                   linearLayout3.setVisibility(View.GONE);
                   linearLayout4.setVisibility(View.VISIBLE);
                   admin = 0;
               }
           }
       });
        cbYes.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                CheckBox c =(CheckBox) v;

               if(c.isChecked()){
                   linearLayout3.setVisibility(View.GONE);

                   linearLayout2.setVisibility(View.VISIBLE);
                   linearLayout4.setVisibility(View.VISIBLE);

                   admin= 1;

               }
           }
       });
        is_network_flag = true;
    }

    public void getAdapterString() {
        if(isNetworkAvailable()){
            // start post thread
            pd = ProgressDialog.show(this,"","Loading...",false,true);

            RequestThread p = new RequestThread();
            p.start();
        }
    }

    public void initializingSpinner() {
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,allHouseCode);

        spnHouse.setAdapter(adapter);

        spnHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                  etHouseString = allHouseCode.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    class RequestThread extends Thread {
        @Override
        public void run(){

            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(POST_URI);

                HttpResponse httpResponse = client.execute(httpGet);
                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity = httpResponse.getEntity();
                    String str = EntityUtils.toString(entity);
                    // Log.d("json data:: ", str);

                    JSONObject reqObject = new JSONObject(str);
                    int success = reqObject.getInt("success");
                    if(success == 1){
                        JSONArray jsonArray = reqObject.getJSONArray("books");
                        int size = jsonArray.length();
                        allHouseCode = new ArrayList<String>();
                        for (int i = 0; i < size; i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            String house = jobject.getString("house");
                            allHouseCode.add(house);
                        }
                        handler.sendEmptyMessage(SUCCESS);
                    }
                    else{
                        handler.sendEmptyMessage(FAILURE);
                    }
                }
                else {
                    handler.sendEmptyMessage(FAILURE);

                }



            }catch (ClientProtocolException e){
                e.printStackTrace();
                handler.sendEmptyMessage(FAILURE);
            }
            catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(FAILURE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    android.os.Handler handler = new android.os.Handler(){
        @Override

        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what){
                case SUCCESS:
                    if(allHouseCode != null){
                        initializingSpinner();
                    }
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"404:: error  not found.",Toast.LENGTH_LONG).show();

                    break;
            }

        }
    };

    class RequestThread2 extends Thread {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(UrL);

            List param = new ArrayList<BasicNameValuePair>();

            if(!etName.equals("")&&!etEmail.equals("")&&!etPass.equals("") ) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                param.add(new BasicNameValuePair("name", name));
                param.add(new BasicNameValuePair("email", email));
                param.add(new BasicNameValuePair("password", pass));
                param.add(new BasicNameValuePair("house", etHouseString));
                param.add(new BasicNameValuePair("admin",admin+""));

            }


            try {
                postReq.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse resp = client.execute(postReq);

                if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    HttpEntity entity= resp.getEntity();
                    String json = EntityUtils.toString(entity);
                    Log.d("json post:: ", json);

                    JSONObject jOb = new JSONObject(json);

                    int success = jOb.getInt("success");

                    if(success == 1){
                        h.sendEmptyMessage(success);
                    }
                    else{
                        h.sendEmptyMessage(FAILURE);
                    }


                }
            }
            catch (UnsupportedEncodingException e) {
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

    android.os.Handler h = new android.os.Handler(){
        @Override

        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(getApplicationContext(),"successfully created an account.",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"404:: error not found.",Toast.LENGTH_LONG).show();

                    break;
            }

        }
    };



    private boolean isNetworkAvailable(){
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if(networkInfo != null){
            if(networkInfo.isAvailable() && networkInfo.isConnected()){

                return true;
            }
        }
        return false;
    }

    public void submitAccount(View v){
        if (etEmail == null) {
            flag = false;
        } else {
            flag = android.util.Patterns.EMAIL_ADDRESS.matcher((CharSequence) etEmail.getText()).matches();
        }
        if(flag == true){
            if(!etHouse.equals("") && admin==1){
                etHouseString = etHouse.getText().toString();
                for (String m: allHouseCode) {
                    if(m == etHouseString) {
                        is_network_flag = false;
                    }
                }

            }
            if(is_network_flag == true && isNetworkAvailable()) {
                // start post thread
                pd = ProgressDialog.show(this, "", "Loading...", false, true);

                RequestThread2 p = new RequestThread2();
                p.start();
              }
            else{
                Toast.makeText(getApplicationContext(),"Error:: This code is already exits.\nPlease Type an Unique family code.",Toast.LENGTH_LONG).show();

            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Error:: Please Enter a valid email id.",Toast.LENGTH_LONG).show();

        }
    }

}
