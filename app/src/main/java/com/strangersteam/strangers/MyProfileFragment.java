package com.strangersteam.strangers;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener{


    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //todo Do wyświetlania obrazka do profilu użyć Picasso!

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        Button button = (Button)view.findViewById(R.id.my_profile_my_events_button);
        button.setOnClickListener(this);

        return view;
    }


    //Obsługa buttonów w fragmencie
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.my_profile_my_events_button:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }

    }
}
