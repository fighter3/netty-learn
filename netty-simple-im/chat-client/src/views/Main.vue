<template>
  <!--采用Container 布局容器布局-->
  <el-container>
    <!--顶部，标题个人信息等-->
    <el-header
      style="height: 80px; background-color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,0,0,.1);">
      <div style="display: flex; align-items: center;">
        <h1 style="margin: 0;">聊天系统</h1>
      </div>
      <!--折叠菜单-->
      <el-dropdown>
        <div>
          <el-avatar :size="36" :src="user.avatar" style="margin-left: 10px;" />
          <span>{{ user.nickname }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">退出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

    </el-header>

    <!--侧边栏，群组列表、好友列表-->
    <el-aside width="200px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="好友列表" name="1" v-show="activeTab === '1'">
          <el-scrollbar>
            <ul class="user-list">
              <li v-for="(friend, index) in friendList" :key="index" @click="handleFriendClick(friend.userId)">
                <el-row :gutter="30" align="middle">
                  <el-col :span="4">
                    <el-avatar :size="size" :src="friend.avatar" />
                  </el-col>
                  <el-col :span="16">
                    <el-link>{{ friend.nickname }}</el-link>
                  </el-col>
                </el-row>
              </li>
            </ul>
          </el-scrollbar>
        </el-tab-pane>
        <el-tab-pane label="群组列表" name="1" v-show="activeTab === '2'">
          <el-scrollbar>
            <ul class="user-list">
              <li v-for="(group, index) in groupList" :key="index" @click="handleSendGroupMessage(group.groupId)">
                <div class="info">
                  <el-avatar :size="size" :src="group.avatar" />
                  <div class="nickname">{{ group.name }}</div>
                </div>
              </li>
            </ul>
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </el-aside>
    <!--中间部分-->
    <el-main>
      <el-tabs v-model="activeChat">
        <el-tab-pane v-for="(chat, index) in chatList" :key="index" :label="
          chat.type === 'private' ? chat.user.nickname : chat.group.name
        " :name="chat.id">
          <div class="chat-box">
            <el-scrollbar>
              <ul class="chat-message-list">
                <li v-for="(message, index) in chat.messageList" :key="index"
                  :class="{ self: message.senderId === user.id }">
                  <div class="avatar">
                    <el-image :src="message.senderAvatar" fit="cover" alt="头像" />
                  </div>
                  <div class="message-content">
                    <div class="sender-info">
                      <div class="nickname">{{ message.senderNickname }}</div>
                      <div class="time">{{ message.sendTime }}</div>
                    </div>
                    <div class="content">{{ message.content }}</div>
                  </div>
                </li>
              </ul>
            </el-scrollbar>
            <div class="chat-input-box">
              <el-input v-model="chatInput" placeholder="请输入消息" clearable></el-input>
              <el-button class="send-btn" type="primary" @click="handleSend(chat)">发送</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import axios from "axios";

export default {
  name: "MainView",


  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")),
      friendList: [],
      groupList: [],
      chatList: [],
      activeTab: "1",
      activeChat: "",
      chatInput: "",
      socket: null,
      friendListRef: null,
      groupListRef: null,
      chatListRef: null,
    };
  },
  mounted() {
    this.friendListRef = this.$refs.friendListRef;
    this.groupListRef = this.$refs.groupListRef;
    this.chatListRef = this.$refs.chatListRef;
    this.getFriendList();
    //this.getGroupList();
    this.connectSocket();
  },
  methods: {
    // 建立Socket长连接
    connectSocket() {
      try {
        const token = localStorage.getItem("token");
        if (token && !this.socket) {

          // 创建一个原生的 WebSocket 连接，将 token 作为查询参数添加,因为有些浏览器不允许修改 WebSocket 连接的部分请求头
          this.socket = new WebSocket("ws://127.0.0.1:8886/ws?token=" + token);
          //this.socket = new WebSocket("ws://127.0.0.1:8886/ws");

          // 监听连接成功事件
          this.socket.addEventListener("open", () => {
            console.log("WebSocket已连接");

          });

          this.socket.addEventListener("error", error => {
            console.error("WebSocket连接错误：", error);
          });

          this.socket.addEventListener("close", event => {
            console.log("WebSocket连接已关闭：", event);
          });

          // 监听新消息事件
          this.socket.addEventListener("message", this.handleNewMessage);
        }
      } catch (error) {
        console.error("连接Socket时出现异常：", error);
      }
    },
    // 处理新消息的方法
    handleNewMessage(event) {
      try {
        const message = JSON.parse(event.data);
        console.log("收到新消息：", message);

        // 根据收到的消息进行相应的处理，例如更新数据、显示通知等。
        // 以下是一个假设的示例，您需要根据实际情况编写代码。
        if (message.type === 'notification') {
          this.showNotification(message.content);
        } else if (message.type === 'update_data') {
          this.updateData(message.data);
        } else {
          console.warn('未知消息类型：', message.type);
        }
      } catch (error) {
        console.error("处理消息时出现异常：", error);
      }
    },
    // 获取好友列表
    getFriendList() {
      axios
        .get("/im/user/friends", {
          headers: {
            "token": localStorage.getItem("token")
          }
        })
        .then((response) => {
          this.friendList = response.data;
          if (response.data) {
            this.$nextTick(() => {
              this.friendListRef = this.$refs.friendList;
              if (this.friendListRef) {
                this.friendListRef.update();
              }
            });
          }

        })
        .catch((error) => {
          console.error(error);
        });
    },
    // 获取群组列表
    getGroupList() {
      axios
        .get("/im/user/groups", {
          headers: {
            "token": localStorage.getItem("token")
          }
        })
        .then((response) => {
          this.groupList = response.data;
          this.$nextTick(() => {
            this.groupListRef = this.$refs.groupList;
            if (this.groupListRef) {
              this.groupListRef.update();
            }
          });
        })
        .catch((error) => {
          console.error(error);
        });
    },
    // 处理发送消息事件
    handleSend(chat) {
      const content = this.chatInput.trim();
      if (content) {
        const { type } = chat;
        const token = localStorage.getItem("token");
        const data = { content };
        if (type === "private") {
          data.toUserId = chat.user.id;
          this.socket.emit("privateMessage", { token, data });
        } else if (type === "group") {
          data.groupId = chat.group.id;
          this.socket.emit("groupMessage", { token, data });
        }
        chat.messageList.push({
          senderId: this.user.id,
          senderNickname: this.user.nickname,
          senderAvatar: this.user.avatar,
          content,
          sendTime: new Date().toLocaleString(),
        });
        this.chatInput = "";
        this.$nextTick(() => {
          this.chatListRef.update();
        });
      }
    },
    //处理群组消息
    handleSendGroupMessage(chat) {
      const content = this.chatInput.trim();
      if (content) {
        const data = { content, groupId: chat.group.id };
        const token = localStorage.getItem("token");
        this.socket.emit("groupMessage", { token, data });

        chat.messageList.push({
          senderId: this.user.id,
          senderNickname: this.user.nickname,
          senderAvatar: this.user.avatar,
          content,
          sendTime: new Date().toLocaleString(),
        });
        this.chatInput = "";
        this.$nextTick(() => {
          this.chatListRef.update();
        });
      }
    },
  },
  //退出登录
  handleLogout() {
    axios
      .post("/im/user/logout", {
        headers: {
          "token": localStorage.getItem("token")
        }
      })
      .then(() => {
        //清除本地token和user，跳转到登录页面
        // 清除本地存储的用户信息和 token
        localStorage.removeItem('user');
        localStorage.removeItem('token');

        //断开socket连接
        this.socket.disconnect();

        //跳转到登录页面
        this.$router.push('/login');
      })
      .catch((error) => {
        console.error(error);
      });
  },
};
</script>
<style scoped>
.main {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.title {
  font-size: 36px;
  margin-bottom: 50px;
}

.container {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: center;
  width: 1200px;
  height: 600px;
}

.sidebar {
  width: 300px;
  height: 100%;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  padding: 20px;
  background-color: #f2f6fc;
}

.user-profile img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.user-profile span {
  margin-top: 10px;
  font-size: 16px;
}

.user-list,
.group-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.user-list li,
.group-list li {
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 60px;
  padding: 10px;
  cursor: pointer;
}

.user-list li:hover,
.group-list li:hover {
  background-color: #f2f6fc;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.avatar img {
  width: 100%;
}

.info {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: center;
  margin-right: 10px;
  vertical-align: middle;
}

.nickname {
  font-size: 16px;
  font-weight: bold;
  display: inline-block;
  vertical-align: middle;
}

.username {
  font-size: 14px;
  color: #999;
}

.content {
  flex: 1;
  height: 100%;
  padding: 20px;
  border-left: 1px solid #e6e6e6;
}

.chat-box {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chat-message-list {
  list-style: none;
  margin: 0;
  padding: 0;
  overflow-y: auto;
}

.chat-message-list li {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  margin-bottom: 10px;
}

.chat-message-list li.self {
  justify-content: flex-end;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.avatar img {
  width: 100%;
}

.message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  background-color: #f2f6fc;
  border-radius: 5px;
  padding: 10px;
}

.sender-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 5px;
}

.nickname {
  font-size: 14px;
  font-weight: bold;
  margin-right: 10px;
}

.time {
  font-size: 12px;
  color: #999;
}

.content {
  font-size: 14px;
  margin-top: 5px;
}

.chat-input-box {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.el-input {
  flex: 1;
  margin-right: 10px;
}

.send-btn {
  width: 80px;
}
</style>
