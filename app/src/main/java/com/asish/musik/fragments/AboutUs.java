package com.asish.musik.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asish.musik.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends Fragment {

    Activity myActivity;
    TextView about;

    public AboutUs() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if(item != null) {
            item.setVisible(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


            View view  = inflater.inflate(R.layout.fragment_about_us, container, false);
            getActivity().setTitle("About Us");
            about = (TextView) view.findViewById(R.id.about_us_stuff);

            return  view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String text = "Made with ;_; by:" + "\n\n" + "  Asish Samantaray" + "\n\n" +"  Abhinav Panigrahi" + "\n\n" + "  Omm Mishra" + "\n\n" + "  Swagat Parija";

        about.setText(text);

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




}
