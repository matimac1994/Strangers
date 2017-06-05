package com.strangersteam.strangers;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        AddEventTimePickerFragment.OnCompleteListener{

    private GoogleMap mMap;
    private Marker mMarker;
    private LatLng markerLatLng;

    private Calendar calendar;
    private int hour;
    private int minute;

    private int durationHour;
    private int durationMinute;

    private EditText titleET;
    private EditText placeET;
    private EditText descriptionET;
    private TextView timeSelectedTV;
    private Button addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_event);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeToolbar();
        initializeValuesAndShow();
        initializeNumberPickers();

        Intent intent = this.getIntent();
        markerLatLng = (LatLng) intent.getExtras().getParcelable("MARKER_POSITION");

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.add_event_map_view);
        mapFragment.getMapAsync(this);

        addEventButton = (Button) findViewById(R.id.add_event_add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddEvent();
            }
        });

        titleET = (EditText) findViewById(R.id.add_event_title);
        placeET = (EditText) findViewById(R.id.add_event_where);
        descriptionET = (EditText) findViewById(R.id.add_event_describe);
    }

    private void initializeToolbar(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeValuesAndShow(){
        timeSelectedTV = (TextView) findViewById(R.id.add_event_time_selected);
        calendar = GregorianCalendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        setUpTimeSelectedTV();

        durationHour = 1;
        durationMinute = 0;
    }

    private void initializeNumberPickers() {
        NumberPicker numberPickerHour = (NumberPicker) findViewById(R.id.add_event_number_picker_hour);
        NumberPicker numberPickerMinute = (NumberPicker) findViewById(R.id.add_event_number_picker_minute);

        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);

        numberPickerMinute.setMaxValue(0);
        numberPickerMinute.setMaxValue(59);

        numberPickerHour.setValue(durationHour);
        numberPickerMinute.setValue(durationMinute);

        numberPickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                durationHour = newVal;
            }
        });

        numberPickerMinute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                durationMinute = newVal;
            }
        });
    }

    private void setUpTimeSelectedTV(){
        calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        timeSelectedTV.setText(new SimpleDateFormat("HH:mm", new Locale("pl", "PL")).format(calendar.getTimeInMillis()));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        if(markerLatLng != null){
            mMarker = mMap.addMarker(new MarkerOptions().position(markerLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 12));
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new AddEventTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onComplete(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        setUpTimeSelectedTV();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("HOUR", hour);
        outState.putInt("MINUTE", minute);

        outState.putInt("DURATION_HOUR", durationHour);
        outState.putInt("DURATION_MINUTE", durationMinute);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        hour = savedInstanceState.getInt("HOUR");
        minute = savedInstanceState.getInt("MINUTE");
        setUpTimeSelectedTV();

        durationHour = savedInstanceState.getInt("DURATION_HOUR");
        durationMinute = savedInstanceState.getInt("DURATION_MINUTE");
        initializeNumberPickers();
    }

    private void onClickAddEvent() {
        addEventButton.setEnabled(false);
        if(!validate()){
            //todo jakies errory powyswietlac?
        }else{
            addEventRequest();
        }
    }

    private boolean validate() {
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }

        return true;
    }

    private void addEventRequest() {

        Map<String, String> params = addEventParams();

        JsonObjectRequest jsonObjectRequest = createAddEventRequest(params);

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Map<String, String> addEventParams() {
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("latitude",Double.toString(markerLatLng.latitude));
        params.put("longitude",Double.toString(markerLatLng.longitude));
        params.put("dateStart", Long.toString(calendar.getTimeInMillis()));
        params.put("durationHours", Integer.toString(durationHour));
        params.put("title", titleET.getText().toString());
        params.put("place", placeET.getText().toString());
        params.put("description", descriptionET.getText().toString());
        return params;
    }

    private JsonObjectRequest createAddEventRequest(Map<String, String> params) {
        return new AuthJsonObjectRequest(
                getApplicationContext(),
                Request.Method.POST,
                ServerConfig.ADD_EVENT,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            addEventButton.setEnabled(true);
                            Long eventId = response.getLong("eventId");
                            System.out.println(eventId);
                            goToEventActivity(eventId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Wystąpił nieoczekiwany błąd. Został on zgłoszony.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        addEventButton.setEnabled(true);
                        onAddEventError(error);
                    }
                }
        );
    }

    private void goToEventActivity(Long eventId) throws JSONException {

        Intent intent = new Intent(getApplicationContext(),ShowEventActivity.class);
        intent.putExtra(ShowEventActivity.EVENT_ID,eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void onAddEventError(VolleyError error) {
        Log.e(AddEventActivity.class.getName(),"errooor: " + error.getMessage());
        Toast.makeText(getApplicationContext(),"errooor: " + error.getMessage(),Toast.LENGTH_LONG).show();
    }

}
