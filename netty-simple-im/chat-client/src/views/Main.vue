<template>
  <div class="main">
    <h1 class="title">即时通讯</h1>
    <div class="container">
      <div class="sidebar">
        <div class="user-profile">
          <el-image :src="user.avatar" fit="cover" alt="头像" />
          <span>{{ user.nickname }}</span>
        </div>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="好友列表" name="1">
            <el-scrollbar>
              <ul class="user-list">
                <li v-for="(friend, index) in friendList" :key="index" @click="handleFriendClick(friend.username)">
                  <div class="avatar">
                    <el-image :src="friend.avatar" fit="cover" alt="头像" />
                  </div>
                  <div class="info">
                    <div class="nickname">{{ friend.nickname }}</div>
                    <div class="username">{{ friend.username }}</div>
                  </div>
                </li>
              </ul>
            </el-scrollbar>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="content">
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
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "MainView",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")),
      friendList: [],
      //groupList: [],
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
    //this.groupListRef = this.$refs.groupListRef;
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
              this.friendListRef.update();
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
        .get("/api/groups")
        .then((response) => {
          this.groupList = response.data;
          this.$nextTick(() => {
            this.groupListRef.update();
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
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
}

.nickname {
  font-size: 16px;
  font-weight: bold;
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
