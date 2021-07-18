package com.zhuang.distributedjob.job;

import com.zhuang.distributedjob.annotation.Job;
import com.zhuang.distributedjob.annotation.JobComponent;

@JobComponent
public class TestJob {

    @Job(cron = "0/1 * * * * ? ")
    public void test() {
        System.out.println("test");
    }

}
