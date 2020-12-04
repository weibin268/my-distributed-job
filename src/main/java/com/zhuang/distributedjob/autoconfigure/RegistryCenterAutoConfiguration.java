package com.zhuang.distributedjob.autoconfigure;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({MyDistributedJobProperties.class})
@ConditionalOnProperty(name = "my.distributed-job.enable", havingValue = "true", matchIfMissing = true)
//@ConditionalOnExpression("'${my.distributed-job.enable}'.length() > 0")
public class RegistryCenterAutoConfiguration {

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ZookeeperRegistryCenter.class)
    public ZookeeperRegistryCenter zookeeperRegistryCenter(MyDistributedJobProperties myDistributedJobProperties) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(myDistributedJobProperties.getZooKeeper().getServerLists(), myDistributedJobProperties.getZooKeeper().getNamespace()));
    }

}
