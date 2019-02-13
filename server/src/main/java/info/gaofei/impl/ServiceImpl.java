package info.gaofei.impl;

import info.gaofei.Service;
import info.gaofei.annotation.RPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by GaoQingming on 2019/2/10.
 */
@RPCService(Service.class)
public class ServiceImpl implements Service {
    Logger logger = LoggerFactory.getLogger(getClass());
    public ServiceImpl() {
        logger.info("initializing...");
    }

    @Override
    public String sayHello(String name) {
        logger.info("get request with param name = :{}", name);
        return "hello" + name;
    }
}
