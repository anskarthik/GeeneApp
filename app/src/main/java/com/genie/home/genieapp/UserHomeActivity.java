package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.genie.home.genieapp.auth.LoginCredentials;
import com.genie.home.genieapp.auth.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.genie.home.genieapp.LoginActivity.MyPREFERENCES;

public class UserHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DevicesFragment.OnFragmentInteractionListener {

    private String username;

    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Context context;

    @BindView(R.id.toolbarMain)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_devices_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_room_black_24dp);

        setSupportActionBar(toolbar);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        handler = new Handler(context.getMainLooper());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setupViewPager(ViewPager viewPager) {
        pagerAdapter.addFragment(new Tab1Fragment());
        pagerAdapter.addFragment(new Tab2Fragment());
        pagerAdapter.addFragment(new Tab3Fragment());
        viewPager.setAdapter(pagerAdapter);
    }

    public void logout() {
        UserService.attemptLogout(
                new MyRunnable<LoginCredentials>() {
                    @Override
                    public void run(LoginCredentials loginCredentials) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", null);
                        editor.putString("password", null);
                        editor.apply();

                        Intent iUsrHome = new Intent(UserHomeActivity.this, LoginActivity.class);
                        startActivity(iUsrHome);

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context,
                                        "Logout successful !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                },
                new MyRunnable<Exception>() {
                    @Override
                    public void run(Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context,
                                        "Logout failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_help:
                break;
            case R.id.nav_email_us:
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(getApplicationContext(), "Fragment Clicked", Toast.LENGTH_SHORT).show();
    }
}
