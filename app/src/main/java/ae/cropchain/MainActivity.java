package ae.cropchain;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static User user;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<Header> listDataHeader;
    HashMap<Header, List<Product>> listDataChild;
    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullToRefresh = findViewById(R.id.pullToRefresh);

        final DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
        // Start SignUpActivity or Login Activity
        if(this.user == null || this.user.getEmail() == null){
            this.user = dh.getLoggedInUser();
            if(this.user == null || this.user.getEmail() == null) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        String userEmail = this.user.getEmail();
        ((TextView) findViewById(R.id.textMsg)).setText(getString(R.string.loggedin_msg) + userEmail);


        // LIST VIEW PREPRATION START

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                dh.emptyProducts();
                prepareListData();
            }
        });
        // preparing list data
        prepareListData();

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
//                 Toast.makeText(getApplicationContext(),
//                 "Group Clicked*** " + listDataHeader.get(groupPosition),
//                 Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(MainActivity.this,
//                        listDataHeader.get(groupPosition) + " Expanded ***",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(MainActivity.this,
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
//                Toast.makeText(
//                        getApplicationContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
                Product prod = (Product) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("prodId",prod.getId());
                bundle.putString("prodName", prod.getName());
                bundle.putInt("prodRate",prod.getRate());
                bundle.putString("prodRateUpdatedAt", prod.getRateUpdatedAt());
                bundle.putLong("prodRateUpdatedByUserId", prod.getRateUpdatedByUserId());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

//                listDataHeader.add("kk..");
//                List<String> comingSoon = new ArrayList<String>();
//                comingSoon.add("2 Guns");
//                comingSoon.add("The Smurfs 2");
//                comingSoon.add("The Spectacular Now");
//                comingSoon.add("The Canyons");
//                comingSoon.add("Europa Report");
//
//                listDataChild.put(listDataHeader.get(3), comingSoon); // Header, Child data
//
//                listAdapter.setNewItems(listDataHeader, listDataChild);
                return false;
            }
        });

        dh.closeDB();
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        pullToRefresh.setRefreshing(true);

        List<Header> lsHeader = new ArrayList<Header>();
        Header h = new Header(1, "Current Rates");
        lsHeader.add(h);

        listDataHeader = lsHeader;
        listDataChild = new HashMap<Header, List<Product>>();

        final DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
        if(dh.getProductCount() == 0){
            // Request Product List from Server Start
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = getString(R.string.product_url);

            Gson gson = new Gson();
            String json = gson.toJson(MainActivity.user);
            JSONObject request = new JSONObject();
            try {
                request = new JSONObject(json);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Response***",response.toString());
                    List<Product> childProducts = new ArrayList<Product>();
                    try {
                        JSONObject resultsOBJ = response.getJSONObject("results");
                        String msg = resultsOBJ.getString("msg");
                        if(msg.equals("Success")){
                            JSONArray resultProdArr = resultsOBJ.getJSONArray("Products");
                            for (int i=0; i < resultProdArr.length(); i++) {
                                JSONObject prodObj = resultProdArr.getJSONObject(i);
                                Product prod = new Product(prodObj.getLong("Id"),prodObj.getString("Name"),
                                        prodObj.getInt("Rate"),prodObj.getString("RateUpdatedAt"),
                                        prodObj.getInt("RateUpdatedByUserId"), prodObj.getInt("IsActive"), prodObj.getInt("SortOrder"));
                                childProducts.add(prod);
                                dh.createProduct(prod);
                            }
                            listDataChild.put((Header) listDataHeader.get(0),childProducts);
                            listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild, dh.getProductRateDigits());
                            // setting list adapter
                            expListView.setAdapter(listAdapter);

                            Toast.makeText(getApplicationContext(), getString(R.string.product_refresh_completed), Toast.LENGTH_SHORT).show();
                            pullToRefresh.setRefreshing(false);

                        }else{
                            Toast.makeText(getApplicationContext(), msg.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                }
            });
            queue.add(jsObjRequest);
            // Request Data List from Server End
        } else{
            List<Product> childProducts = dh.getProducts();
            listDataChild.put((Header) listDataHeader.get(0),childProducts);
            listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild, dh.getProductRateDigits());
            // setting list adapter
            expListView.setAdapter(listAdapter);
            pullToRefresh.setRefreshing(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.addAction);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
        switch (item.getItemId()){
            case R.id.mRefresh:
                dh.emptyProducts();
                prepareListData();
                return true;
            case R.id.mLogout:
                dh.emptyDB();
                MainActivity.user = null;
                finish();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                return true;
            case R.id.mExit:
                finish();
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}
