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
    private CustomEditText campo_email;
    private CustomEditText campo_senha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        mGoogleBtn.setOnClickListener(onClickSingIn());

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

        setGooglePlusButtonText(mGoogleBtn, getString(R.string.btn_entre_com_google));
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

            showProgressDialog(R.string.realizando_login);

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
                    hideProgressDialog();
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
