<template>
    <div class="login">
        <h1>IM</h1>
        <el-form :model="form" label-width="80px" class="form">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="form.password"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="login">登录</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>
  
<script>
import axios from 'axios';

export default {
    name: 'LoginComponent',
    data() {
        return {
            form: {
                username: '',
                password: '',
            },
        };
    },
    methods: {
        login() {
            const url = '/user/login';
            const data = this.form;
            axios.post(url, data).then(response => {
                const token = response.data.token;
                localStorage.setItem('token', token);
                this.$router.push('/chat');
            }).catch(error => {
                console.error(error);
            });
        },
    },
};

</script>
