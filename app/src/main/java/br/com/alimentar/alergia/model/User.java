package br.com.alimentar.alergia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.gms.analytics.internal.zzy.i;
import static com.google.android.gms.analytics.internal.zzy.s;

/**
 * Created by gilmar on 25/09/16.
 */

@IgnoreExtraProperties
public class User implements Parcelable {
    public static final String KEY = "user";
    public String nome;
    public String email;
    public String imagem;
    public List<Substancia> substancias;


    public User() {
    }

    public User(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public User(String nome, String email, List<Substancia> substancias) {
        this.nome = nome;
        this.email = email;
        this.substancias = substancias;
    }

    public User(String nome, String email, String imagem) {
        this.nome = nome;
        this.email = email;
        this.imagem = imagem;
    }

    public User(String nome, String email, String imagem, List<Substancia> substancias) {
        this.nome = nome;
        this.email = email;
        this.imagem = imagem;
        this.substancias = substancias;
    }

    protected User(Parcel in) {
        nome = in.readString();
        email = in.readString();
        imagem = in.readString();
        substancias = in.createTypedArrayList(Substancia.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Exclude
    public Map<String, Object> toMapSubstancias() {
        Map<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("email", email);
        result.put("substancias", substancias);
        return result;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("email", email);
        result.put("imagem", imagem);

        Map<String, Object> substanciasList = new HashMap<>();
        for (int i = 0; i < substancias.size(); i++) {
            Map<String, Object> substanciasMap = new ObjectMapper().convertValue(substancias.get(i), Map.class);
            substanciasList.put(String.valueOf(i), substanciasMap);
        }
        result.put("substancias",  substanciasList);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(email);
        parcel.writeString(imagem);
        parcel.writeTypedList(substancias);
    }

    /*public Map<String, Object> serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);
    }*/

    /*public Map<String, Object> getSubstancias() {
        Map<String, Object> hashtaghMap = new ObjectMapper().convertValue(substancias, Map.class);
        return hashtaghMap;

    }*/

    public Map json() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(substancias, Map.class);
    }

}
