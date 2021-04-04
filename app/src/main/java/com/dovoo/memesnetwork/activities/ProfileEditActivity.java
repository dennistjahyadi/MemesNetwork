package com.dovoo.memesnetwork.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileEditActivity extends AppCompatActivity {

    private LinearLayout linBtnBack;
    private TextView tvBtnChangeProfilePic, tvBtnSave;
    private TextInputLayout tilUsername;
    private EditText etUsername;
    private String email;
    private FrameLayout loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        email = SharedPreferenceUtils.getPrefs(getApplicationContext()).getString(SharedPreferenceUtils.PREFERENCES_USER_EMAIL, "");

        loadingBar = findViewById(R.id.loadingBar);
        linBtnBack = findViewById(R.id.linBtnBack);
        tvBtnChangeProfilePic = findViewById(R.id.tvBtnChangeProfilePic);
        tvBtnSave = findViewById(R.id.tvBtnSave);
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etUsername);

        linBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvBtnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
            }
        });

        tvBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doEditProfile();
            }
        });
    }

    private void doEditProfile() {
        loadingBar.setVisibility(View.VISIBLE);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("username", etUsername.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        AndroidNetworking.post(BuildConfig.API_URL + "editprofile")
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
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        finish();
//                        loadingBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                        System.out.print("error");
//                        Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
//                        tilUsername.setError(anError.getErrorBody());
//                        loadingBar.setVisibility(View.GONE);
//                    }
//                });
    }
}
