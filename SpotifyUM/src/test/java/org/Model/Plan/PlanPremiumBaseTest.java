package org.Model.Plan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanPremiumBaseTest {

    @Test
    void testDefaultConstructorSetsPointsToZero() {
    PlanPremiumBase plan = new PlanPremiumBase();
    assertEquals(0, plan.getPoints());
    plan.setPoints(99);
    assertEquals(99, plan.getPoints());
    PlanPremiumBase plan2 = new PlanPremiumBase();
    assertNotEquals(99, plan2.getPoints());
}

    @Test
    void testCopyConstructor() {
        PlanPremiumBase original = new PlanPremiumBase();
        original.setPoints(50);
        PlanPremiumBase copy = new PlanPremiumBase(original);
        assertEquals(50, copy.getPoints());
    }

    @Test
    void testCopyConstructorZeroPoints() {
        PlanPremiumBase original = new PlanPremiumBase();
        PlanPremiumBase copy = new PlanPremiumBase(original);
        assertEquals(0, copy.getPoints());
    }

    @Test
    void testAddPointsFromZero() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.addPoints();
        assertEquals(10, plan.getPoints());
    }

    @Test
    void testAddPointsMultipleTimes() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.addPoints();
        plan.addPoints();
        plan.addPoints();
        assertEquals(30, plan.getPoints());
    }

    @Test
    void testAddPointsFromNonZero() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.setPoints(20);
        plan.addPoints();
        assertEquals(30, plan.getPoints());
    }

    @Test
    void testSetAndGetPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.setPoints(100);
        assertEquals(100, plan.getPoints());
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
        plan.setPoints(500);
        assertEquals(500, plan.getPoints());
    }

    @Test
    void testAddPointsExactIncrement() {
        PlanPremiumBase plan = new PlanPremiumBase();
        int before = plan.getPoints();
        plan.addPoints();
        int after = plan.getPoints();
        assertEquals(10, after - before);
    }

    @Test
    void testCanAccessLibrary() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canAccessLibrary());
    }

    @Test
    void testCanSkip() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canSkip());
    }

    @Test
    void testCanChooseWhatToPlay() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canChooseWhatToPlay());
    }

    @Test
    void testHasAccessToFavorites() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    void testToStringZeroPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertEquals("Plano: PremiumBase\n    Pontos: 0", plan.toString());
    }

    @Test
    void testToStringWithPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.setPoints(75);
        assertEquals("Plano: PremiumBase\n    Pontos: 75", plan.toString());
    }

    @Test
    void testGetPlanName() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertEquals("PremiumBase", plan.getPlanName());
        assertNotEquals("Free", plan.getPlanName());
        assertNotEquals("PremiumTop", plan.getPlanName());
    }
}