package com.example.meshu.mealsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

public class MainActivity extends ActionBarActivity {
    private android.support.v7.widget.Toolbar toolbar;

    public static final String POST_URI = "http://meshu.net84.net/meal/loginCheck.php";
    private ArrayList<MealPerson> allbook,allbookfrom;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    private ProgressDialog pd;

    //EditText
    private EditText etEmail,etPass,etHouseCode;

    private Button btnLogin,btnCreateAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
       setSupportActionBar(toolbar);


       /*
       * edit text and button initialization
       * */
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etHouseCode = (EditText) findViewById(R.id.etHouseCode);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateAccount = (Button) findViewById(R.id.btnAccount);

    }



    //for login method

    public void login(View v){
        if(isNetworkAvailable()){
            // start post thread
            pd = ProgressDialog.show(this,"","Loading...",false,true);

            PostThread p = new PostThread();
            p.start();
        }

    }

    public void createAccount(View v){
        Intent intent = new Intent(getApplicationContext(),CreateAccount.class);
        startActivity(intent);

    }



    class PostThread extends Thread{


        @Override
        public void run() {
            List param = new ArrayList<BasicNameValuePair>();

            if(!etEmail.equals("") &&!etPass.equals("")
                    && !etHouseCode.equals("")){
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                String house = etHouseCode.getText().toString();

                param.add(new BasicNameValuePair("email",email));
                param.add(new BasicNameValuePair("password",password));
                param.add(new BasicNameValuePair("house",house));
            }

            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(POST_URI);



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
                        JSONArray jsonArray = jOb.getJSONArray("books");
                        int size = jsonArray.length();
                        allbookfrom = new ArrayList<MealPerson>();
                        for (int i = 0; i < size; i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            String name = jobject.getString("name");
                            String email = jobject.getString("email");
                            String pass = jobject.getString("password");
                            String house = jobject.getString("house");
                            String deposit = jobject.getString("deposit");
                            String meal = jobject.getString("meal");
                            String des = jobject.getString("description");
                            String due = jobject.getString("due");
                               int due_pay = jobject.getInt("due_pay");
                               int admin = jobject.getInt("admin");

                            int id = jobject.getInt("id");

                           MealPerson mealPerson = new MealPerson(id,name,email,pass,house,deposit,meal,des,due,admin,due_pay);
                            allbookfrom.add(mealPerson);

                        }
                        h.sendEmptyMessage(SUCCESS);
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
                    Toast.makeText(getApplicationContext(), "Successfully login.", Toast.LENGTH_LONG).show();
                    if(allbookfrom != null){
                        MealPerson m = allbookfrom.get(0);
                         Intent intent = new Intent(MainActivity.this,AfterLogin.class);
                          intent.putExtra("person",m);
                          startActivity(intent);
                    }
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"404:: error Books not found.",Toast.LENGTH_LONG).show();

                    break;
            }

        }
    };

 /*
 * network testing method,,
 *
 * */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

            if(id == R.id.help){
                Intent i = new Intent(this,HelpActivity.class);
                startActivity(i);
            }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
