package com.zhuang.distributedjob;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zhuang.distributedjob.processor.ElasticJobBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan
@Configuration
@EnableConfigurationProperties({MyDistributedJobProperties.class})
@ConditionalOnProperty(name = "my.distributed-job.enable", havingValue = "true", matchIfMissing = true)
//@ConditionalOnExpression("'${my.distributed-job.enable}'.length() > 0")
public class MyDistributedJobAutoConfiguration {

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ZookeeperRegistryCenter.class)
    public ZookeeperRegistryCenter zookeeperRegistryCenter(MyDistributedJobProperties myDistributedJobProperties) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(myDistributedJobProperties.getZooKeeper().getServerLists(), myDistributedJobProperties.getZooKeeper().getNamespace()));
    }

}
