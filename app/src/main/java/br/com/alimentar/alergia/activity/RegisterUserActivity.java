package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.view.CustomEditText;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.utils.AnimationUtils;
import br.com.alimentar.alergia.validator.UserValidator;

public class RegisterUserActivity extends GoogleActivity {
    private CustomEditText campo_nome;
    private CustomEditText campo_email;
    private CustomEditText campo_senha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        mGoogleBtn.setOnClickListener(onClickSingIn());

        campo_nome = (CustomEditText) findViewById(R.id.edit_nome);
        campo_nome.setOnClickListener(onFocusNome());
        campo_nome.setOnClickKeyPreItem(onEscondeTeclado());
        campo_email = (CustomEditText) findViewById(R.id.edit_email);
        campo_email.setOnClickListener(onFocusEmail());
        campo_email.setOnClickKeyPreItem(onEscondeTeclado());
        campo_senha = (CustomEditText) findViewById(R.id.edit_senha);
        campo_senha.setOnClickListener(onFocusSenha());
        campo_senha.setOnClickKeyPreItem(onEscondeTeclado());

        mReLayoutOU = (RelativeLayout) findViewById(R.id.rl_ou);
        mLayoutForm = (LinearLayout) findViewById(R.id.login_form);


        findViewById(R.id.salvarBtn).setOnClickListener(onClickSalvar());
        findViewById(R.id.text_entrar).setOnClickListener(onClickEntrar());
        findViewById(R.id.tv_termos_uso).setOnClickListener(onClickTermosDeUso());
        findViewById(R.id.tv_politica_privacidade).setOnClickListener(onClickPoliticaPrivacidade());

        setGooglePlusButtonText(mGoogleBtn, getString(R.string.btn_cadastrar_com_google));
    }

    private View.OnClickListener onClickTermosDeUso() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser(Tabelas.URL_TERMOS_DE_USO);
            }
        };
    }

    private View.OnClickListener onClickPoliticaPrivacidade() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser(Tabelas.URL_POLITICA_PRIVACIDADE);
            }
        };
    }

    private View.OnClickListener onClickEntrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
    }

    private CustomEditText.OnClickKeyPreItem onEscondeTeclado() {
        return new CustomEditText.OnClickKeyPreItem() {
            @Override
            public void onEscondeTeclado() {
                AnimationUtils.setAnimationVisible(mGoogleBtn, mReLayoutOU, mLayoutForm);
            }
        };
    }

    private View.OnClickListener onFocusNome() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.setAnimationGone(mGoogleBtn, mReLayoutOU, mLayoutForm);
                UserValidator.openTeclado(campo_nome, getBaseContext());
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

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistrar();
            }
        };
    }

    private void startRegistrar() {
        final String nome = campo_nome.getText().toString().trim();
        final String email = campo_email.getText().toString().trim();
        final String senha = campo_senha.getText().toString().trim();

        if (!validator()) {

            showProgressDialog(R.string.realizando_cadastro);

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseUsuario.child(user_id);


                        User user = new User(nome, email, Tabelas.DEFAULT, Tabelas.addSubstancias(getBaseContext()));
                        current_user_db.setValue(user);

                        hideProgressDialog();

                        Intent mainIntent = new Intent(RegisterUserActivity.this, RegisterSubstanciasUserActivity.class);
                        mainIntent.putExtra(User.KEY, user);
                        startActivity(mainIntent);
                        AndroidUtils.closeVirtualKeyboard(RegisterUserActivity.this, campo_senha);

                    } else {
                        Toast.makeText(RegisterUserActivity.this, getString(R.string.error_email_ja_cadastrado), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }
            });
        }
    }


    private boolean validator() {
        campo_nome.setError(null);
        campo_email.setError(null);
        campo_senha.setError(null);

        boolean nome_not_null = UserValidator.validateNotNull(campo_nome, getString(R.string.error_field_required));
        if (!nome_not_null) {
            return true;
        }

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
}
