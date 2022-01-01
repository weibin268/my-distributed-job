package com.zhuang.distributedjob;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my.distributed-job")
public class MyDistributedJobProperties {

    private boolean enable = true;
    private ZooKeeper zooKeeper = new ZooKeeper();

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

    public static class ZooKeeper {

        private String serverLists = "127.0.0.1:2181";
        private String namespace = "my-distributed-job";

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
