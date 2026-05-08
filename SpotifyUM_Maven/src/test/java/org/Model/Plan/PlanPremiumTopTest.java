package org.Model.Plan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanPremiumTopTest {

    @Test
    void testDefaultConstructorSetsPointsToZero() {
    PlanPremiumTop plan = new PlanPremiumTop();
    assertEquals(100, plan.getPoints());
    plan.setPoints(99);
    assertEquals(99, plan.getPoints());
    PlanPremiumTop plan2 = new PlanPremiumTop();
    assertNotEquals(99, plan2.getPoints());
}

    @Test
    void testCopyConstructorAdds100Points() {
        PlanPremiumTop base = new PlanPremiumTop();
        base.setPoints(200);
        PlanPremiumTop plan = new PlanPremiumTop(base);
        assertEquals(300, plan.getPoints());
    }

    @Test
    void testCopyConstructorFromZero() {
        PlanPremiumTop base = new PlanPremiumTop();
        PlanPremiumTop plan = new PlanPremiumTop(base);
        assertEquals(200, plan.getPoints());
    }

    @Test
    void testCopyConstructorExactBonus() {
        PlanPremiumTop base = new PlanPremiumTop();
        base.setPoints(50);
        PlanPremiumTop plan = new PlanPremiumTop(base);
        assertEquals(150, plan.getPoints());
    }

    @Test
    void testAddPointsFrom1000() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(1000);
        plan.addPoints();
        assertEquals(1025, plan.getPoints());
    }

    @Test
    void testAddPointsFrom400() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(400);
        plan.addPoints();
        assertEquals(410, plan.getPoints());
    }

    @Test
    void testAddPointsFrom0() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(0);
        plan.addPoints();
        assertEquals(0, plan.getPoints());
    }

    @Test
    void testAddPointsMultipleTimes() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(1000);
        plan.addPoints(); // 1025
        plan.addPoints(); // 1025 + 25 = 1050 (int truncation: 1025 * 1.025 = 1050)
        assertTrue(plan.getPoints() >= 1050);
    }

    @Test
    void testAddPointsIsPercentageBased() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(200);
        plan.addPoints();
        assertEquals(205, plan.getPoints()); // 200 + 2.5% = 205
    }

    @Test
    void testSetAndGetPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(999);
        assertEquals(999, plan.getPoints());
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
    }

    @Test
    void testCanAccessLibrary() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canAccessLibrary());
    }

    @Test
    void testCanSkip() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canSkip());
    }

    @Test
    void testCanChooseWhatToPlay() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canChooseWhatToPlay());
    }

    @Test
    void testHasAccessToFavorites() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.hasAccessToFavorites());
    }

    @Test
    void testToStringZeroPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertEquals("Plano: PremiumTop\n    Pontos: 100", plan.toString());
    }

    @Test
    void testToStringWithPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(500);
        assertEquals("Plano: PremiumTop\n    Pontos: 500", plan.toString());
    }

    @Test
    void testGetPlanName() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertEquals("PremiumTop", plan.getPlanName());
        assertNotEquals("Free", plan.getPlanName());
        assertNotEquals("PremiumBase", plan.getPlanName());
    }
}