package com.example.meshu.mealsystem;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment  implements View.OnClickListener{
     public static  final String PREF_FILE_NAME = "testpref";
     public static  final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

     public ActionBarDrawerToggle mdrawerToggle;
     public DrawerLayout mDrawerLayout;

    public boolean mUserLearnedDrawer;
    public boolean mFromUser;
    private View containerview;
    LinearLayout goToResult,goToSheet,goToFinalResult;
    Button signOut;
    View finalButton;
    private TextView tvPersonName;
    private int is_admin;

    MyCommunicator communicator;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AfterLogin activity = (AfterLogin) getActivity();
        String email = activity.sendDataToFragment();
        is_admin = activity.sendAdminCheckToFragment();

        tvPersonName = (TextView) getActivity().findViewById(R.id.tvNavigationPersonName);
        tvPersonName.setText(email);


        communicator = (MyCommunicator) getActivity();
        goToResult = (LinearLayout) getActivity().findViewById(R.id.goToResut);
        goToSheet = (LinearLayout) getActivity().findViewById(R.id.goToSheet);
        goToFinalResult = (LinearLayout) getActivity().findViewById(R.id.goToFinalResult);
        signOut = (Button) getActivity().findViewById(R.id.signOut);

        goToResult.setOnClickListener(this);
        goToSheet.setOnClickListener(this);
        goToFinalResult.setOnClickListener(this);
        signOut.setOnClickListener(this);


        finalButton = getActivity().findViewById(R.id.goToFinalResult);

        if(is_admin == 1) {
            finalButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.goToResut:
                communicator.response("result");
                mDrawerLayout.closeDrawer(containerview);
                break;
            case R.id.goToSheet:
                communicator.response("sheet");
                mDrawerLayout.closeDrawer(containerview);
                break;
            case R.id.goToFinalResult:
                communicator.response("finalResult");
                mDrawerLayout.closeDrawer(containerview);
                break;
            case R.id.signOut:
                communicator.response("signout");
                mDrawerLayout.closeDrawer(containerview);
                break;

        }

    }


    /*
    *
    *
    * do not make any changes to the below code.
    *
    *
    *
    * */


    public void setUp(int fragmentId,DrawerLayout drawerLayout, final Toolbar toolbar){
            containerview = getActivity().findViewById(fragmentId);
             mDrawerLayout = drawerLayout;
            mdrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close ){
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if(!mUserLearnedDrawer){
                        mUserLearnedDrawer = true;
                        saveToPreference(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                    }
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    getActivity().invalidateOptionsMenu();

                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }
            };
        // if(!mUserLearnedDrawer && !mFromUser){
        //     mDrawerLayout.openDrawer(containerview);
        // }
           mDrawerLayout.setDrawerListener(mdrawerToggle);
         mDrawerLayout.post(new Runnable() {
             @Override
             public void run() {
                 mdrawerToggle.syncState();
             }
         });
     }

    public static  void saveToPreference(Context context,String preferenceName,String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
    public static String readFromPreference(Context context,String preferenceName,String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return  sharedPreferences.getString(preferenceName,defaultValue);
    }


}
