package com.marciliojr.comprazfx.infra;

import com.marciliojr.comprazfx.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemNormalizer {

    private static final Map<String, String> abreviacoes;

    static {
        abreviacoes = new HashMap<>();

        abreviacoes.put("AZEIT", "AZEITE");
        abreviacoes.put("EXT", "EXTRATO");
        abreviacoes.put("CR", "CREME");
        abreviacoes.put("IOG", "IOGURTE");
        abreviacoes.put("SOBCOX", "SOBRE COXA");
        abreviacoes.put("BEB", "BEBIDA");
        abreviacoes.put("LACT", "LACTEA");
        abreviacoes.put("FERM", "FERMENTADO");
        abreviacoes.put("IOG NAT", "IOGURTE NATURAL");
        abreviacoes.put("QJO", "IOGURTE NATURAL");
        abreviacoes.put("SAB", "SABONETE");
        abreviacoes.put("DES", "DESODORANTE");
        abreviacoes.put("AZ", "AZEITE");
        abreviacoes.put("MAC", "MACARRAO");
        abreviacoes.put("CHIC", "CHICLETE");
        abreviacoes.put("CHOC", "CHOCOLATE");
        abreviacoes.put("FAR", "FARINHA");
        abreviacoes.put("ANT", "ANTISEPTICO");
        abreviacoes.put("IOG NAT", "IOGURTE NATURAL");
        abreviacoes.put("CR", "CREME");
        abreviacoes.put("QJO", "QUEIJO");
        abreviacoes.put("BISC", "BISCOITO");
        abreviacoes.put("LIMP", "LIMPADOR");
        abreviacoes.put("DES", "DESODORANTE");
        abreviacoes.put("REFRIG", "REFRIGERANTE");
        abreviacoes.put("CR.LEITE", "CREME DE LEITE");
        abreviacoes.put("SAC", "SACOLA");
        abreviacoes.put("SAB.LIQ", "SABONETE LIQUIDO");
        abreviacoes.put("QUEIJO MUSS.", "QUEIJO MUSSARELA");

    }

    public static Map<String, String> getMapaabreviacoes() {
        return Collections.unmodifiableMap(abreviacoes);
    }

    public static void normalizarNomes(List<Item> itens) {
        for (Item item : itens) {
            String[] palavras = item.getNome().trim().split(" ");
            for (int i = 0; i < palavras.length; i++) {
                String chave = palavras[i].toUpperCase();
                if (getMapaabreviacoes().containsKey(chave)) {
                    palavras[i] = getMapaabreviacoes().get(chave);
                }
            }
            item.setNome(String.join(" ", palavras));
        }
    }
}
