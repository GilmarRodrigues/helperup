package br.com.alimentar.alergia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.view.RoundedImageView;

import static android.R.attr.logo;
import static android.R.attr.onClick;

/**
 * Created by gilmar on 07/11/16.
 */

public class ProdutoAdapter extends FirebaseRecyclerAdapter<Produto, ProdutoAdapter.ViewHolder> {
    private Context mContext;
    private onClick onClick;

    public ProdutoAdapter(Class<Produto> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, Context context, onClick onClick) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        this.onClick = onClick;
    }

    @Override
    protected void populateViewHolder(final ViewHolder viewHolder, Produto model, final int position) {
        final String key = getRef(position).getKey();
        viewHolder.tv_nome.setText(model.nome);
        viewHolder.tv_data.setText(formatDate(model.data));

        carregaImagem(viewHolder.iv_produto, model.imagem);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        database.child(model.uid_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome_user = (String) dataSnapshot.child("nome").getValue();
                String imagem_user = (String) dataSnapshot.child("imagem").getValue();
                viewHolder.tv_nome_user.setText(nome_user);
                carregaImagem(viewHolder.iv_perfil, imagem_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick(view, position, key);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_produto;
        RoundedImageView iv_perfil;
        TextView tv_nome;
        TextView tv_data;
        TextView tv_nome_user;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_produto = (ImageView) itemView.findViewById(R.id.iv_produto);
            iv_perfil = (RoundedImageView) itemView.findViewById(R.id.iv_perfil);
            tv_nome = (TextView) itemView.findViewById(R.id.tv_nome);
            tv_data = (TextView) itemView.findViewById(R.id.tv_data);
            tv_nome_user = (TextView) itemView.findViewById(R.id.tv_nome_user);

        }
    }

    private String formatDate(String data) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = fmt.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fmt.format(date);
    }


    private void carregaImagem(final View imagem, final String url) {
        Picasso.with(mContext)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into((ImageView) imagem, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(url)
                                //.error(R.drawable.header)
                                .into((ImageView) imagem, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });

                    }
                });
    }

    public interface onClick {
        public void onClick(View view, int idx, String key);
    }

}
