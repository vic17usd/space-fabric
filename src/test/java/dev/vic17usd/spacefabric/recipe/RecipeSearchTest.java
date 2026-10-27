package dev.vic17usd.spacefabric.recipe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeSearchTest {
    @Test void emptyQueryKeepsTheCatalogVisible(){assertTrue(RecipeSearch.matches("","Lingote de titanio","space_fabric:titanium_ingot"));}
    @Test void namesIgnoreCaseAndAccents(){assertTrue(RecipeSearch.matches("NIQUEL","Lingote de níquel","space_fabric:nickel_ingot"));}
    @Test void namespaceFilterAndAllWordsMustMatch(){
        assertTrue(RecipeSearch.matches("@space_fabric lingote níquel","Lingote de níquel","space_fabric:nickel_ingot"));
        assertFalse(RecipeSearch.matches("@minecraft niquel","Lingote de níquel","space_fabric:nickel_ingot"));
        assertFalse(RecipeSearch.matches("@space_fabric oro","Lingote de níquel","space_fabric:nickel_ingot"));
    }
    @Test void registryIdsCanBeSearchedEvenInAnotherLanguage(){assertTrue(RecipeSearch.matches("titanium_ingot","Lingote de titanio","space_fabric:titanium_ingot"));}
}
