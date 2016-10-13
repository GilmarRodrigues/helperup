package br.com.alimentar.alergia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by gilmar on 06/10/16.
 */

public class Substancia implements Parcelable {
    public static final String KEY = "substancia";
    public String nome;
    public String status;

    public Substancia() {
    }

    public Substancia(String nome, String status) {
        this.nome = nome;
        this.status = status;
    }

    protected Substancia(Parcel in) {
        nome = in.readString();
        status = in.readString();
    }

    public static final Creator<Substancia> CREATOR = new Creator<Substancia>() {
        @Override
        public Substancia createFromParcel(Parcel in) {
            return new Substancia(in);
        }

        @Override
        public Substancia[] newArray(int size) {
            return new Substancia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(status);
    }

    public List<Substancia> ser() {
        return (List<Substancia>) serialize();
    }

    public Map<String, Object> serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);
    }
}
