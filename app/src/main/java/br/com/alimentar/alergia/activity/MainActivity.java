package br.com.alimentar.alergia.activity;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
import br.com.alimentar.alergia.fragment.HomeFragment;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import br.com.alimentar.alergia.view.RoundedImageView;

import static br.com.alimentar.alergia.R.id.nav_cartilha;
import static br.com.alimentar.alergia.R.id.nav_categoria;
import static br.com.alimentar.alergia.R.id.nav_configuracoes;
import static br.com.alimentar.alergia.R.id.nav_favorito;
import static br.com.alimentar.alergia.R.id.nav_perfil;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabase.keepSynced(true);

        mAuthListener =  usuarioLogado(mAuthListener, MainActivity.this);


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

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        setUser(headerView);

        setFirstItemNavigationView(navigationView);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setUser(View headerView) {
        if (mAuth.getCurrentUser() != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            final TextView tv_nome = (TextView) headerView.findViewById(R.id.tv_username);
            final TextView tv_email = (TextView) headerView.findViewById(R.id.tv_email);
            final RoundedImageView iv_perfil = (RoundedImageView) headerView.findViewById(R.id.iv_peril);

            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    tv_nome.setText(user.nome);
                    tv_email.setText(user.email);

                    if (!user.imagem.equals(Tabelas.DEFAULT)) {
                        carregaImagem(iv_perfil, user.imagem);
                    } else {
                        Log.i(TAG, "Foto: " + user.imagem);
                    }
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
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
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
                break;
            case nav_favorito:
                break;
            case nav_cartilha:
                break;
            case nav_perfil:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case nav_configuracoes:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }

/*        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new HomeFragment());
        } else if (id == R.id.nav_categoria) {

        } else if (id == R.id.nav_favorito) {

        } else if (id == R.id.nav_cartilha) {

        } else if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_configuracoes){

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_drawer_cotainer, frag, "TAG").commit();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
