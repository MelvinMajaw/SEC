package com.edmundsshillong.edmundscollege;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edmundsshillong.edmundscollege.Adapter.DeptListAdapter;
import com.edmundsshillong.edmundscollege.Adapter.NewsAdapter;
import com.edmundsshillong.edmundscollege.DB_and_Http.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.edmundsshillong.edmundscollege.Config.base_url;

/**
 * Created by Peace on 09/04/2018.
 */

public class DeptFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    Context c;
    View rootView;
    LayoutInflater L_inflater;
    RecyclerView deptList;
    String stream;
    Bundle b;
    List<String> dept_name = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    String url=base_url+"deptlist.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        L_inflater=inflater;
        c = inflater.getContext();
        b=getArguments();
        stream=b.getString("stream");


        rootView = inflater.inflate(R.layout.fragment_department, container, false);
        deptList = (RecyclerView)rootView.findViewById(R.id.lvDeptsName);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_dept);
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        deptList.setLayoutManager(layoutManager);
        deptList.setNestedScrollingEnabled(false);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkserver();
                                    }
                                }
        );


        return rootView;
    }

    @Override
    public void onRefresh() {
        checkserver();
    }

    private void checkserver() {
        swipeRefreshLayout.setRefreshing(true);
        deptList.setAdapter(null);
        dept_name.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                dept_name.add(jsonObject.getString("dept"));
                            }
                           // Toast.makeText(c,"DEPT "+dept_name,Toast.LENGTH_SHORT).show();
                            DeptListAdapter deptListAdapter= new DeptListAdapter(c,dept_name);
                            deptList.setAdapter(deptListAdapter);

                        }catch(Exception e){
                            Toast.makeText(c,"ERROR",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
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
                params.put("stream",stream);
                return params;
            }
        };

        MySingleton.getInstance(c).addToRequestQueue(stringRequest);
    }
}
