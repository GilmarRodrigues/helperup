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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.SubstanciasAdapter;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;

/**
 * Created by gilmar on 07/12/16.
 */

public class UpdateAlergenicosDialog extends BaseDialog {
    private Callback callback;
    private User mUser;
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private ListView mListView;

    public static void show(FragmentManager fm, User user, Callback callback) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("update_alergenicos");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        UpdateAlergenicosDialog frag = new UpdateAlergenicosDialog();
        frag.callback = callback;
        Bundle args = new Bundle();
        args.putParcelable(User.KEY, user);
        frag.setArguments(args);
        frag.show(fm, "update_alergenicos");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_alergenicos, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUser.keepSynced(true);

        mUser = getArguments().getParcelable(User.KEY);

        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(new SubstanciasAdapter(getActivity(), onClickSwitch(), mUser.substancias));

        view.findViewById(R.id.btn_salvar).setOnClickListener(onClickSalvar());
        view.findViewById(R.id.btn_cancelar).setOnClickListener(onClickCancelar());

        return view;
    }

    private SubstanciasAdapter.OnClickSwitch onClickSwitch() {
        return new SubstanciasAdapter.OnClickSwitch() {
            @Override
            public void onClick(View view, int position) {
                if (mUser.substancias.get(position).status.equals(getString(R.string.const_nao_contem))) {
                    mUser.substancias.get(position).status = getString(R.string.const_contem);
                } else {
                    mUser.substancias.get(position).status = getString(R.string.const_nao_contem);
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

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = mAuth.getCurrentUser().getUid();

                if (mUser.substancias.size() > 0) {

                    showProgressDialog(R.string.msg_update);

                    User user = new User(mUser.nome, mUser.email, mUser.imagem, mUser.substancias);
                    Map<String, Object> postValue = user.toMap();
                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put(uid, postValue);
                    mDatabaseUser.updateChildren(childUpdates);

                    hideProgressDialog();

                    if (callback != null) {
                        callback.onAlergenicosUpdate(mUser.substancias);
                    }
                    dismiss();
                }
            }
        };
    }

    public static interface Callback {
        public void onAlergenicosUpdate(List<Substancia> alergenicos);
    }
}
