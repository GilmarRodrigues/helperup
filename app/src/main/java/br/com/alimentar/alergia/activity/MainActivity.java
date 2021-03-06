package br.com.alimentar.alergia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.fragment.CategoriaFragment;
import br.com.alimentar.alergia.fragment.HomeFragment;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.view.RoundedImageView;

import static br.com.alimentar.alergia.R.id.nav_adicionar_produto;
import static br.com.alimentar.alergia.R.id.nav_categoria;
import static br.com.alimentar.alergia.R.id.nav_configuracoes;
import static br.com.alimentar.alergia.utils.AndroidUtils.alertDialog;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private GoogleApiClient client;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabase.keepSynced(true);

        mAuthListener = usuarioLogado(mAuthListener, MainActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        headerView.findViewById(R.id.iv_perfil).setOnClickListener(onClickPerfil());
        headerView.findViewById(R.id.nav_drawer_header).setOnClickListener(onClickPerfil());

        setFirstItemNavigationView(navigationView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUser(headerView);
    }

    private View.OnClickListener onClickPerfil() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        };
    }

    private void setUser(View headerView) {
        if (mAuth.getCurrentUser() != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            final TextView tv_nome = (TextView) headerView.findViewById(R.id.tv_username);
            final TextView tv_email = (TextView) headerView.findViewById(R.id.tv_email);
            final ProgressBar progressBar = (ProgressBar) headerView.findViewById(R.id.progressBar);
            final RoundedImageView iv_perfil = (RoundedImageView) headerView.findViewById(R.id.iv_peril);

            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tv_nome.setText((String) dataSnapshot.child("nome").getValue());
                    tv_email.setText((String) dataSnapshot.child("email").getValue());

                    String imagem = (String) dataSnapshot.child("imagem").getValue();
                    Log.i("Script", "imagem " + imagem);
                    if (imagem != null ) {
                        if (imagem != Tabelas.DEFAULT) {
                            carregaImagem(iv_perfil, imagem, progressBar);
                        }
                    }
                    /*final User user = dataSnapshot.getValue(User.class);
                    tv_nome.setText(user.nome);
                    tv_email.setText(user.email);

                    if (!user.imagem.equals(Tabelas.DEFAULT)) {
                        carregaImagem(iv_perfil, user.imagem, progressBar);
                    } else {
                        Log.i(TAG, "Foto: " + user.imagem);
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        //AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        //AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(onSearch());
        return true;
    }

    private SearchView.OnQueryTextListener onSearch() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ("".equals(query)) {
                    Log.i("Script", "Change");
                    //carregaLista();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("Script", "Change");
                /*if (categorias != null) {
                    List<Categoria> list = new ArrayList<Categoria>();
                    for (Categoria categoria : categorias) {
                        boolean contains = categoria.getDescricao().toUpperCase().contains(
                                textoFinal.toUpperCase());
                        if (contains) {
                            list.add(categoria);
                        }
                    }
                    // Exibe no ListView um Adapter com apenas a lista que fez o
                    // filtro
                    listView.setAdapter(new CategoriaAdapter(list, getActivity()));
                    AndroidUtils.closeVirtualKeyboard(getActivity(), listView);
                }*/
                return false;
            }
        };
    }

    private void setFirstItemNavigationView(NavigationView navigationView) {
        replaceFragment(new HomeFragment());
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new HomeFragment());
                break;
            case nav_categoria:
                replaceFragment(new CategoriaFragment());
                break;
            case nav_adicionar_produto:
                Intent intent = new Intent(MainActivity.this, RegisterProdutoActivity.class);
                intent.putExtra("codigo_barra", "");
                startActivity(intent);
                break;
            /*case nav_favorito:
                break;
            case nav_cartilha:
                break;*/
            case R.id.nav_perfil:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case nav_configuracoes:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_drawer_cotainer, frag, "TAG").commit();
    }

    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.i("MainActivy", "onActivityResult:Cancelled");
            } else {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean flag = true;
                        for (DataSnapshot produto : dataSnapshot.getChildren()) {
                            if (result.getContents().equals(produto.child("codigo_barra").getValue(String.class))) {
                                flag = false;
                                Intent intent = new Intent(MainActivity.this, ViewProdutoActivity.class);
                                intent.putExtra("key", produto.getKey());
                                startActivity(intent);
                                break;
                            }
                        }
                        if (flag) {
                            alertDialog(MainActivity.this, R.string.msg_produto_não_encontrado, R.string.msg_produto_não_cadastrado, onClickVaiParaRegisterProdutoActivity(result.getContents()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private AndroidUtils.onClickPositivo onClickVaiParaRegisterProdutoActivity(final String contents) {
        return new AndroidUtils.onClickPositivo() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, RegisterProdutoActivity.class);
                intent.putExtra("codigo_barra", contents);
                startActivity(intent);
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
