package ae.cropchain;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText etEmail = (EditText) findViewById(R.id.email);
        final EditText etPass = (EditText) findViewById(R.id.pass1);

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.signup_not_present), Toast.LENGTH_LONG).show();
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.email_empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.password_empty), Toast.LENGTH_LONG).show();
                    return;
                }

                DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                final  User usr = new User();
                usr.setEmail(email);
                usr.setPassword(Common.get_SHA_512_SecurePassword(pass));

// Volley JSON Request Code Started
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = getString(R.string.signin_url);
//Gson Prepare JSON from Object Started
                Gson gson = new Gson();
                String json = gson.toJson(usr);
                JSONObject request = new JSONObject();
                try {
                    request = new JSONObject(json);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//Gson Prepare JSON from Object Ended
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response***",response.toString());
                        try {
                            JSONObject resultsOBJ = response.getJSONObject("results");
                            String msg = resultsOBJ.getString("msg");
                            if(msg.equals("Success")){
                                DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                                if(usr.getId() == 0){
                                    usr.setId(resultsOBJ.getLong("Id"));
                                    usr.setName(resultsOBJ.getString("Name"));
                                    dh.createUser(usr);
                                }
                                Toast.makeText(getApplicationContext(), getString(R.string.successful_login), Toast.LENGTH_LONG).show();
                                MainActivity.user = usr;
                                // Do after Login Activity
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
// Volley JSON Request Code Ended
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem mLogout = menu.findItem(R.id.mLogout);
        mLogout.setVisible(false);

        MenuItem mRefresh = menu.findItem(R.id.mRefresh);
        mRefresh.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
        switch (item.getItemId()){
            case R.id.mExit:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
