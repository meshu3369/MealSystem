package com.example.meshu.mealsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class Result extends ActionBarActivity {
   // private android.support.v7.widget.Toolbar toolbar;

    private ListView listView;

    BaseAdapter adapter;
    ArrayList<MealPerson> allPersonDataArray;

    private TextView tvTotalcost;


    public static final String URL = "http://meshu.net84.net/meal/getAllMealInfo.php";
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;
    private ProgressDialog pd;
    /*
 *
 * class variable
  *
  * */
    private String house_code,totalDeposit,totalMeal,permeal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        ///toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
       // setSupportActionBar(toolbar);


        /*code for back menu
        *
        * */
       // getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        house_code = getIntent().getStringExtra("HOUSE");


        listView = (ListView) findViewById(R.id.lvResultListview);

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
                    view = layoutInflater.inflate(R.layout.result_list_item,null);
                }
                TextView tvRightSide = (TextView) view.findViewById(R.id.tvRightSide);
                TextView tvLeft = (TextView) view.findViewById(R.id.tvLeftSide);

                MealPerson m = allPersonDataArray.get(position);
                int deposit = Integer.parseInt(m.getDeposit());
                int meal = Integer.parseInt(m.getMeal());
                int netCost = meal * ((int)Float.parseFloat(permeal));
                int netProfit = netCost - deposit;
                tvRightSide.setText("Deposit: "+allPersonDataArray.get(position).getDeposit()+" Tk\n"+
                    "Meal: "+allPersonDataArray.get(position).getMeal()+"\n"+
                     "Meal cost: "+netCost+" Tk\n"+"Final due: "+netProfit+" Tk");

                tvLeft.setText(allPersonDataArray.get(position).getName());

                return view;
            }
        };

        listView.setAdapter(adapter);

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
                    getTheTotalValue();
                    tvTotalcost = (TextView) findViewById(R.id.tvTotalCost);
                    tvTotalcost.setText("Total Amount: " + totalDeposit + " Tk\n" + "Total Meal: " + totalMeal + "\n" + "Per Meal: " + permeal + " Tk");

                    adapter.notifyDataSetChanged();
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(), "404:: error not found.", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    private void getTheTotalValue() {
        long deposit=0,meal=0,sumDeposit=0,sumMeal=0;
        float perMeal = 0;
        for (MealPerson m : allPersonDataArray
             ) {
            deposit = Long.parseLong(m.getDeposit());
            meal = Long.parseLong(m.getMeal());
            sumDeposit += deposit;
            sumMeal += meal;
        }
        perMeal = (float) sumDeposit / sumMeal;
        permeal = Float.toString(perMeal);
        totalDeposit = Long.toString(sumDeposit);
        totalMeal = Long.toString(sumMeal);

    }

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

        //noinspection SimplifiableIfStatement

        if(id == android.R.id.home){
           //NavUtils.navigateUpFromSameTask(this);
            /*Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);*/

        }

        return super.onOptionsItemSelected(item);
    }
}
