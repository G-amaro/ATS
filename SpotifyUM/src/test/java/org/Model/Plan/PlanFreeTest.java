package org.Model.Plan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanFreeTest {

    @Test
    void testDefaultConstructorSetsPointsToZero() {
    PlanFree plan = new PlanFree();
    assertEquals(0, plan.getPoints());
    plan.setPoints(99);
    assertEquals(99, plan.getPoints());
    PlanFree plan2 = new PlanFree();
    assertNotEquals(99, plan2.getPoints());
}

    @Test
    void testCopyConstructor() {
        PlanFree original = new PlanFree();
        original.setPoints(30);
        PlanFree copy = new PlanFree(original);
        assertEquals(30, copy.getPoints());
    }

    @Test
    void testCopyConstructorZeroPoints() {
        PlanFree original = new PlanFree();
        PlanFree copy = new PlanFree(original);
        assertEquals(0, copy.getPoints());
    }

    @Test
    void testAddPointsFromZero() {
        PlanFree plan = new PlanFree();
        plan.addPoints();
        assertEquals(5, plan.getPoints());
    }

    @Test
    void testAddPointsMultipleTimes() {
        PlanFree plan = new PlanFree();
        plan.addPoints();
        plan.addPoints();
        plan.addPoints();
        assertEquals(15, plan.getPoints());
    }

    @Test
    void testAddPointsFromNonZero() {
        PlanFree plan = new PlanFree();
        plan.setPoints(10);
        plan.addPoints();
        assertEquals(15, plan.getPoints());
    }

    @Test
    void testSetAndGetPoints() {
        PlanFree plan = new PlanFree();
        plan.setPoints(100);
        assertEquals(100, plan.getPoints());
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
        plan.setPoints(999);
        assertEquals(999, plan.getPoints());
    }

    @Test
    void testCanAccessLibrary() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canAccessLibrary());
    }

    @Test
    void testCanSkip() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canSkip());
    }

    @Test
    void testCanChooseWhatToPlay() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canChooseWhatToPlay());
    }

    @Test
    void testHasAccessToFavorites() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    void testToStringZeroPoints() {
        PlanFree plan = new PlanFree();
        assertEquals("Plano: Free\n    Pontos: 0", plan.toString());
    }

    @Test
    void testToStringWithPoints() {
        PlanFree plan = new PlanFree();
        plan.setPoints(42);
        assertEquals("Plano: Free\n    Pontos: 42", plan.toString());
    }

    @Test
    void testGetPlanName() {
        PlanFree plan = new PlanFree();
        assertEquals("Free", plan.getPlanName());
        assertNotEquals("PremiumBase", plan.getPlanName());
        assertNotEquals("PremiumTop", plan.getPlanName());
    }

    @Test
    void testAddPointsExactIncrement() {
        PlanFree plan = new PlanFree();
        int before = plan.getPoints();
        plan.addPoints();
        int after = plan.getPoints();
        assertEquals(5, after - before);
    }
}