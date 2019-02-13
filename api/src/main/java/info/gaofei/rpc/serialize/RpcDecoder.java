package info.gaofei.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by GaoQingming on 2019/2/10.
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf inputByteBuf, List<Object> list) throws Exception {
        if (inputByteBuf.readableBytes() < 4) {
            return;
        }
        inputByteBuf.markReaderIndex();
        int dataLength = inputByteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }
        if (inputByteBuf.readableBytes() < dataLength) {
            inputByteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        inputByteBuf.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);
        list.add(obj);
    }
}
