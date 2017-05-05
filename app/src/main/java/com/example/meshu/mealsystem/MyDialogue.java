package com.example.meshu.mealsystem;

import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
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

/**
 * Created by Engr Meshu on 8/21/2015.
 */
public class MyDialogue extends DialogFragment {

    public static final String UrL = "http://meshu.net84.net/meal/deleteAllDeposit.php";
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
   String name;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         AlertDialog.Builder  builder= new AlertDialog.Builder(getActivity());

          builder.setMessage("Delete all member Record from Database?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if (isNetworkAvailable()) {
                      // start post thread
                      RequestThread2 p = new RequestThread2();
                      p.start();
                  }
                  AfterLogin c = (AfterLogin) getActivity();
                  c.dialogueRespons();
              }
          }).setNegativeButton("No", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {

              }
          });
        AlertDialog dialog = builder.create();
        return dialog;
    }


    class RequestThread2 extends Thread {

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(UrL);


            List param = new ArrayList<BasicNameValuePair>();
            param.add(new BasicNameValuePair("name", name));

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
            switch (msg.what){
                case SUCCESS:

                    break;
                case FAILURE:
                     break;
            }

        }
    };

    private boolean isNetworkAvailable(){
        ConnectivityManager cManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if(networkInfo != null){
            if(networkInfo.isAvailable() && networkInfo.isConnected()){

                return true;
            }
        }
        return false;
    }

    public void name(String name){
         this.name = name;
    }
}
