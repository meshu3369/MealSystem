package com.example.meshu.mealsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Sheet extends ActionBarActivity {
    /*for network access
    * variable declaring
    *
    * and the url http://meshu.net84.net/meal/getAllMealInfo.php
    * */

    public static final String URL = "http://meshu.net84.net/meal/getAllMealInfo.php";
    public static final String URL_LOCAL = "http://192.168.137.1/meal/getAllMealInfo.php";
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;
    private ProgressDialog pd;

    private ListView lvSheet;

    BaseAdapter adapter;
    ArrayList<MealPerson> allPersonDataArray;


    /*
    *
    * class variable
     *
     * */
    private String house_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        house_code = getIntent().getStringExtra("HOUSE");



        lvSheet = (ListView) findViewById(R.id.lvSheet);

        allPersonDataArray = new ArrayList<MealPerson>();

        adapter = new BaseAdapter() {
            LayoutInflater layoutInflater  = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @Override
            public int getCount() {
                return allPersonDataArray.size();
            }

            @Override
            public Object getItem(int position) {
                return allPersonDataArray.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if(view == null){
                    view = layoutInflater.inflate(R.layout.sheet_list_item,null);
                }
                TextView tvSheetItemPersonName = (TextView) view.findViewById(R.id.tvSheetPersonName);
                TextView tvdeposit = (TextView) view.findViewById(R.id.tvSheetDeposit);
                TextView tvmeal = (TextView) view.findViewById(R.id.tvSheetMeal);
               // TextView tvMonth = (TextView) view.findViewById(R.id.tvMonth);


                tvSheetItemPersonName.setText(allPersonDataArray.get(position).getName());
                tvdeposit.setText("Deposit: "+allPersonDataArray.get(position).getDeposit()+" Tk");
                tvmeal.setText("Meal: " + allPersonDataArray.get(position).getMeal());
               // tvMonth.setText("Month");

                return view;
            }
        };

       lvSheet.setAdapter(adapter);

        lvSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent intent = new Intent(Sheet.this,DescriptoinActivity.class);
                 intent.putExtra("name",allPersonDataArray.get(position).getName());
                 intent.putExtra("description", allPersonDataArray.get(position).getDescription());
                 startActivity(intent);
            }
        });

        if (isNetworkAvailable()) {
            // start post thread
            pd = ProgressDialog.show(this, "", "Loading...", false, true);


            RequestThread p = new RequestThread();
            p.start();
        }

    }


    class RequestThread extends Thread {


        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(URL);


            List param = new ArrayList<BasicNameValuePair>();
            param.add(new BasicNameValuePair("house", house_code));


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
                        JSONArray jsonArray = jOb.getJSONArray("books");
                        int size = jsonArray.length();

                        for (int i = 0; i < size; i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            String name = jobject.getString("name");
                            String deposit = jobject.getString("deposit");
                            String meal = jobject.getString("meal");
                            String des = jobject.getString("description");
                            String due = jobject.getString("due");
                            int due_pay = jobject.getInt("due_pay");

                            int id = jobject.getInt("id");

                            MealPerson mealPerson = new MealPerson(id, name,house_code, deposit, meal, des, due,due_pay);
                            allPersonDataArray.add(mealPerson);

                            handler.sendEmptyMessage(success);
                        }
                    } else {
                        handler.sendEmptyMessage(FAILURE);
                    }
                }
                else {
                    handler.sendEmptyMessage(FAILURE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        android.os.Handler handler = new android.os.Handler() {
            @Override

            public void handleMessage(Message msg) {
                pd.dismiss();
                switch (msg.what) {
                    case SUCCESS:
                        adapter.notifyDataSetChanged();
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


}
