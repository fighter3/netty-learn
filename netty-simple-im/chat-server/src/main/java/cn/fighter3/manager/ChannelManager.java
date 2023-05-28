package cn.fighter3.manager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Date: 2023/5/19 22:19</p>
 * <p>Author: fighter3</p>
 * <p>Description: channel管理类</p>
 */
@Component
public class ChannelManager {
    /**
     * channel map，key为userId
     */
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("userId");


    public  void addChannel(String userId, Channel channel) {
        CHANNEL_MAP.put(userId, channel);
    }

    public  void removeChannel(String userId) {
        CHANNEL_MAP.remove(userId);
    }

    public  void removeChannel(Channel channel) {
        CHANNEL_MAP.values().removeIf(value -> value.id().equals(channel.id()));
    }

    public  Channel getChannel(String userId) {
        return CHANNEL_MAP.get(userId);
    }

    public  List<Channel> getAllChannels() {
        return new ArrayList<>(CHANNEL_MAP.values());
    }

    public  List<Channel> getOtherChannels(String userId){
        List<Channel> otherChannels = CHANNEL_MAP.keySet()
                .stream()
                .filter(key -> !key.equals(userId))
                .map(CHANNEL_MAP::get)
                .collect(Collectors.toList());
        return otherChannels;
    }
}
