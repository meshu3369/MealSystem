package com.example.meshu.mealsystem;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DescriptoinActivity extends ActionBarActivity {

    private String name,description;
    private ListView listView;

    ArrayList<String> arrayList;

    TextView tvperson;
    BaseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptoin);

        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");

        tvperson = (TextView) findViewById(R.id.tvPersonName);
        tvperson.setText("Shopping List("+name+")");

        arrayList = new ArrayList<String>();


       listView = (ListView) findViewById(R.id.descriptionList);
        adapter = new BaseAdapter() {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @Override
            public int getCount() {
                return arrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return arrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
               if(view == null){
                   view = inflater.inflate(R.layout.descrip_list_item,null);
               }
                TextView tvDescriptionItem = (TextView) view.findViewById(R.id.tvDescriptionItem);

                tvDescriptionItem.setText(arrayList.get(position));

                return view;
            }
        };
        listView.setAdapter(adapter);

        getArrayList();
    }


    public void getArrayList() {
        String str;
        int a=1,b=-1,count=1;
       // Log.d("string:",description);
        for (int i = 1; i < description.length(); i++) {
            if(description.charAt(i) == '%'){
                       b = i;

                      arrayList.add(count+" : "+description.substring(a,b));
                      count++;
                      adapter.notifyDataSetChanged();
                      Log.d("String", description.substring(a,b));
//                      adapter.notifyDataSetChanged();
                   a = b+2;
                  i++;
            }
        }
    }
}
