package cn.fighter3.service.impl;

import cn.fighter3.handle.WebSocketChannelHandler;
import cn.fighter3.manager.ChannelManager;
import cn.fighter3.manager.UserManager;
import cn.fighter3.model.Message;
import cn.fighter3.model.User;
import cn.fighter3.service.ChatService;
import cn.fighter3.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Date: 2023/5/16 1:37</p>
 * <p>Author: fighter3</p>
 * <p>Description: 类描述</p>
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Resource
    private UserManager userManager;
    @Resource
    private ChannelManager channelManager;

    @Override
    public void handleMessage(Channel channel, Message message) {
        log.info("[handleMessage] channel:{} message:{}",channel,message);
        switch (message.getMessageType()){
            //上线消息
            case ONLINE:
                 this.handleOnlineMessage(channel,message);
                break;
            //下线消息
            case OFFLINE:
                this.handleOfflineMessage(channel,message);
                break;
            case SINGLE_CHAT:
                this.handleSingleChat(channel,message);
                break;
            case GROUP_CHAT:
                this.handleGroupChat(channel,message);
                break;
        }
    }

    /**
     * 处理用户上线消息
     */
    @Override
    public void handleOnlineMessage(Channel channel, Message message) {
        //上线
        channelManager.addChannel(message.getFromUserId(),channel);
        User user= userManager.getUserById(message.getFromUserId());
        String hello="欢迎"+user.getNickname()+"上线";
        channel.writeAndFlush(new TextWebSocketFrame(hello));
    }

    /**
     * 下线用户
     */
    @Override
    public void handleOfflineMessage(Channel channel, Message message) {
        channelManager.removeChannel(message.getFromUserId());
    }

    /**
     * 处理单聊消息
     */
    @Override
    public void handleSingleChat(Channel channel,Message message) {
        Channel toChannel=channelManager.getChannel(message.getToUserId());
        if (toChannel==null){
            log.error("[handleSingleChat] toUserId:{} can not find channel.",message.getToUserId());
            channel.writeAndFlush(new TextWebSocketFrame("该用户已下线"));
            return;
        }
        toChannel.writeAndFlush(new TextWebSocketFrame(message.getContent()));
    }

    /**
     * 处理群聊消息
     */
    @Override
    public void handleGroupChat(Channel channel, Message message) {
        for (Channel toChannel:channelManager.getOtherChannels(message.getFromUserId())){
            toChannel.writeAndFlush(new TextWebSocketFrame(message.getContent()));
        }
    }


}
