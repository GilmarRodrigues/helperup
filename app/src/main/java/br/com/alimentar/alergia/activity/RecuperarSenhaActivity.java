package br.com.alimentar.alergia.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.custom.CustomEditText;
import br.com.alimentar.alergia.validator.UserValidator;

public class RecuperarSenhaActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private CustomEditText campo_email;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(new ContextThemeWrapper(this, R.style.AppTheme));

        campo_email = (CustomEditText) findViewById(R.id.edit_email);
        campo_email.setOnClickListener(onFocusEmail());

        findViewById(R.id.enviar_btn).setOnClickListener(onClickEnviar());
    }

    private View.OnClickListener onClickEnviar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecuperarSenha();
            }
        };
    }

    private View.OnClickListener onFocusEmail() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserValidator.openTeclado(campo_email, getBaseContext());
            }
        };
    }

    private void startRecuperarSenha() {
        final String email = campo_email.getText().toString().trim();
        Log.i("Script", "startRecuperarSenha");

        if (!validator()) {
            mProgress.setTitle(getString(R.string.aguarde));
            mProgress.setMessage(getString(R.string.msg_checando_email));
            mProgress.setCancelable(false);
            mProgress.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RecuperarSenhaActivity.this, getString(R.string.msg_enviar_email), Toast.LENGTH_SHORT).show();
                                Log.i("Script", getString(R.string.msg_enviar_email));
                            } else {
                                Toast.makeText(RecuperarSenhaActivity.this, getString(R.string.msg_email_nao_cadastrado), Toast.LENGTH_SHORT).show();
                                Log.i("Script", getString(R.string.msg_email_nao_cadastrado));
                            }
                            mProgress.dismiss();
                        }
                    });
        }
    }

    private boolean validator() {
        campo_email.setError(null);

        boolean email_not_null = UserValidator.validateNotNull(campo_email, getString(R.string.error_field_required));
        if (!email_not_null) {
            return true;
        }

        boolean email_valido = UserValidator.valitadeEmail(campo_email.getText().toString().trim());
        if (!email_valido) {
            campo_email.setError(getString(R.string.error_invalid_email));
            campo_email.setFocusable(true);
            campo_email.requestFocus();
            return true;
        }

        return false;
    }

}
