package com.asish.musik.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.asish.musik.R;

public class SettingFragment extends Fragment {

    Activity myActivity;
    Switch switchShake;
    static String MY_PREFS_NAME ="ShakeFeature";

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        myActivity.getActionBar().setTitle("Settings");
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        getActivity().setTitle("Settings");
        switchShake = view.findViewById(R.id.switchShake);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences preferences = myActivity.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        final boolean isAllowed= preferences.getBoolean("feature",false);
        if(isAllowed){
            switchShake.setChecked(true);
        }else{
            switchShake.setChecked(false);

        }

        switchShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences.Editor editor = myActivity.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = myActivity.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         myActivity = (Activity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            myActivity.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if(item != null){
            item.setVisible(false);
        }
    }

}
