package com.edmundsshillong.edmundscollege;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edmundsshillong.edmundscollege.Account.Attendance_Report;
import com.edmundsshillong.edmundscollege.Application.Admission_Help;
import com.edmundsshillong.edmundscollege.Application.Fragment_Admission;
import com.edmundsshillong.edmundscollege.Application.Scan;
import com.edmundsshillong.edmundscollege.DB_and_Http.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    AppBarLayout.LayoutParams params;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager,deptViewPager;
    private TabLayout Tabs;
    MenuItem Admission;
    private FrameLayout mFrame;
    public String url="http://192.168.43.108/SEC/Applicant/Admission_Status.php";
    int dr;

    private DepartmentsPagerAdapter departmentsPagerAdapter;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        Tabs = (TabLayout)findViewById(R.id.tabs);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView title1=(TextView)findViewById(R.id.toolbar_title);
        Typeface myCustom = Typeface.createFromAsset(this.getAssets(),"fonts/OLD.ttf");
        title1.setTypeface(myCustom);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        /*********************************/
        mFrame = (FrameLayout)findViewById(R.id.containerDrawer);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerTabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Tabs.setupWithViewPager(mViewPager);

        deptViewPager=findViewById(R.id.containerDeptTabs);
        departmentsPagerAdapter= new DepartmentsPagerAdapter(getSupportFragmentManager());
     /*   Tabs.getTabAt(0).setIcon(R.drawable.tab_icon_home);
        Tabs.getTabAt(1).setIcon(R.drawable.tab_icon_schedule);
        Tabs.getTabAt(2).setIcon(R.drawable.tab_icon_acc);*/


        mFrame.setVisibility(View.GONE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        Menu menu =navigationView.getMenu();

        Admission = menu.findItem(R.id.nav_admission);
        Admission.setVisible(false);

        checkAdmission();
    }

    public void checkAdmission()
    {
    StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if(jsonObject.getString("status").equalsIgnoreCase("OPEN"))
                            Admission.setVisible(true);

                    }catch(Exception e){
                        //Toast.makeText(Attendance.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }

            }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            {
                Toast.makeText(MainActivity.this,"Connection Interrupted",Toast.LENGTH_LONG).show();
            }
        }
    } );

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            deptViewPager.setVisibility(GONE);
            Tabs.setTabMode(MODE_FIXED);
            Tabs.setVisibility(View.VISIBLE);
            mFrame.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            Tabs.setupWithViewPager(mViewPager);

            //item.setTitle("GG");

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            mViewPager.setAdapter(null);
            mFrame.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            Tabs.setVisibility(View.VISIBLE);
            Tabs.setTabMode(GRAVITY_FILL);
            deptViewPager.setVisibility(View.VISIBLE);
            deptViewPager.setAdapter(departmentsPagerAdapter);
            Tabs.setupWithViewPager(deptViewPager);
           /* Fragment4 fragInfo = new Fragment4();
            openfrag(fragInfo);*/

            /*Intent i = new Intent(MainActivity.this, Admission_Help.class);
            startActivity(i);*/

        } else if (id == R.id.nav_admission) {

            params.setScrollFlags(0);
            mViewPager.setAdapter(null);            
            mFrame.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            Tabs.setVisibility(View.GONE);
            deptViewPager.setVisibility(GONE);
            Fragment_Admission fragInfo = new Fragment_Admission();
            openfrag(fragInfo);

        } else if (id == R.id.nav_manage) {

            mViewPager.setAdapter(null);
            mFrame.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            Tabs.setVisibility(View.GONE);
            deptViewPager.setVisibility(GONE);
            Fragment_endless fragInfod = new Fragment_endless();
            openfrag(fragInfod);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openfrag(final Fragment f)
    {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                mFragmentTransaction.replace(R.id.containerDrawer,f).commit();
            }
        };
        handler.postDelayed(r,0);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 0 : return new Fragment1();
                case 1 : return new Fragment2();
                case 2 : return new Fragment3();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "News";
                case 1:
                    return "Schedules";
                case 2:
                    return "Account";
            }
            return null;
        }
    }
    
     public class DepartmentsPagerAdapter extends FragmentPagerAdapter {

        public DepartmentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            Fragment dept;
            switch (position){
                case 0 : b.putString("stream","Arts"); dept= new DeptFragment(); dept.setArguments(b); return dept;
                case 1 : b.putString("stream","Commerce"); dept= new DeptFragment(); dept.setArguments(b); return dept;//CommerceFragment();
                case 2 : b.putString("stream","Science"); dept= new DeptFragment(); dept.setArguments(b); return dept;//ScienceFragment();
                case 3 : b.putString("stream","Pro"); dept= new DeptFragment(); dept.setArguments(b); return dept;//ProFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Arts";
                case 1:
                    return "Commerce";
                case 2:
                    return "Science";
                case 3:
                    return "Professional";
            }
            return null;
        }
    }
}
