package com.example.mini_pekkas;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestClassTest {
    private TestClass testClass = new TestClass(1);

    @Test
    public void testExecute() {
        assertEquals(2, testClass.addEpic());
    }
}
