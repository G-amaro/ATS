package org.Model.Plan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanTest {

    static class TestPlan extends Plan {
        @Override public boolean canAccessLibrary() { return true; }
        @Override public boolean canSkip() { return false; }
        @Override public boolean canChooseWhatToPlay() { return true; }
        @Override public boolean hasAccessToFavorites() { return false; }
        @Override public void addPoints() { setPoints(getPoints() + 10); }
        @Override public String toString() { return "TestPlan"; }
        @Override public String getPlanName() { return "Test Plan"; }
    }

    @Test
    void testDefaultPointsAreZero() {
        Plan plan = new TestPlan();
        assertEquals(0, plan.getPoints());
    }

    @Test
    void testSetPoints() {
        Plan plan = new TestPlan();
        plan.setPoints(50);
        assertEquals(50, plan.getPoints());
    }

    @Test
    void testSetPointsToZero() {
        Plan plan = new TestPlan();
        plan.setPoints(100);
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
    }

    @Test
    void testSetPointsMultipleValues() {
        Plan plan = new TestPlan();
        plan.setPoints(1);
        assertEquals(1, plan.getPoints());
        plan.setPoints(999);
        assertEquals(999, plan.getPoints());
    }

    @Test
    void testAddPoints() {
        Plan plan = new TestPlan();
        plan.setPoints(20);
        plan.addPoints();
        assertEquals(30, plan.getPoints());
    }

    @Test
    void testAddPointsExactIncrement() {
        Plan plan = new TestPlan();
        int before = plan.getPoints();
        plan.addPoints();
        assertEquals(10, plan.getPoints() - before);
    }

    @Test
    void testAddPointsMultipleTimes() {
        Plan plan = new TestPlan();
        plan.addPoints();
        plan.addPoints();
        assertEquals(20, plan.getPoints());
    }

    @Test
    void testCanAccessLibrary() {
        Plan plan = new TestPlan();
        assertTrue(plan.canAccessLibrary());
    }

    @Test
    void testCanSkip() {
        Plan plan = new TestPlan();
        assertFalse(plan.canSkip());
    }

    @Test
    void testCanChooseWhatToPlay() {
        Plan plan = new TestPlan();
        assertTrue(plan.canChooseWhatToPlay());
    }

    @Test
    void testHasAccessToFavorites() {
        Plan plan = new TestPlan();
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    void testToString() {
        Plan plan = new TestPlan();
        assertEquals("TestPlan", plan.toString());
    }

    @Test
    void testGetPlanName() {
        Plan plan = new TestPlan();
        assertEquals("Test Plan", plan.getPlanName());
    }
}