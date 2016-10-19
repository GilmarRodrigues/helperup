package br.com.alimentar.alergia.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.utils.AndroidUtils;

import static br.com.alimentar.alergia.R.id.tv_versao;

public class SettingActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.tv_contate_suport).setOnClickListener(onClickSuport());
        findViewById(R.id.tv_termos_uso).setOnClickListener(onClickTermosDeUso());
        findViewById(R.id.tv_politica_privacidade).setOnClickListener(onClickPoliticaPrivacidade());
        findViewById(R.id.rl_facebook).setOnClickListener(onClickFacebook());
        findViewById(R.id.rl_twitter).setOnClickListener(onClickTwitter());
        findViewById(R.id.rl_convide_amigos).setOnClickListener(onClickConvideAmigos());
        findViewById(R.id.rl_avaliar).setOnClickListener(onClickAvaliar());
        findViewById(R.id.tv_sair).setOnClickListener(onClickSair());

        TextView tv_numro_versao = (TextView) findViewById(R.id.tv_numero_versao);
        tv_numro_versao.setText(AndroidUtils.getVersionName(this));

        mAuthListener = usuarioLogado(mAuthListener, SettingActivity.this);
    }

    private View.OnClickListener onClickSuport() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickTermosDeUso() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickPoliticaPrivacidade() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickFacebook() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickTwitter() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }


    private View.OnClickListener onClickConvideAmigos() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickAvaliar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onClickSair() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
