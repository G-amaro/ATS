package org.spotifumtp37.model.subscription;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionPlanTest {

    @Test
    void testFreePlan() {
        FreePlan plan = new FreePlan();

        // Testa o cálculo de pontos (Soma 5 pontos aos atuais)
        assertEquals(15.0, plan.addPoints(10.0), 0.001); // 10 + 5 = 15
        assertEquals(5.0, plan.addPoints(0.0), 0.001);   // 0 + 5 = 5

        // Testa as permissões (Tudo Falso)
        assertFalse(plan.canCreatePlaylist(), "FreePlan não deve poder criar playlists");
        assertFalse(plan.canBrowsePlaylist(), "FreePlan não deve poder navegar nas playlists");
        assertFalse(plan.canAccessFavouritesList(), "FreePlan não deve ter acesso aos favoritos");
    }

    @Test
    void testPremiumBase() {
        PremiumBase plan = new PremiumBase();

        // Testa o cálculo de pontos (Soma 10 pontos aos atuais)
        assertEquals(20.0, plan.addPoints(10.0), 0.001); // 10 + 10 = 20
        assertEquals(10.0, plan.addPoints(0.0), 0.001);  // 0 + 10 = 10

        // Testa as permissões (Verdadeiro, Verdadeiro, Falso)
        assertTrue(plan.canCreatePlaylist(), "PremiumBase deve poder criar playlists");
        assertTrue(plan.canBrowsePlaylist(), "PremiumBase deve poder navegar nas playlists");
        assertFalse(plan.canAccessFavouritesList(), "PremiumBase não deve ter acesso aos favoritos");
    }

    @Test
    void testPremiumTop() {
        PremiumTop plan = new PremiumTop();

        // Testa o cálculo de pontos (Aplica bónus de 2.5%, ou seja, multiplica por 1.025)
        assertEquals(10.25, plan.addPoints(10.0), 0.001); // 10 * 1.025 = 10.25
        assertEquals(102.5, plan.addPoints(100.0), 0.001); // 100 * 1.025 = 102.5
        assertEquals(0.0, plan.addPoints(0.0), 0.001);    // 0 * 1.025 = 0

        // Testa as permissões (Tudo Verdadeiro)
        assertTrue(plan.canCreatePlaylist(), "PremiumTop deve poder criar playlists");
        assertTrue(plan.canBrowsePlaylist(), "PremiumTop deve poder navegar nas playlists");
        assertTrue(plan.canAccessFavouritesList(), "PremiumTop deve ter acesso aos favoritos");
    }
}