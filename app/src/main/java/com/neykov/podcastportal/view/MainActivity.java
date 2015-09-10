package com.neykov.podcastportal.view;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.DrawerLayoutProvider;
import com.neykov.podcastportal.view.discover.view.ExploreFragment;

public class MainActivity extends AppCompatActivity implements DrawerLayoutProvider, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();

        if(savedInstanceState == null){
            openExploreScreen();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public DrawerLayout getDrawerLayout() {
        return mNavigationDrawer;
    }

    private void setupNavigationDrawer(){
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mNavigationDrawer.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()){
            case R.id.navigation_home:
                clearNavigationStack();
                break;
            case R.id.navigation_about:
                break;
            default:
                return false;
        }

        return true;
    }

    private void openExploreScreen(){
        clearNavigationStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, ExploreFragment.newInstance(), ExploreFragment.TAG)
                .addToBackStack(ExploreFragment.TAG)
                .commit();
    }

    private void clearNavigationStack(){
        FragmentManager manager = getSupportFragmentManager();
        while (manager.getBackStackEntryCount() > 0){
            manager.popBackStackImmediate();
        }
    }

}