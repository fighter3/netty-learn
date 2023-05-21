package cn.fighter3.manager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;


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
public class ChannelManager {
    /**
     * channel map，key为userId
     */
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("userId");


    public static void addChannel(String userId, Channel channel) {
        channel.attr(USER_ID).set(userId);
        CHANNEL_MAP.put(userId, channel);
    }

    public static void removeChannel(String userId) {
        CHANNEL_MAP.remove(userId);
    }

    public static void removeChannel(Channel channel) {
        CHANNEL_MAP.values().removeIf(value -> value.id().equals(channel.id()));
    }

    public static Channel getChannel(String userId) {
        return CHANNEL_MAP.get(userId);
    }

    public static List<Channel> getAllChannels() {
        return new ArrayList<>(CHANNEL_MAP.values());
    }

    public static List<Channel> getOtherChannels(String userId){
        List<Channel> otherChannels = CHANNEL_MAP.keySet()
                .stream()
                .filter(key -> !key.equals(userId))
                .map(CHANNEL_MAP::get)
                .collect(Collectors.toList());
        return otherChannels;
    }
}
