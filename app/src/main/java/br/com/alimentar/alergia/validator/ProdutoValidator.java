package br.com.alimentar.alergia.validator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.activity.RegisterProdutoActivity;
import br.com.alimentar.alergia.activity.ViewProdutoActivity;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.view.CustomEditText;

import static br.com.alimentar.alergia.utils.AndroidUtils.alertDialog;

/**
 * Created by gilmar on 20/10/16.
 */

public class ProdutoValidator extends Validator{
    private static Callback callback;

    public static boolean setCategoriaError(Spinner seletor_categoria, String errorMessage, View viewError, int position) {

        View view = seletor_categoria.getSelectedView();
        TextView tvListItem = (TextView) view;
        TextView tvInvisibleError = (TextView) viewError; //findViewById(R.id.tvInvisibleError);

        if (errorMessage != null && position == 0) {
            tvListItem.setError(errorMessage);
            tvListItem.requestFocus();

            tvInvisibleError.requestFocus();
            tvInvisibleError.setError(errorMessage);
            return false;
        } else {
            tvListItem.setError(null);
            tvInvisibleError.setError(null);
            return true;
        }
    }

    public static void verificarCodigoBarraFirebase(DatabaseReference database, final CustomEditText campo_codigo_barra, final Activity activity, final String result, final String TAG, final Callback callback) {
        final ProdutoValidator validator = new ProdutoValidator();
        validator.callback = callback;
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = true;
                for (DataSnapshot produto : dataSnapshot.getChildren()) {
                    if (result.equals(produto.child("codigo_barra").getValue(String.class))) {
                        alertDialog(activity, R.string.msg_produto_ja_existe, R.string.msg_produto_ja_possui_um_cadastro, onClickVaiParaRegisterProdutoActivity(produto.getKey(), activity));
                        flag = false;
                        break;
                    }
                }
                if (validator.callback != null){
                    validator.callback.onVerificarCodigoBarraFirebase(flag);
                }
                campo_codigo_barra.setText(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private static AndroidUtils.onClickPositivo onClickVaiParaRegisterProdutoActivity(final String key, final Activity activity) {
        return new AndroidUtils.onClickPositivo() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, ViewProdutoActivity.class);
                intent.putExtra("key", key);
                activity.startActivity(intent);
            }
        };
    }

    public static interface Callback {
        public void onVerificarCodigoBarraFirebase(boolean flag);
    }
}
