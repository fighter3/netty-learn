<template>
  Hello World
</template>

<script>
export default {
  name: "MainTest",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")),
      socket: null,
    };
  },
  mounted() {
    this.connectSocket();
  },
  methods: {
    // 建立Socket长连接
    connectSocket() {
      try {
        const token = localStorage.getItem("token");
        if (token && !this.socket) {

          // 创建一个原生的 WebSocket 连接，将 token 作为查询参数添加,因为有些浏览器不允许修改 WebSocket 连接的部分请求头
          this.socket = new WebSocket("ws://127.0.0.1:8886/ws?token="+token);
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
    // 启动心跳定时器
    startHeartBeat() {
      this.heartBeatTimer = setInterval(() => {
        console.log("心跳==")
        this.socket.emit("heartbeat");
      }, 10000);
    },
    // 停止心跳定时器
    stopHeartBeat() {
      clearInterval(this.heartBeatTimer);
      this.heartBeatTimer = null;
    },

  },
};
</script>