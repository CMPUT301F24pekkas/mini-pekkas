package com.example.mini_pekkas;

/**
 * Test Class for various task
 */
public class TestClass {
    /**
     * Test int epic for TestClass
     */
    private int epic;

    /**
     * constructor for the TestClass
     * @param epic
     */
    public TestClass(int epic) {
        this.epic = epic;
    }

    /**
     * add 1 to epic
     * @return
     * epic + 1
     */
    public int addEpic() {
        epic += 1;
        return epic;
    }
}
