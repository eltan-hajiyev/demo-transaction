package com.example.demotransaction.tools;

public class CPUTime {
    public static Long exec(Runnable runnable) {
        Long start = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - start;
    }
}
