package br.com.alimentar.alergia.activity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.fragment.CustomBottomSheetDialogFragment;
import br.com.alimentar.alergia.fragment.dialog.UpdateAlergenicosDialog;
import br.com.alimentar.alergia.fragment.dialog.UpdateUserDialog;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class ProfileActivity extends BaseActivity {
    private DatabaseReference mDatabaseUser;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private User mUser;
    private TextView tv_nome;
    private TextView tv_email;
    private ProgressBar progressBar;
    private ImageView iv_perfil;
    private Uri mImagemUri = null;
    private String key;
    private LinearLayout mLLAlergenicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseUser.keepSynced(true);

        key = mAuth.getCurrentUser().getUid();

        findViewById(R.id.iv_editar_perfil).setOnClickListener(onClickUpdateUser());
        findViewById(R.id.iv_editar_alergenicos).setOnClickListener(onClickUpdateAlergenicos());

        setUser();

        iv_perfil.setOnClickListener(onClickUpdateImageUser());
    }

    private View.OnClickListener onClickUpdateAlergenicos() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAlergenicos();
            }
        };
    }

    private View.OnClickListener onClickUpdateImageUser() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage(view);
            }
        };
    }

    private View.OnClickListener onClickUpdateUser() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePerfil();
            }
        };
    }

    private void setUser() {

        tv_nome = (TextView) findViewById(R.id.tv_username);
        tv_email = (TextView) findViewById(R.id.tv_email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iv_perfil = (ImageView) findViewById(R.id.iv_perfil);
        mLLAlergenicos = (LinearLayout) findViewById(R.id.llt_alergenicos);

        mDatabaseUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                tv_nome.setText(mUser.nome);
                tv_email.setText(mUser.email);

                if (!mUser.imagem.equals(Tabelas.DEFAULT)) {
                    carregaImagem(iv_perfil, mUser.imagem, progressBar);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "Foto: " + mUser.imagem);
                }

                List<Substancia> substancias = mUser.substancias;
                for (int i=0; i <substancias.size(); i++) {
                    if (substancias.get(i).status.equals(getString(R.string.const_contem))) {
                        TextView tv_alergenico = new TextView(ProfileActivity.this);
                        tv_alergenico.setPadding(0, 0, 0, 15);
                        tv_alergenico.setText(substancias.get(i).nome);
                        mLLAlergenicos.addView(tv_alergenico);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePerfil() {
        UpdateUserDialog.show(getSupportFragmentManager(), mUser, new UpdateUserDialog.Callback() {
            @Override
            public void onUserUpdate(User user) {
                tv_nome.setText(user.nome);
                mUser.nome = user.nome;
            }
        });
    }

    private void updateImage(Uri uri) {


        showProgressDialog(R.string.msg_update);

        StorageReference filepath = mStorage.child(Tabelas.IMAGEM_PERFIL).child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = new User(mUser.nome, mUser.email, downloadUrl.toString(), mUser.substancias);

                            if (dataSnapshot.hasChild(key)) {
                                Map<String, Object> postValue = user.toMap();
                                Map<String, Object> childUpdates = new HashMap<String, Object>();
                                childUpdates.put(key, postValue);
                                mDatabaseUser.updateChildren(childUpdates);
                                mUser = user;
                            }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void setImage(final View v) {
        CustomBottomSheetDialogFragment.show(getSupportFragmentManager(), new CustomBottomSheetDialogFragment.Callback() {
            @Override
            public void onBottomSheet(Uri imagem, boolean flag) {

                if (imagem != null) {
                    carregaImagem(iv_perfil, imagem.toString());
                    try {
                        File actualImage = FileUtil.from(ProfileActivity.this, imagem);
                        iv_perfil.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                        actualImage = Compressor.getDefault(ProfileActivity.this).compressToFile(actualImage);
                        mImagemUri = Uri.fromFile(actualImage);
                        updateImage(mImagemUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(v, getString(R.string.msg_imagem_null), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    private void updateAlergenicos() {
        UpdateAlergenicosDialog.show(getSupportFragmentManager(), mUser, new UpdateAlergenicosDialog.Callback() {
            @Override
            public void onAlergenicosUpdate(List<Substancia> alergenicos) {
                mLLAlergenicos.removeAllViews();
                for (int i=0; i <alergenicos.size(); i++) {
                    if (alergenicos.get(i).status.equals(getString(R.string.const_contem))) {
                        TextView tv_alergenico = new TextView(ProfileActivity.this);
                        tv_alergenico.setPadding(0, 0, 0, 15);
                        tv_alergenico.setText(alergenicos.get(i).nome);
                        mLLAlergenicos.addView(tv_alergenico);
                    }
                }
            }
        });
    }

}
