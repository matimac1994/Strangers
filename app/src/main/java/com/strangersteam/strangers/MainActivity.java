package com.strangersteam.strangers;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.strangersteam.strangers.notifications.FewEventMsgNotificationBuildStrategy;
import com.strangersteam.strangers.notifications.FewMyEventsMsgNotificationBuildStrategy;
import com.strangersteam.strangers.serverConn.AuthStringRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String FRAGMENT_ID_EXTRA = "FRAGMENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = chooseStartFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_map, fragment).commit();

    }

    private Fragment chooseStartFragment() {
        Fragment fragment;
        int fragmentIdExtra = getIntent().getIntExtra(FRAGMENT_ID_EXTRA,-1);
        if(fragmentIdExtra != -1){
            if(fragmentIdExtra == FewMyEventsMsgNotificationBuildStrategy.FEW_MY_EVENTS_NOTIFICATION_ID){
                fragment = new MyEventsFragment();
            }else if(fragmentIdExtra == FewEventMsgNotificationBuildStrategy.FEW_ATTENDING_EVENTS_NOTIFICATION_ID){
                fragment = new MyAttendEventsFragment();
            }
            else
                fragment =new GMapFragment();
        }else{
            fragment = new GMapFragment();
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //czy trzeba te fragmenty za kazdym razem tworzyc? moze szybkosc zwrosnąc gdyby je zapisać po utworzeniu?
        FragmentManager fragmentManager = getSupportFragmentManager();
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.fragment_map, new GMapFragment()).commit();
        } else if (id == R.id.nav_profile){
            fragmentManager.beginTransaction().replace(R.id.fragment_map, new MyProfileFragment()).commit();
        } else if (id == R.id.nav_my_events){
            fragmentManager.beginTransaction().replace(R.id.fragment_map, new MyEventsFragment()).commit();
        } else if (id == R.id.nav_my_attend_events){
            fragmentManager.beginTransaction().replace(R.id.fragment_map, new MyAttendEventsFragment()).commit();
        } else if (id == R.id.nav_logout) {
            logoutRequest();
        }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void logoutRequest(){
        String logoutUrl = ServerConfig.LOGOUT;

        AuthStringRequest authStringRequest = new AuthStringRequest(
                getApplicationContext(),
                Request.Method.POST,
                logoutUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogoutHandler.logout(getApplicationContext());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Niepowodzenie", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(authStringRequest);
    }
}
