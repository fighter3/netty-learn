package cn.fighter3.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Date: 2023/5/16 9:53</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Component
public class NettyChannelManager {
    private static final Logger logger = LoggerFactory.getLogger(NettyChannelManager.class);

    private static final ConcurrentHashMap<ChannelId, Channel> channels = new ConcurrentHashMap<>();

    public static void addChannel(Channel channel) {
        channels.put(channel.id(), channel);
        logger.info(String.format("添加新的连接：%s", channel.id().asShortText()));
    }

    public static void removeChannel(Channel channel) {
        channels.remove(channel.id());
        logger.info(String.format("移除连接：%s", channel.id().asShortText()));
    }

    public static Channel getChannel(ChannelId channelId) {
        return channels.get(channelId);
    }

    public static Collection<Channel> getAllChannels() {
        return channels.values();
    }
}
