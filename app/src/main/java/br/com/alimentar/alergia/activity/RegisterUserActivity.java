package br.com.alimentar.alergia.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.SubstanciasAdapter;
import br.com.alimentar.alergia.custom.CustomEditText;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.utils.AnimationUtils;
import br.com.alimentar.alergia.validator.UserValidator;

import static android.R.attr.data;

public class RegisterUserActivity extends GoogleActivity {
    private CustomEditText campo_nome;
    private CustomEditText campo_email;
    private CustomEditText campo_senha;
    //private ImageView mImagemPerfil;
    //private ProgressDialog mProgress;
    //private FirebaseAuth mAuth;
    //private DatabaseReference mDatabase;
    //private StorageReference mStorageImage;
    //private Uri mImageUri = null;
    private static final int GALLERRY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleBtn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        mGoogleBtn.setOnClickListener(onClickSingIn());

        /*mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabase.keepSynced(true);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Perfil_imagem");

        mProgress = new ProgressDialog(new ContextThemeWrapper(this, R.style.AppTheme));*/

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

        //mImagemPerfil = (ImageView) findViewById(R.id.iv_perfil);
        //mImagemPerfil.setOnClickListener(onClickImagePerfil());

        findViewById(R.id.salvarBtn).setOnClickListener(onClickSalvar());
        findViewById(R.id.text_entrar).setOnClickListener(onClickEntrar());

        setGooglePlusButtonText(mGoogleBtn, getString(R.string.btn_cadastrar_com_google));
        setProgressMsg(R.string.realizando_cadastro);
    }

    private View.OnClickListener onClickEntrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
    }

    /*private View.OnClickListener onClickImagePerfil() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERRY_REQUEST);
            }
        };
    }*/

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

            mProgress.setTitle(getString(R.string.aguarde));
            mProgress.setMessage(getString(R.string.realizando_cadastro));
            mProgress.setCancelable(false);
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseUsuario.child(user_id);


                        User user = new User(nome, email, Tabelas.DEFAULT);
                        current_user_db.setValue(user);

                        mProgress.dismiss();


                        Intent mainIntent = new Intent(RegisterUserActivity.this, SubstanciasActivity.class);
                        mainIntent.putExtra(User.KEY, user);
                        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        AndroidUtils.closeVirtualKeyboard(RegisterUserActivity.this, campo_senha);

                        //StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

                        /*filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);

                                String downloadUri = taskSnapshot.getDownloadUrl().toString();

                                current_user_db.child("nome").setValue(nome);
                                current_user_db.child("email").setValue(email);
                                current_user_db.child("fone").setValue(Tabelas.DEFAULT);
                                current_user_db.child("image").setValue(downloadUri);

                                mProgress.dismiss();

                            }
                        });*/
                    } else {
                        Toast.makeText(RegisterUserActivity.this, getString(R.string.error_email_ja_cadastrado), Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }
            });
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERRY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mImagemPerfil.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }*/

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
