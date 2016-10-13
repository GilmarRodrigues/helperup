package br.com.alimentar.alergia.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.i;
import static com.google.android.gms.analytics.internal.zzy.p;
import static com.google.android.gms.analytics.internal.zzy.s;

/**
 * Created by gilmar on 25/09/16.
 */

public class Tabelas {
    public static final String USUARIO = "Usuario";
    public static final String PRODUTO = "Produto";
    public static final String IMAGEM = "Perfil_Imagem";
    public static final List<Substancia> SUBSTANCIAS = addSubstancias();
    public static final String CONTEM = "Contem";
    public static final String NAOCONTEM = "Não Contem";

    public static List<Substancia> addSubstancias() {
        String[] substancias = {"LEITE", "OVO", "OLEÁGINOSAS", "CRUSTÁCEOS OU FRITOS DO MAR", "PEIXE", "SOJA", "TRIGO", "GLÚTEN"};
        List<Substancia> list = new ArrayList<>();
        for (int i = 0; i < substancias.length; i++) {
            list.add(new Substancia(substancias[i], NAOCONTEM));
        }
        return list;
    }
}
