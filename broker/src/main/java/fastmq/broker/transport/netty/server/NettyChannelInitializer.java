package fastmq.broker.transport.netty.server;

import fastmq.broker.transport.netty.server.handler.ServerBusinessHandler;
import fastmq.common.netty.handler.RpcObjectHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Author: Jason Chen Date: 2018/7/9
 */
@Sharable
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final NettyChannelInitializer INSTANCE = new NettyChannelInitializer();

    private NettyChannelInitializer() {

    }

    static NettyChannelInitializer getInstance() {
        return INSTANCE;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
            .addLast(RpcObjectHandler.getInstance())
            .addLast(ServerBusinessHandler.getInstance());
    }
}
