package com.zhuang.distributedjob.job;

import com.zhuang.distributedjob.annotation.Job;
import com.zhuang.distributedjob.annotation.JobComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

@JobComponent
public class TestJob {

    private Integer count = 0;

    @Job(cron = "0/1 * * * * ?")//每秒执行1次
    public void test() {
        System.out.println("test -> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
