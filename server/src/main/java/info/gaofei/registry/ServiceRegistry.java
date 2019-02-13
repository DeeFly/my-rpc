package info.gaofei.registry;

/**
 * Created by GaoQingming on 2019/2/10.
 */
public interface ServiceRegistry {

    /**
     * 像注册中心注册服务
     * @param data
     */
    void register(String data);
}
