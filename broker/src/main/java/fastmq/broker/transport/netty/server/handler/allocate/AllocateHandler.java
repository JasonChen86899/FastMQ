package fastmq.broker.transport.netty.server.handler.allocate;

import java.io.IOException;
import java.util.Objects;

import com.github.zkclient.ZkClient;
import fastmq.broker.partition.PartitionAllocate;
import fastmq.broker.spring.SpringUtil;
import fastmq.broker.transport.netty.server.handler.OrderHandler;
import fastmq.common.netty.dto.RpcObject;
import fastmq.common.netty.dto.allocate.AllocateRpcObject;
import fastmq.common.rpc.RpcTransaction;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Jason Created in 2018/7/14
 */
public class AllocateHandler implements OrderHandler, RpcTransaction {

    private static Logger LOGGER = LoggerFactory.getLogger(AllocateHandler.class);

    private static AllocateHandler INSTANCE;


    private AllocateHandler() {
    }

    public static AllocateHandler getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (AllocateHandler.class) {
                if (Objects.isNull(INSTANCE)) {
                    INSTANCE = new AllocateHandler();
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void handler(RpcObject dto, ChannelHandlerContext ctx) {
        AllocateRpcObject aDto = (AllocateRpcObject) dto;

        try {
            PartitionAllocate
                .registTopicEvent(SpringUtil.getBean(ZkClient.class), aDto.getTopicName(), aDto.getPartitionNum());
        } catch (IOException e) {
            LOGGER.error("Partition allocate error,topic_name: {},partition_num: {}",
                aDto.getTopicName(), aDto.getPartitionNum());
        }
    }

    @Override
    public void send(final Object msg, final Object sender) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) sender;
        ctx.writeAndFlush(msg);
    }

    @Override
    public Object receive(Object receiver) {
        return null;
    }
}
