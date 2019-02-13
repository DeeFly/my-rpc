package info.gaofei.registry;

import info.gaofei.rpc.Constant;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by GaoQingming on 2019/2/10.
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    //注册服务的根目录
    private String data_path;
    //zkSession timeout minutes
    private int timeout;
    private CountDownLatch latch = new CountDownLatch(1);
    //服务注册地址
    private String registryAddress;

    public ZookeeperServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Override
    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connect();
            if (zk != null) {
                createNode(zk, data);
            }
        }
    }

    //连接zookeeper
    private ZooKeeper connect() {
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            logger.error("", e);
        }
        return zookeeper;
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            logger.error("", e);
        }
    }

}
