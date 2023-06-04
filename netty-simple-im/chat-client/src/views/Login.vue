<template>
    <body id="login-page">
        <el-form class="login-container" label-position="left" label-width="0px">
            <h3 class="login_title">系统登录</h3>
            <el-form-item>
                <el-input type="text" v-model="form.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item>
                <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item style="width: 100%">
                <el-button type="primary" style="width: 100%;  border: none" @click="login">登录</el-button>
            </el-form-item>
        </el-form>
    </body>
</template>

<script>
import { ElMessage } from 'element-plus';
import axios from 'axios';
export default {
    name: "LoginView",
    data() {
        return {
            form: {
                username: "",
                password: "",
            },
        };
    },
    methods: {
        login() {
            const { username, password } = this.form;
            axios
                .post("/im/user/login", { username, password })
                .then((response) => {
                    // 登录成功，保存token和用户信息到localStorage中
                    localStorage.setItem("token", response.data.token);
                    console.log("saved token: ", response.data.token);
                    console.log(localStorage.getItem("token"))
                    localStorage.setItem("user", JSON.stringify(response.data.user));
                    console.log("login response:" + JSON.stringify(response))
                    this.$router.replace({ path: "/" })

                })
                .catch((error) => {
                    // 处理登录失败的情况
                    console.error(error);
                    ElMessage.error('登录失败，请检查用户名和密码！')
                });
        },
    },
};
</script>

<style scoped>
#login-page {
    display: flex;
    justify-content: center;
    align-items: center;
    background: url("../assets/bg.jpg") no-repeat;
    background-position: center;
    height: 100%;
    width: 100%;
    background-size: cover;
    position: fixed;
    margin: 0;
    padding: 0;
}

body {
    margin: 0;
    padding: 0;
}

.login-container {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 90px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
}

.login_title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
}

.title {
    font-size: 36px;
    margin-bottom: 50px;
}
</style>
