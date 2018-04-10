package com.edmundsshillong.edmundscollege;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edmundsshillong.edmundscollege.Adapter.DownloadAdapter;
import com.edmundsshillong.edmundscollege.DB_and_Http.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static com.edmundsshillong.edmundscollege.Config.base_url;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    Context c;
    View rootView;
    LayoutInflater L_inflater;
    RecyclerView downloadList;
    CardView cardView;
    Spinner category;

    List<String> dept_no = new ArrayList<>();
    String selDeptNo="";
    List<String> dept_name = new ArrayList<>();
    List<String> down_title = new ArrayList<>();
    List<String> file_link = new ArrayList<>();
    DownloadAdapter downloadAdapter;

    //private SwipeRefreshLayout swipeRefreshLayout;
    private String urlGetDeptList=base_url+"downloadGetDeptlist.php";
    private String urlGetDeptDownList=base_url+"downloadDeptDownlist.php";

    public DownloadFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        L_inflater=inflater;
        c = inflater.getContext();
        getActivity().setTitle("Downloads");
        rootView= inflater.inflate(R.layout.fragment_download, container, false);

        downloadList=rootView.findViewById(R.id.downList);
        //  swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_download);
        // swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        downloadList.setLayoutManager(layoutManager);
        dept_name.clear();
        dept_no.clear();
        cardView=rootView.findViewById(R.id.cv_download);
        category=rootView.findViewById(R.id.spinner_download);
        category.setOnItemSelectedListener(this);

          setupSpinner();
        downloadList.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if(velocityY>0) {
                    if(down_title.size()>7)
                    cardView.setVisibility(GONE);
                }
                else{

                    cardView.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        //
        return rootView;
        //turn inflater.inflate(R.layout.fragment_download, container, false);

    }

    private void setupSpinner() {

        //List the dept names in the category
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlGetDeptList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                dept_no.add(jsonObject.getString("dept_no"));
                                dept_name.add(jsonObject.getString("dept_name"));
                            }
                            Toast.makeText(getContext(),"Got"+dept_name,Toast.LENGTH_SHORT).show();
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_attendance,dept_name);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            category.setAdapter(dataAdapter);
                            category.setSelection(0);


                        }catch(Exception e){
                            Toast.makeText(c,"ERROR",Toast.LENGTH_SHORT).show();
                        }
                        //swipeRefreshLayout.setRefreshing(false);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c,"Connection Interrupted"+error,Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        } )
                ;

        MySingleton.getInstance(c).addToRequestQueue(stringRequest);


    }

    private void checkserver() {
        //swipeRefreshLayout.setRefreshing(true);
        downloadList.setAdapter(null);
        //dept_no.clear();
        //dept_name.clear();

        down_title.clear();
        file_link.clear();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGetDeptDownList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                down_title.add(jsonObject.getString("title"));
                                file_link.add(jsonObject.getString("filename"));

                            }
                            downloadAdapter = new DownloadAdapter(c,down_title,file_link);
                            downloadList.setAdapter(downloadAdapter);

                        }catch(Exception e){
                            Toast.makeText(c,"ERROR",Toast.LENGTH_SHORT).show();
                        }
                        //swipeRefreshLayout.setRefreshing(false);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c,"Connection Interrupted",Toast.LENGTH_SHORT).show();
            }
        } )
        {
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("dept_no",selDeptNo);
                return params;
            }
        };

        MySingleton.getInstance(c).addToRequestQueue(stringRequest);
    }

    @Override
    public void onRefresh() {
        checkserver();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selDeptNo=dept_no.get(position);
            checkserver();
        }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}
