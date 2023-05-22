<template>
    <div class="login">
        <h1 class="title">即时通讯</h1>
        <el-form class="form" :model="form" :rules="rules" ref="form" @click="handleLogin">
            <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" native-type="submit">登录</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import axios from "axios";
export default {
    name: "LoginView",
    data() {
        return {
            form: {
                username: "",
                password: "",
            },
            rules: {
                username: [
                    { required: true, message: "请输入用户名", trigger: "blur" },
                ],
                password: [{ required: true, message: "请输入密码", trigger: "blur" }],
            },
        };
    },
    methods: {
        handleLogin() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const { username, password } = this.form;
                    axios
                        .post("/api/login", { username, password })
                        .then((response) => {
                            // 登录成功，保存token和用户信息到localStorage中
                            localStorage.setItem("token", response.data.token);
                            localStorage.setItem("user", JSON.stringify(response.data.user));
                            // 跳转到主页面
                            this.$router.push("/main");
                        })
                        .catch((error) => {
                            // 处理登录失败的情况
                            console.error(error);
                            alert("登录失败，请检查用户名和密码！");
                        });
                }
            });
        },
    },
};
</script>

<style scoped>
.login {
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

.form {
    width: 300px;
}
</style>
