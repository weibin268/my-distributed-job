package com.zhuang.distributedjob.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my.distributed-job")
public class MyDistributedJobProperties {

    private boolean enable;
    private ZooKeeper zooKeeper;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public static class ZooKeeper{
        private String serverLists;
        private String namespace = "distributed_job";

        public String getServerLists() {
            return serverLists;
        }

        public void setServerLists(String serverLists) {
            this.serverLists = serverLists;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }


}
