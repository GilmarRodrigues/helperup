package br.com.alimentar.alergia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gilmar on 20/10/16.
 */
@IgnoreExtraProperties
public class Produto implements Parcelable{
    public static final String KEY = "produto";
    public String nome;
    public String fabricatente;
    public String codigo_barra;
    public String categoria;
    public String imagem;
    public String data;
    public String status;
    public String uid_user;
    public List<Substancia> substancias;

    public Produto() {
    }

    public Produto(String nome, String fabricatente, String codigo_barra, String categoria, String imagem, String data, String status, String uid_user) {
        this.nome = nome;
        this.fabricatente = fabricatente;
        this.codigo_barra = codigo_barra;
        this.categoria = categoria;
        this.imagem = imagem;
        this.data = data;
        this.status = status;
        this.uid_user = uid_user;
    }

    public Produto(String nome, String fabricatente, String codigo_barra, String categoria, String imagem, String data, String status, String uid_user, List<Substancia> substancias) {
        this.nome = nome;
        this.fabricatente = fabricatente;
        this.codigo_barra = codigo_barra;
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
        codigo_barra = in.readString();
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(fabricatente);
        parcel.writeString(codigo_barra);
        parcel.writeString(categoria);
        parcel.writeString(imagem);
        parcel.writeString(data);
        parcel.writeString(status);
        parcel.writeString(uid_user);
        parcel.writeTypedList(substancias);
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("fabricatente", fabricatente);
        result.put("codigo_barra", codigo_barra);
        result.put("categoria", categoria);
        result.put("imagem", imagem);
        result.put("data", data);
        result.put("status", status);
        result.put("uid_user", uid_user);

        Map<String, Object> substanciasList = new HashMap<>();
        for (int i = 0; i < substancias.size(); i++) {
            Map<String, Object> substanciasMap = new ObjectMapper().convertValue(substancias.get(i), Map.class);
            substanciasList.put(String.valueOf(i), substanciasMap);
        }
        result.put("substancias",  substanciasList);
        return result;
    }
}
