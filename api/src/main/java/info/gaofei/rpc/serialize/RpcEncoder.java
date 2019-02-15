package info.gaofei.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by GaoQingming on 2019/2/10.
 */
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            //写入本次数据长度，获取的时候前四位就是数据的长度，并根据数据长度判断是否接收到了所有数据，（nio有可能受到部分数据）
            //可以去瞅瞅Decoder
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
