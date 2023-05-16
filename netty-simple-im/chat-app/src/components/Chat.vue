<template>
    <div class="chat">
        <div class="sidebar">
            <h2>联系人列表</h2>
            <ul>
                <li v-for="(user, index) in users" :key="index">
                    <span>{{ user.name }}</span>
                    <span v-if="user.online" class="online">[在线]</span>
                    <span v-else class="offline">[离线]</span>
                </li>
                <li>
                    <a @click="logout">退出登录</a>
                </li>
            </ul>
        </div>
        <div class="main">
            <h2 v-if="selectedUser">与 {{ selectedUser.name }} 聊天中</h2>
            <h2 v-else>群聊</h2>
            <el-scrollbar ref="scrollbar" class="message-list">
                <div v-for="(message, index) in messages" :key="index" class="message-item">
                    <div :class="{ 'from-me': message.from === currentUser.id }">{{ message.content }}</div>
                </div>
            </el-scrollbar>
            <div class="compose-box">
                <el-input v-model="messageText" placeholder="请输入消息"></el-input>
                <el-button type="primary" @click="sendMessage">发送</el-button>
            </div>
        </div>
    </div>
</template>
  
<script>
import axios from 'axios';
import io from 'socket.io-client';

export default {
    name: 'ChatComponent',
    data() {
        return {
            currentUser: null,
            users: [],
            messages: [],
            selectedUser: null,
            messageText: '',
            socket: null,
        };
    },
    mounted() {
        const token = localStorage.getItem('token');
        axios.defaults.headers.common.Authorization = `Bearer ${token}`;
        axios.get('/user/profile').then(response => {
            this.currentUser = response.data;
            this.getUsers();
            this.initSocket();
        }).catch(error => {
            console.error(error);
        });
    },
    methods: {
        getUsers() {
            axios.get('/user/list').then(response => {
                this.users = response.data.map(user => ({
                    id: user.id,
                    name: user.username,
                    online: false,
                }));
            }).catch(error => {
                console.error(error);
            });
        },
        initSocket() {
            this.socket = io('/', { transports: ['websocket'] });
            this.socket.on('connect', () => {
                console.log('Connected to server.');
            });
            this.socket.on('disconnect', () => {
                console.log('Disconnected from server.');
            });

            this.socket.on('users', users => {
                this.users.forEach(user => {
                    user.online = users.includes(user.id);
                });
            });

            this.socket.on('message', message => {
                if (message.to === this.currentUser.id) {
                    this.messages.push(message);
                    this.$refs.scrollbar.scrollToBottom();
                    this.socket.emit('confirm', message);
                }
            });

            this.socket.on('confirm', message => {
                if (message.from === this.currentUser.id) {
                    const index = this.messages.findIndex(m => m.id === message.id);
                    this.messages[index].confirmed = true;
                }
            });
        },
        logout() {
            axios.post('/user/logout').then(() => {
                localStorage.removeItem('token');
                this.$router.push('/');
            }).catch(error => {
                console.error(error);
            });
        },
        sendMessage() {
            if (!this.messageText) {
                return;
            }

            const message = {
                from: this.currentUser.id,
                to: this.selectedUser ? this.selectedUser.id : '',
                content: this.messageText.trim(),
                timestamp: Date.now(),
            };

            if (this.selectedUser) {
                this.messages.push(message);
                this.$refs.scrollbar.scrollToBottom();
            }

            this.messageText = '';
            this.socket.emit('message', message);
        },
    },
};
</script>