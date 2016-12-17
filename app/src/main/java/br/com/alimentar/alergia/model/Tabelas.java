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
    public static final String IMAGEM_PERFIL = "Perfil";
    public static final String IMAGEM_PRODUTO = "Produto";
    public static final String DEFAULT = "default";
    public static final String URL_SUPPORT = "http://up2apps.com.br/contato/";
    public static final String URL_POLITICA_PRIVACIDADE = "http://up2apps.com.br/portifolio/help-up/politica-de-privacidade/";
    public static final String URL_TERMOS_DE_USO = "http://up2apps.com.br/portifolio/help-up/termo-de-uso/";
    public static final String URL_FACEBOOK = "https://www.facebook.com/up2apps";
    public static final String URL_TWITTER = "https://twitter.com/up2apps";
    public static final String APP_PACKAGER_NAME = "br.com.alimentar.alergia";

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

    public static String[] addCategorias(Context context) {
        String[] categorias = new String[]{context.getString(R.string.categoria_selecione_item), context.getString(R.string.categoria_bebidas),
                context.getString(R.string.categoria_biscoitos), context.getString(R.string.categoria_congelados), context.getString(R.string.categoria_doces_sobremesas),
                context.getString(R.string.categoria_enlatados), context.getString(R.string.categoria_graos_cereais), context.getString(R.string.categoria_leite),
                context.getString(R.string.categoria_iorgute), context.getString(R.string.categoria_massa),context.getString(R.string.categoria_paes),
                context.getString(R.string.categoria_bolos), context.getString(R.string.categoria_queijo_frios), context.getString(R.string.categoria_salgadinhos),
                context.getString(R.string.categoria_outros)};
        return categorias;
    }

    public static String[] addFabricantes(Context context) {
        String[] fabricantes = new String[]{context.getString(R.string.fabrica_a_saborosa), context.getString(R.string.fabrica_a_tal_da_castanha), context.getString(R.string.fabrica_a_vaca_que_ri), context.getString(R.string.fabrica_abbott), context.getString(R.string.fabrica_activeslim), context.getString(R.string.fabrica_activia), context.getString(R.string.fabrica_adams), context.getString(R.string.fabrica_adaptogen), context.getString(R.string.fabrica_ades), context.getString(R.string.fabrica_adocyl),context.getString(R.string.fabrica_adria), context.getString(R.string.fabrica_advanced_nutrition), context.getString(R.string.fabrica_agreco), context.getString(R.string.fabrica_agro_nippo), context.getString(R.string.fabrica_agtal), context.getString(R.string.fabrica_aguia), context.getString(R.string.fabrica_aida), context.getString(R.string.fabrica_ailiram)};
        return fabricantes;
    }
}
