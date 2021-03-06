package fastmq.client.consumer.transport.netty;

import fastmq.common.netty.handler.RpcObjectHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Description:
 * @Author: Jason Created in 2018/7/26
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final NettyChannelInitializer INSTANCE = new NettyChannelInitializer();

    private NettyChannelInitializer() {
    }

    static NettyChannelInitializer getInstance() {
        return INSTANCE;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
            .addLast(RpcObjectHandler.getInstance());
    }
}
