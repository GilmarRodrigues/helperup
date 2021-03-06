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
    public static final String URL_TWITTER = "https://twitter.com/up2appsbr";
    public static final String APP_PACKAGER_NAME = "br.com.alimentar.alergia";

    public static List<Substancia> addSubstancias(Context context) {
        String[] substancias = {context.getString(R.string.substancia_avela), context.getString(R.string.substancia_amendoas), context.getString(R.string.substancia_amendorim),
                context.getString(R.string.substancia_castanha_de_caju), context.getString(R.string.substancia_castanha_de_para),
                context.getString(R.string.substancia_cevada), context.getString(R.string.substancia_crustaceos), context.getString(R.string.substancia_gluten),
                context.getString(R.string.substancia_leite), context.getString(R.string.substancia_macadamia), context.getString(R.string.substancia_nozes),
                context.getString(R.string.substancia_ovo), context.getString(R.string.substancia_peixe), context.getString(R.string.substancia_pistache),
                context.getString(R.string.substancia_soja), context.getString(R.string.substancia_trigo)};
        List<Substancia> list = new ArrayList<>();
        for (int i = 0; i < substancias.length; i++) {
            list.add(new Substancia(substancias[i], context.getString(R.string.const_nao_contem)));
        }
        return list;
    }

    public static String[] addCategorias(Context context) {
        String[] categorias = new String[]{context.getString(R.string.categoria_selecione_item), context.getString(R.string.categoria_bebidas), context.getString(R.string.categoria_biscoitos),
                context.getString(R.string.categoria_bolos), context.getString(R.string.categoria_achocolatado), context.getString(R.string.categoria_congelados), context.getString(R.string.categoria_doces_sobremesas),
                context.getString(R.string.categoria_enlatados), context.getString(R.string.categoria_graos_cereais), context.getString(R.string.categoria_iogute),
                context.getString(R.string.categoria_leite), context.getString(R.string.categoria_massa),context.getString(R.string.categoria_paes),
                context.getString(R.string.categoria_queijo_frios), context.getString(R.string.categoria_salgadinhos), context.getString(R.string.categoria_outros)};
        return categorias;
    }

}
