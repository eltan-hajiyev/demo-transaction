package com.example.demotransaction.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@DependsOn("taskExecutor")
public class ThreadPoolExecutorForInfo {
    private long totalTime;
    private long mimeTime;
    private long maxTime;
    private long minTime;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public void execute(int threadCount, Callable callable) {
        List<CompletableFuture> futureList = new ArrayList<>();
        List<CallableInfo> callableInfoList = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            CallableInfo callableInfo = new CallableInfo(callable);
            callableInfoList.add(callableInfo);
            CompletableFuture future = taskExecutor.submitListenable(callableInfo).completable();
            futureList.add(future);
        }

        futureList.stream().forEach(f -> f.join());

        minTime = callableInfoList.stream().mapToLong(t -> t.getTotalSpendTime()).min().getAsLong();
        maxTime = callableInfoList.stream().mapToLong(t -> t.getTotalSpendTime()).max().getAsLong();
        totalTime = callableInfoList.stream().mapToLong(t -> t.getTotalSpendTime()).sum();
        mimeTime = totalTime / threadCount;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getMimeTime() {
        return mimeTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    @Override
    public String toString() {
        return "ThreadPoolExecutorForInfo{" +
                "totalTime=" + totalTime +
                ", mimeTime=" + mimeTime +
                ", maxTime=" + maxTime +
                ", minTime=" + minTime +
                '}';
    }
}
