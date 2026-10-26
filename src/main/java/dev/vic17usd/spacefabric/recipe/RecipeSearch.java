package dev.vic17usd.spacefabric.recipe;

import java.text.Normalizer;
import java.util.Locale;

/** Search behavior is independent of rendering and the current language. */
public final class RecipeSearch {
    private RecipeSearch() { }
    private static String normalize(String text) {
        return Normalizer.normalize(text,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);
    }
    public static boolean matches(String query,String name,String id) {
        String text=normalize(name+" "+id),namespace=id.split(":",2)[0];
        for(String token:normalize(query).trim().split("\\s+")) {
            if(token.startsWith("@")) {if(!namespace.contains(token.substring(1)))return false;}
            else if(!text.contains(token))return false;
        }
        return true;
    }
}
