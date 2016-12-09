package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.utils.AndroidUtils;

public class SettingsActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.tv_register_produto).setOnClickListener(onClickRegisterProduto());
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

        mAuthListener = usuarioLogado(mAuthListener, SettingsActivity.this);
    }

    private View.OnClickListener onClickRegisterProduto() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, RegisterProdutoActivity.class);
                intent.putExtra("codigo_barra", "");
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener onClickSuport() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser(Tabelas.URL_SUPPORT);
            }
        };
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

    private View.OnClickListener onClickFacebook() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser(Tabelas.URL_FACEBOOK);
            }
        };
    }

    private View.OnClickListener onClickTwitter() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browser(Tabelas.URL_TWITTER);
            }
        };
    }


    private View.OnClickListener onClickConvideAmigos() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        };
    }

    private View.OnClickListener onClickAvaliar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlePlay();
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

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + ": " + "http://play.google.com/store/apps/details?id=" + Tabelas.APP_PACKAGER_NAME);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.msg_compartilhar)));
    }

    private void googlePlay() {
        Intent irGooglePlay;
        try {
            irGooglePlay = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Tabelas.APP_PACKAGER_NAME));
        } catch (android.content.ActivityNotFoundException anfe) {
            irGooglePlay = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Tabelas.APP_PACKAGER_NAME));
        }
        startActivity(irGooglePlay);
    }

}
