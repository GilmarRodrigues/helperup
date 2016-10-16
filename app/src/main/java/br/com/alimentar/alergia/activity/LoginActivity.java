package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.view.CustomEditText;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.utils.AnimationUtils;
import br.com.alimentar.alergia.validator.UserValidator;

public class LoginActivity extends GoogleActivity {
    private static final String TAG = "LoginActivity";
    //private FirebaseAuth mAuth;
    //private ProgressDialog mProgress;
    private CustomEditText campo_email;
    private CustomEditText campo_senha;
    //private DatabaseReference mDatabaseUsuario;
    //private SignInButton mGoogleBtn;
    //private static final int RC_SIGN_IN = 1;
    //private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        mGoogleBtn.setOnClickListener(onClickSingIn());
        /*mAuth = FirebaseAuth.getInstance();
        mDatabaseUsuario = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUsuario.keepSynced(true);

        mProgress = new ProgressDialog(new ContextThemeWrapper(this, R.style.AppTheme));*/

        mReLayoutOU = (RelativeLayout) findViewById(R.id.rl_ou);
        mLayoutForm = (LinearLayout) findViewById(R.id.login_form);

        campo_email = (CustomEditText) findViewById(R.id.edit_email);
        campo_email.setOnClickListener(onFocusEmail());
        campo_email.setOnClickKeyPreItem(onEscondeTeclado());
        campo_senha = (CustomEditText) findViewById(R.id.edit_senha);
        campo_senha.setOnClickListener(onFocusSenha());
        campo_senha.setOnClickKeyPreItem(onEscondeTeclado());

        findViewById(R.id.entrar_btn).setOnClickListener(onClickEntrar());
        findViewById(R.id.tv_esqueceu_senha).setOnClickListener(onClickEsqueceuSenha());

       // mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_btn);

        // ----------- GOOGLE SIGN IN -----
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });*/
        setGooglePlusButtonText(mGoogleBtn, getString(R.string.btn_entre_com_google));
        setProgressMsg(R.string.realizando_login);
    }


    private CustomEditText.OnClickKeyPreItem onEscondeTeclado() {
        return new CustomEditText.OnClickKeyPreItem() {
            @Override
            public void onEscondeTeclado() {
                AnimationUtils.setAnimationVisible(mGoogleBtn, mReLayoutOU, mLayoutForm);
            }
        };
    }

    private View.OnClickListener onFocusEmail() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.setAnimationGone(mGoogleBtn, mReLayoutOU, mLayoutForm);
                UserValidator.openTeclado(campo_email, getBaseContext());
            }
        };
    }

    private View.OnClickListener onFocusSenha() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.setAnimationGone(mGoogleBtn, mReLayoutOU, mLayoutForm);
                UserValidator.openTeclado(campo_senha, getBaseContext());
            }
        };
    }

   /* private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            mProgress.setTitle(getString(R.string.aguarde));
            mProgress.setMessage(getString(R.string.realizando_login));
            mProgress.setCancelable(false);
            mProgress.show();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mProgress.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Autenticação falhou.", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        } else {

                            mProgress.dismiss();
                            checkUserExist(task);
                        }
                        // ...
                    }
                });
    }

    private void checkUserExist(final Task<AuthResult> task) {
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            final DatabaseReference current_user_db = mDatabaseUsuario.child(user_id);

            mDatabaseUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    } else {

                        User user = new User(task.getResult().getUser().getDisplayName(),
                                task.getResult().getUser().getEmail(),
                                task.getResult().getUser().getPhotoUrl().toString());
                        //current_user_db.setValue(user);

                        Intent mainIntent = new Intent(LoginActivity.this, SubstanciasActivity.class);
                        mainIntent.putExtra(User.KEY, user);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Database Error.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    private View.OnClickListener onClickEntrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        };
    }

    private void checkLogin() {
        final String email = campo_email.getText().toString().trim();
        final String senha = campo_senha.getText().toString().trim();

        if (!validator()) {
            mProgress.setTitle(getString(R.string.aguarde));
            mProgress.setMessage(getString(R.string.realizando_login));
            mProgress.setCancelable(false);
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        AndroidUtils.closeVirtualKeyboard(LoginActivity.this, campo_senha);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_login_invalid), Toast.LENGTH_SHORT).show();
                    }
                    mProgress.dismiss();
                }
            });
        }
    }

    private View.OnClickListener onClickEsqueceuSenha() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecuperarSenhaActivity.class));
            }
        };
    }

    private boolean validator() {
        campo_email.setError(null);
        campo_senha.setError(null);

        boolean email_not_null = UserValidator.validateNotNull(campo_email, getString(R.string.error_field_required));
        if (!email_not_null) {
            return true;
        }

        boolean senha_not_null = UserValidator.validateNotNull(campo_senha, getString(R.string.error_field_required));
        if (!senha_not_null) {
            return true;
        }

        boolean email_valido = UserValidator.valitadeEmail(campo_email.getText().toString().trim());
        if (!email_valido) {
            campo_email.setError(getString(R.string.error_invalid_email));
            campo_email.setFocusable(true);
            campo_email.requestFocus();
            return true;
        }
        boolean password_valido = UserValidator.validatePassword(campo_senha.getText().toString().trim());
        if (!password_valido) {
            campo_senha.setError(getString(R.string.error_invalid_password));
            campo_senha.setFocusable(true);
            campo_senha.requestFocus();
            return true;
        }

        return false;
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //Toast.makeText(this, "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
                //toolbar.setVisibility(View.VISIBLE);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }*/


}
