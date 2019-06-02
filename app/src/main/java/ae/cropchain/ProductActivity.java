package ae.cropchain;




import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        long prodId = 0;
        String prodName = "";
        int prodRate = 0;
        String prodRateUpdatedAt = "";


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(!bundle.isEmpty()) {
            if (bundle.containsKey("prodId")) {
                prodId = bundle.getLong("prodId");
            }
            if (bundle.containsKey("prodName")) {
                prodName = bundle.getString("prodName");
                TextView lblListHeader = (TextView) findViewById(R.id.lblListHeader);
                lblListHeader.setText(prodName);
            }
            if (bundle.containsKey("prodRate")) {
                prodRate = bundle.getInt("prodRate");
                TextView textViewRateOld = (TextView) findViewById(R.id.textViewRateOld);
                textViewRateOld.setText(Integer.toString(prodRate));
            }
            if (bundle.containsKey("prodRateUpdatedAt")) {
                prodRateUpdatedAt = bundle.getString("prodRateUpdatedAt");
                TextView textViewLastUpdateDate = (TextView) findViewById(R.id.textViewLastUpdateDate);
                textViewLastUpdateDate.setText(Common.getDateFormatted(prodRateUpdatedAt,"yyyy-MM-dd HH:mm:ss","hh:mm a dd/MM/yyyy"));
            }
        }
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        final long finalProdId = prodId;
        final String finalProdName = prodName;
        final int finalProdRate = prodRate;
        final String finalProdRateUpdatedAt = prodRateUpdatedAt;
        btnUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                final String Rate = ((EditText) findViewById(R.id.editTextRateNew)).getText().toString();
                int intRate;
                if (Rate.isEmpty()) {
                    intRate = 0;
                } else {
                    intRate = Integer.parseInt(Rate);
                }
                if ( intRate <= 0 || intRate == finalProdRate) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_rate), Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ProductActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
                    } else {
                        builder = new AlertDialog.Builder(ProductActivity.this);
                    }
                    builder.setTitle(getString(R.string.diag_rate))
                            .setMessage(getString(R.string.rate_update_confirmation))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with Update
                                    final ProgressDialog mDialog = new ProgressDialog(ProductActivity.this);
                                    mDialog.setMessage(getString(R.string.update_in_process));
                                    mDialog.setCancelable(false);
                                    mDialog.show();


//                                Toast.makeText(getApplicationContext(), getString(R.string.update_in_process), Toast.LENGTH_SHORT).show();


                                    JsonObject json = new JsonObject();
                                    json.addProperty("Email", MainActivity.user.getEmail());
                                    json.addProperty("Password", MainActivity.user.getPassword());
                                    json.addProperty("Id", Long.toString(finalProdId));
                                    json.addProperty("Name", finalProdName);
                                    json.addProperty("OldRate", finalProdRate);
                                    json.addProperty("Rate", Rate);
                                    json.addProperty("RateUpdatedAt", finalProdRateUpdatedAt);

                                    JSONObject request = new JSONObject();
                                    try {
                                        request = new JSONObject(json.toString());
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                    String url = getString(R.string.product_rate_update_url);

                                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.e("Response***", response.toString());
                                            try {
                                                JSONObject resultsOBJ = response.getJSONObject("results");
                                                String msg = resultsOBJ.getString("msg");
                                                String strRateUpdatedAt = resultsOBJ.getString("RateUpdatedAt");
                                                if (msg.equals("Success")) {
                                                    DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                                                    Product prod = dh.getProduct(finalProdId);
                                                    prod.setRate(Integer.parseInt(Rate));
                                                    prod.setRateUpdatedAt(strRateUpdatedAt);
                                                    prod.setRateUpdatedByUserId(MainActivity.user.getId());
                                                    dh.updateProduct(prod);
                                                    dh.closeDB();
                                                    Toast.makeText(getApplicationContext(), getString(R.string.rate_updated), Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), msg.toString(), Toast.LENGTH_LONG).show();
                                                    mDialog.cancel();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                                            mDialog.cancel();
                                        }
                                    });
                                    queue.add(jsObjRequest);
                                    // Request Data List from Server End
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Cancel Action
                                    Toast.makeText(getApplicationContext(), getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
