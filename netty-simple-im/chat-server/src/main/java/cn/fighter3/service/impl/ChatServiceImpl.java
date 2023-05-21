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
        //判断这个用户是不是已经上线
        if (ChannelManager.getChannel(message.getFromUserId())!=null){
            log.info("[handleOnlineMessage] user:{} is already online",message.getFromUserId());
            return;
        }
        //上线
        ChannelManager.addChannel(message.getFromUserId(),channel);
        User user= UserManager.getUserById(message.getFromUserId());
        String hello="欢迎"+user.getNickname()+"上线";
        channel.writeAndFlush(new TextWebSocketFrame(hello));
    }

    /**
     * 下线用户
     */
    @Override
    public void handleOfflineMessage(Channel channel, Message message) {
        ChannelManager.removeChannel(message.getFromUserId());
    }

    /**
     * 处理单聊消息
     */
    @Override
    public void handleSingleChat(Channel channel,Message message) {
        Channel toChannel=ChannelManager.getChannel(message.getToUserId());
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
        for (Channel toChannel:ChannelManager.getOtherChannels(message.getFromUserId())){
            toChannel.writeAndFlush(new TextWebSocketFrame(message.getContent()));
        }
    }


}
