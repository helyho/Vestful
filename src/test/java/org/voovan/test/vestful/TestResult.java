package org.voovan.test.vestful;

/**
 * 类文字命名
 *
 * @author helyho
 *         <p>
 *         Restful Framework.
 *         WebSite: https://github.com/helyho/Restful
 *         Licence: Apache v2 License
 */
public class TestResult {
    private String test1;
    private int test2;

    public TestResult(String test1, int test2){
        this.test1 = test1;
        this.test2 = test2;
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public int getTest2() {
        return test2;
    }

    public void setTest2(int test2) {
        this.test2 = test2;
    }
}
