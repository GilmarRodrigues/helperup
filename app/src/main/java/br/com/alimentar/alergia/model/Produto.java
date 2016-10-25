package br.com.alimentar.alergia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by gilmar on 20/10/16.
 */
@IgnoreExtraProperties
public class Produto implements Parcelable{
    public static final String KEY = "produto";
    public String nome;
    public String fabricatente;
    public String categoria;
    public String imagem;
    public String data;
    public String status;
    public String uid_user;
    public List<Substancia> substancias;

    public Produto() {
    }

    public Produto(String nome, String fabricatente, String categoria, String imagem, String data, String status, String uid_user, List<Substancia> substancias) {
        this.nome = nome;
        this.fabricatente = fabricatente;
        this.categoria = categoria;
        this.imagem = imagem;
        this.data = data;
        this.status = status;
        this.uid_user = uid_user;
        this.substancias = substancias;
    }

    protected Produto(Parcel in) {
        nome = in.readString();
        fabricatente = in.readString();
        categoria = in.readString();
        imagem = in.readString();
        data = in.readString();
        status = in.readString();
        uid_user = in.readString();
        substancias = in.createTypedArrayList(Substancia.CREATOR);
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel in) {
            return new Produto(in);
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(fabricatente);
        dest.writeString(categoria);
        dest.writeString(imagem);
        dest.writeString(data);
        dest.writeString(status);
        dest.writeString(uid_user);
        dest.writeTypedList(substancias);
    }
}
