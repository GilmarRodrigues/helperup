package br.com.alimentar.alergia.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentar.alergia.R;

/**
 * Created by gilmar on 25/09/16.
 */

public class Tabelas {
    public static final String USUARIO = "Usuario";
    public static final String PRODUTO = "Produto";
    public static final String IMAGEM_PERFIL = "Perfil_Imagem";
    public static final String IMAGEM_PRODUTO = "Perfil_Produto";
    public static final String DEFAULT = "default";

    public static List<Substancia> addSubstancias(Context context) {
        String[] substancias = {context.getString(R.string.substancia_leite), context.getString(R.string.substancia_ovo),
                context.getString(R.string.substancia_oleoginosas), context.getString(R.string.substancia_crustaceos),
                context.getString(R.string.substancia_soja), context.getString(R.string.substancia_trigo),
                context.getString(R.string.substancia_peixe), context.getString(R.string.substancia_gluten)};
        List<Substancia> list = new ArrayList<>();
        for (int i = 0; i < substancias.length; i++) {
            list.add(new Substancia(substancias[i], context.getString(R.string.const_nao_contem)));
        }
        return list;
    }
}
