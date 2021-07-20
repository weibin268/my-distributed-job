package com.zhuang.distributedjob.job;

import com.zhuang.distributedjob.annotation.Job;
import com.zhuang.distributedjob.annotation.JobComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

@JobComponent
public class TestJob {

    private Integer count = 0;

    @Job(cron = "0/1 * * * * ?")    //每秒执行1次
    //@Job(cron = "0 10 0/1 * * ?")   //从00:10:00开始每隔1小时执行1次
    //@Job(cron = "0 0 2-6/1 * * ?")  //从02:00:00开始到06:00:00之间每隔1小时执行1次
    //@Job(cron = "0 0 3 * * ?")      //每天03:00:00执行1次
    //@Job(cron = "0 0 3,4 * * ?")    //每天03:00:00和04:00:00各执行1次
    //@Job(cron = "0 0 2 1 * ?")      //每月1日02:00:00执行1次
    public void test() {
        System.out.println("test -> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " -> " + count++);
    }

}
