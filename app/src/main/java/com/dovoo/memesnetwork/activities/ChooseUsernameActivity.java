package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dovoo.memesnetwork.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class ChooseUsernameActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnOk;
    private String email;
    private TextInputLayout tilUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_username);
        email = getIntent().getStringExtra("email");
        init();
    }

    private void init() {
        getSupportActionBar().hide();
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etUsername);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.getText().length() <= 4) {
                    String msg = "Username at least 5 character long";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    tilUsername.setError(msg);

                } else {
                    insertUsername();
                }
            }
        });
    }

    private void insertUsername() {
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email", email);
            jsonObject.put("username", etUsername.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        AndroidNetworking.post(BuildConfig.API_URL + "insertusername")
//                .addJSONObjectBody(jsonObject)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        try {
//                            JSONObject data = response.getJSONObject("data");
//                            SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_NAME, data.getString("username"));
//                            SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_ID, data.getInt("id"));
//                            SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_EMAIL, email);
//                            SharedPreferenceUtils.setPrefs(getApplicationContext(), SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, true);
//                            Toast.makeText(getApplicationContext(),"Welcome "+data.getString("username")+" :)",Toast.LENGTH_LONG).show();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                        System.out.print("error");
//                        Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
//                        tilUsername.setError(anError.getErrorBody());
//                    }
//                });
    }
}
