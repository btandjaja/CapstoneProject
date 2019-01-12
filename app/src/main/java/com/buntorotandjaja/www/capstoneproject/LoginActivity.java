package com.buntorotandjaja.www.capstoneproject;

import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // Firebase Authentication
    private FirebaseAuth mAuth;

    private boolean mLoading;

    // UI references.
    @BindView(R.id.email) AutoCompleteTextView mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.register) Button mRegister;
    @BindView(R.id.email_sign_in_button) Button mEmailSignInButton;
    @BindView(R.id.progressbar_holder_login) View mProgressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        clearLoginForm();
        mLoading = false;
        // FirebaseAuth instance
        initializeFirebaseAuth();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void clearLoginForm() {
        mEmailView.setText("");
        mPasswordView.setText("");
    }

    private void initializeFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuth == null) {
            return;
        }
        showIndicator();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            hideIndicator();
            return;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            hideIndicator();
            return;
        }

        // Check for nonempty password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            hideIndicator();
            return;
        }

        // TODO is it a good idea to move this to function? will it show in memory?????
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideIndicator();
                            Intent intent = new Intent(LoginActivity.this, ItemListActivity.class);
                            startActivity(intent);
                        } else {
                            hideIndicator();
                            // show error
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        // TODO only logic I can find and understood (Dec 2018)
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showIndicator() {
        // TODO show progress bar visibility
        mProgressBarHolder.setVisibility(View.VISIBLE);
        mLoading = true;
        mEmailView.setFocusableInTouchMode(false);
        mEmailView.setFocusable(false);
        mEmailView.setEnabled(false);
        mPasswordView.setFocusableInTouchMode(false);
        mPasswordView.setFocusable(false);
        mPasswordView.setEnabled(false);
        mRegister.setEnabled(false);
        mEmailSignInButton.setEnabled(false);
    }

    private void hideIndicator() {
        mProgressBarHolder.setVisibility(View.INVISIBLE);
        mLoading = false;
        mEmailView.setFocusableInTouchMode(true);
        mEmailView.setFocusable(true);
        mEmailView.setEnabled(true);
        mPasswordView.setFocusableInTouchMode(true);
        mPasswordView.setFocusable(true);
        mPasswordView.setEnabled(true);
        mRegister.setEnabled(true);
        mEmailSignInButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        clearLoginForm();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}