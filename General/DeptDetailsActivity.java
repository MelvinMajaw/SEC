package com.edmundsshillong.edmundscollege.General;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edmundsshillong.edmundscollege.DB_and_Http.MySingleton;
import com.edmundsshillong.edmundscollege.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.edmundsshillong.edmundscollege.Config.base_url;

public class DeptDetailsActivity extends AppCompatActivity {

    Bundle b;
    String deptName;

    TextView desc,name,courses,faculty;
    ImageView facPhoto;
    String url,deptProfile,courseinfo,facinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_details);

        //Toast.makeText(getApplicationContext(),"Name "+deptName,Toast.LENGTH_SHORT).show();
        Toolbar toolbar=(Toolbar)findViewById(R.id.app_bar_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView Head = (TextView)findViewById(R.id.toolbar_title);
        Typeface myCustom = Typeface.createFromAsset(this.getAssets(),"fonts/OLD.ttf");
        Head.setTypeface(myCustom);

        b=getIntent().getExtras();
        deptName=b.getString("deptName");

        desc=findViewById(R.id.tvDept_desc);
        name=findViewById(R.id.tvDept_name);
        facPhoto=findViewById(R.id.imgDept_profile);
        courses=findViewById(R.id.tvDept_courseinfo);
        faculty=findViewById(R.id.tvDept_facinfo);

        url=base_url+"deptinfo.php";
        name.setText(deptName+" Department");
        checkserver();


    }

    private void checkserver() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                if(jsonObject.getString("code").equals("deptinfo")) {
                                    desc.setText(Html.fromHtml(jsonObject.getString("desc")));
                                    Picasso.with(getApplicationContext()).load(jsonObject.getString("pic")).into(facPhoto);
                                    deptProfile = jsonObject.getString("pdf");
                                }
                                else if(jsonObject.getString("code").equals("courseinfo"))
                                {
                                    courseinfo=jsonObject.getString("details");
                                    courses.setText(Html.fromHtml(courseinfo));
                                }
                                else if(jsonObject.getString("code").equals("faculty"))
                                {
                                    facinfo=jsonObject.getString("details");
                                    faculty.setText(Html.fromHtml(facinfo));
                                }

                            }


                        }catch(Exception e){
                            Toast.makeText(getApplicationContext(),"ERROR"+e,Toast.LENGTH_SHORT).show();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Connection Interrupted",Toast.LENGTH_SHORT).show();
            }
        } )
        {
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("dept",deptName);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    public void openPdf(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(deptProfile));
        startActivity(i);
    }
}
