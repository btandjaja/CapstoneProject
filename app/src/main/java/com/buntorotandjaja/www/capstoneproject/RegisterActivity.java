package com.buntorotandjaja.www.capstoneproject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
public class RegisterActivity extends AppCompatActivity {

    private final int PASSWORD_MIN = 8, PASSWORD_MAX = 16;
    // Firebase Authentication
    private FirebaseAuth mAuth;

    // UI references.
    @BindView(R.id.email) AutoCompleteTextView mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.password_confirm) EditText mPasswordConfirmView;
    @BindView(R.id.email_register_button) Button mEmailRegisterButton;
    @BindView(R.id.tb_register) Toolbar mToolbar;
    @BindView(R.id.progressbar_holder_register) View mProgressbarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO remove statusBar before setContentView
        removeStatusBar();
        setContentView(R.layout.activity_register);
        // TODO Butterknife
        ButterKnife.bind(this);
        // TODO setup actionBar with current toolbar
        setupActionBar();
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

        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void removeStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.chevron_left_white_24dp);
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
        // TODO change pb
        mProgressbarHolder.setVisibility(View.VISIBLE);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        // TODO read in retyped password
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            return;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            return;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            return;
        } else if (!TextUtils.isEmpty(passwordConfirm) && !isPasswordValid(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_invalid_password));
            mPasswordConfirmView.requestFocus();
            return;
        } else if (!password.equals(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_nonmatch_password));
            mPasswordConfirmView.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressbarHolder.setVisibility(View.INVISIBLE);
                            // sign in success, update UI with the signed-in user's information
                            Log.d(this.getClass().getSimpleName(), getString(R.string.log_create_account_successful));
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(RegisterActivity.this, ItemListActivity.class);
                            startActivity(intent);
                        } else {
                            // TODO collusion exception
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                mProgressbarHolder.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        // TODO only logic I can find and understood (Dec 2018)
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        // TODO custom password logic
        // only allow alphabet, unique characters (#,$,%,&), and one capitalize letter
        boolean oneCapitalLetter = false;
        boolean oneUnique = false;
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isLetterOrDigit(password.charAt(i))) {
                if (!uniqueChar(password.charAt(i))) {
                    return false;
                }
                oneUnique = true;
            } else if (password.charAt(i) >= 'A' && password.charAt(i) <= 'Z') {
                oneCapitalLetter = true;
            }
        }

        // must be between 8 - 16 characters
        boolean passwordLengthRequirement = password.length() >= PASSWORD_MIN && password.length() <= PASSWORD_MAX;

        return oneCapitalLetter && oneUnique && passwordLengthRequirement;
    }

    private boolean uniqueChar(char c) {
        //TODO check if it contains "#$%&"
        switch (c) {
            case '$':
                return true;
            case '#':
                return true;
            case '&':
                return true;
            case '%':
                return true;
        }
        return false;
    }
}