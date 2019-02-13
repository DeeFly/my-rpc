package info.gaofei.server;

import info.gaofei.RpcHandler;
import info.gaofei.annotation.RPCService;
import info.gaofei.registry.ServiceRegistry;
import info.gaofei.rpc.RpcRequest;
import info.gaofei.rpc.RpcResponse;
import info.gaofei.rpc.serialize.RpcDecoder;
import info.gaofei.rpc.serialize.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GaoQingming on 2019/2/10.
 */
public class RpcServer implements ApplicationContextAware , InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ServiceRegistry serviceRegistry;
    private String serverAddress;
    //服务名称和服务对象的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();
    private ApplicationContext applicationContext;

    public RpcServer(ServiceRegistry serviceRegistry, String serverAddress) {
        this.serviceRegistry = serviceRegistry;
        this.serverAddress = serverAddress;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class)) // 将 RPC 请求进行解码（为了处理请求）
                                    .addLast(new RpcEncoder(RpcResponse.class)) // 将 RPC 响应进行编码（为了返回响应）
                                    .addLast(new RpcHandler(handlerMap)); // 处理 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.debug("server started on port {}", port);

            if (serviceRegistry != null) {
                serviceRegistry.register(serverAddress); // 注册服务地址
            }

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> objects = applicationContext.getBeansWithAnnotation(RPCService.class);
        if (objects != null) {
            for (Object object : objects.values()) {
                String interfaceName = object.getClass().getAnnotation(RPCService.class).value().getName();
                handlerMap.put(interfaceName, object);
            }
        }
    }
}
