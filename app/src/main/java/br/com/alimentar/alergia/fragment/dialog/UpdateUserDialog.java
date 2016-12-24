package br.com.alimentar.alergia.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import br.com.alimentar.alergia.validator.ProdutoValidator;

import static br.com.alimentar.alergia.R.id.edit_nome;

/**
 * Created by gilmar on 06/12/16.
 */

public class UpdateUserDialog extends BaseDialog {
    private Callback callback;
    private User mUser;
    private TextView campo_nome;
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;


    public static void show(FragmentManager fm, User user, Callback callback) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("update_user");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        UpdateUserDialog frag = new UpdateUserDialog();
        frag.callback = callback;
        Bundle args = new Bundle();
        args.putParcelable(User.KEY, user);
        frag.setArguments(args);
        frag.show(fm, "update_user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.title_atualizar_nome);
        View view = inflater.inflate(R.layout.dialog_update_user, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUser.keepSynced(true);

        mUser = getArguments().getParcelable(User.KEY);

        campo_nome = (TextView) view.findViewById(edit_nome);
        if (mUser != null) {
            campo_nome.setText(mUser.nome);
        }

        view.findViewById(R.id.btn_salvar).setOnClickListener(onClickSalvar());
        view.findViewById(R.id.btn_cancelar).setOnClickListener(onClickCancelar());

        return view;
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = mAuth.getCurrentUser().getUid();

                if (!validator()) {
                    showProgressDialog(R.string.msg_update);

                    User user = updateUser(uid);
                    hideProgressDialog();

                    if (callback != null) {
                        callback.onUserUpdate(user);
                    }
                    dismiss();

                }
            }
        };
    }

    private View.OnClickListener onClickCancelar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };
    }

    private User updateUser(String uid) {
        User value = new User(campo_nome.getText().toString(), mUser.email, mUser.imagem, mUser.substancias);
        Map<String, Object> postValue = value.toMap();
        Map<String, Object> childUpdates = new HashMap<String, Object>();
        childUpdates.put(uid, postValue);
        mDatabaseUser.updateChildren(childUpdates);

        return value;
    }

    private boolean validator() {
        campo_nome.setError(null);

        boolean nome_not_null = ProdutoValidator.validateNotNull(campo_nome, getString(R.string.error_field_required));
        if (!nome_not_null) {
            return true;
        }

        return false;
    }

    public static interface Callback {
        public void onUserUpdate(User user);
    }
}
