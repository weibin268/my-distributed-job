package com.zhuang.distributedjob.processor;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.lite.spring.job.util.AopTargetUtils;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zhuang.distributedjob.annotation.Job;
import com.zhuang.distributedjob.annotation.JobComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ElasticJobBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> target = getTargetClass(bean);
        if (!target.isAnnotationPresent(JobComponent.class)) return bean;
        Set<Method> methods = MethodIntrospector.selectMethods(target, (ReflectionUtils.MethodFilter) method -> method.isAnnotationPresent(Job.class));
        for (Method method : methods) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(SimpleJob.class);
            enhancer.setCallback((MethodInterceptor) (obj, tgMethod, args, methodProxy) -> {
                if (method.getParameterTypes().length == 0) return method.invoke(bean);
                return method.invoke(bean, args);
            });
            initJob(method, (SimpleJob) enhancer.create(), method.getAnnotation(Job.class));
        }
        return bean;
    }

    private void initJob(Method method, SimpleJob simpleJob, Job jobAnnotation) {
        if (null != jobAnnotation) {
            String cron = StringUtils.defaultIfBlank(jobAnnotation.cron(), jobAnnotation.value());
            String jobName = StringUtils.isBlank(jobAnnotation.jobName()) ? getMethodDescription(method) : jobAnnotation.jobName();
            JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobName, cron, jobAnnotation.shardingTotalCount())
                    .shardingItemParameters(jobAnnotation.shardingItemParameters())
                    .description(jobAnnotation.description())
                    .failover(jobAnnotation.failover())
                    .jobParameter(jobAnnotation.jobParameter())
                    .build();
            SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, simpleJob.getClass().getCanonicalName());
            LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
                    .overwrite(jobAnnotation.overwrite())
                    .monitorExecution(jobAnnotation.monitorExecution())
                    .build();
            if (StringUtils.isNotBlank(jobAnnotation.dataSource())) {
                if (!applicationContext.containsBean(jobAnnotation.dataSource())) {
                    throw new RuntimeException("not exist datasource [" + jobAnnotation.dataSource() + "] !");
                }
                DataSource dataSource = (DataSource) applicationContext.getBean(jobAnnotation.dataSource());
                JobEventRdbConfiguration jobEventRdbConfiguration = new JobEventRdbConfiguration(dataSource);
                SpringJobScheduler springJobScheduler = new SpringJobScheduler(simpleJob, zookeeperRegistryCenter, liteJobConfiguration, jobEventRdbConfiguration);
                springJobScheduler.init();
            } else {
                SpringJobScheduler springJobScheduler = new SpringJobScheduler(simpleJob, zookeeperRegistryCenter, liteJobConfiguration);
                springJobScheduler.init();
            }
        }
    }

    private String getMethodDescription(Method method) {
        String className = method.getDeclaringClass().getName();
        String prefixName = className + "/" + method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) return prefixName;
        List<String> parameterTypeNames = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            parameterTypeNames.add(parameterType.getName());
        }
        return prefixName + "/" + StringUtils.join(parameterTypeNames, "&");
    }

    private Class<?> getTargetClass(Object bean) {
        Object target = bean;
        while (AopUtils.isAopProxy(target)) {
            target = AopTargetUtils.getTarget(target);
        }
        return target.getClass();
    }

}
