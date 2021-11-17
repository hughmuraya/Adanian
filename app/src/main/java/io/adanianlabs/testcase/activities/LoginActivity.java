package io.adanianlabs.testcase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fxn.stash.Stash;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import io.adanianlabs.testcase.MainActivity;
import io.adanianlabs.testcase.R;
import io.adanianlabs.testcase.dependancies.Constants;
import io.adanianlabs.testcase.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";

    private TextInputLayout til_username;
    private TextInputEditText username;
    private TextInputLayout til_password;
    private TextInputEditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stash.init(this);
        setContentView(R.layout.activity_login);
        AndroidNetworking.initialize(getApplicationContext());

        til_username = findViewById(R.id.til_email);
        username = findViewById(R.id.etxt_email);
        til_password = findViewById(R.id.til_password);
        password = findViewById(R.id.etxt_password);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("692296030771-5k65ld2m4a4jt81p4tfgc30g3o8tunk8.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        ((View) findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateNulls()){

                    doLoginRequest();

                }

            }
        });

        ((View) findViewById(R.id.tv_forgot_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(LoginActivity.this, "Forgot Password requested", Toast.LENGTH_SHORT).show();

            }
        });

        hideSoftKeyboard();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }



    private void updateUI(FirebaseUser user) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", user.getDisplayName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("profilepicture", user.getPhotoUrl().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Constants.GOOGLE_SIGNIN_CALLBACK)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Accept", "*/*")
                .addHeaders("Accept-Encoding", "gzip, deflate, br")
                .addHeaders("Connection","keep-alive")
                .addHeaders("User-Agent","PostmanRuntime/7.28.4")
                .addJSONObjectBody(jsonObject) // posting json
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        Log.e(TAG, response.toString());

                        Toast.makeText(LoginActivity.this, "Complete!", Toast.LENGTH_SHORT).show();


                        try {
                            boolean  success = response.has("Success") && response.getBoolean("Success");
                            boolean  auth = response.has("auth") && response.getBoolean("auth");
                            String message = response.has("message") ? response.getString("message") : "";
                            String access_token = response.has("accesstoken") ? response.getString("accesstoken") : "";


                            if (access_token.contains("")){

                                JSONObject user = response.getJSONObject("authuser");
                                int id = user.has("id") ? user.getInt("id") : 0;
                                String email = user.has("email") ? user.getString("email") : "";
                                String username = user.has("username") ? user.getString("username") : "";
                                String mobile = user.has("mobile") ? user.getString("mobile") : "";
                                String profilepicture = user.has("profilepicture") ? user.getString("profilepicture") : "";

                                User newUser = new User(access_token,username,email,mobile,id,profilepicture);
                                Stash.put(Constants.LOGGED_IN_USER, newUser);

                                Intent mint = new Intent(LoginActivity.this, MainActivity.class);
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                mint.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mint);


                            }else if (!success && auth){

                                Snackbar.make(findViewById(R.id.activity_sign_in), message, Snackbar.LENGTH_LONG).show();

                            }else{

                                Snackbar.make(findViewById(R.id.activity_sign_in), message, Snackbar.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, String.valueOf(error.getErrorCode()));
//                        Log.e(TAG, error.getErrorBody());

                        if(error.getErrorCode() == 0){
                            Intent mint = new Intent(LoginActivity.this, MainActivity.class);
                            mint.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mint);
//                            Toast.makeText(LoginActivity.this, "uncomplete!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(findViewById(R.id.activity_sign_in), "" + error.getErrorBody(), Snackbar.LENGTH_LONG).show();

                        }

                        


                    }
                });

    }

    private void doLoginRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password.getText().toString());
            jsonObject.put("email", username.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Constants.ENDPOINT+ Constants.LOGIN)
                .addHeaders("Content-Type", "application.json")
                .addHeaders("Accept", "*/*")
                .addHeaders("Accept-Encoding", "gzip, deflate, br")
                .addHeaders("Connection","keep-alive")
                .addHeaders("User-Agent","PostmanRuntime/7.28.4")
                .addJSONObjectBody(jsonObject) // posting json
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        Log.e(TAG, response.toString());


                        try {
                            boolean auth = !response.has("auth") || response.getBoolean("auth");
                            String success = response.has("success") ? response.getString("success") : "";
                            String error = response.has("error") ? response.getString("error") : "";
                            String message = response.has("message") ? response.getString("message") : "";

                            if (auth && success.contains("true")){

                                String access_token = response.has("accesstoken") ? response.getString("accesstoken") : "";
                                JSONObject user = response.getJSONObject("currentuser");
                                int id = user.has("id") ? user.getInt("id") : 0;
                                String email = user.has("email") ? user.getString("email") : "";
                                String username = user.has("username") ? user.getString("username") : "";
                                String mobile = user.has("mobile") ? user.getString("mobile") : "";
                                String profilepicture = user.has("profilepicture") ? user.getString("profilepicture") : "";

                                User newUser = new User(access_token,username,email,mobile,id,profilepicture);
                                Stash.put(Constants.LOGGED_IN_USER, newUser);

                                Intent mint = new Intent(LoginActivity.this, MainActivity.class);
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                mint.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mint);


                            }else if (!auth && success.contains("true")){

                                Snackbar.make(findViewById(R.id.activity_sign_in), message, Snackbar.LENGTH_LONG).show();

                            }else{

                                Snackbar.make(findViewById(R.id.activity_sign_in), message, Snackbar.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, String.valueOf(error.getErrorCode()));

                        Snackbar.make(findViewById(R.id.activity_sign_in), "" + error.getErrorBody(), Snackbar.LENGTH_LONG).show();



                    }
                });

    }

    private boolean validateNulls() {
        boolean valid = true;

        if(TextUtils.isEmpty(username.getText().toString()))
        {
            til_username.setError(getString(R.string.username_required));
            valid = false;
            return valid;
        }

        if(TextUtils.isEmpty(password.getText().toString()))
        {
            til_password.setError(getString(R.string.password_required));
            valid = false;
            return valid;
        }

        return valid;
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

}