import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/Login.vue'
import MainView from '../views/Main.vue'
import MainTest from '../views/MainTest.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/',
    name: 'main',
    component: MainView,
    meta: {
      requireAuth: true // 标记需要登录才能访问的页面
    }
  },
  {
    path: '/m',
    name: 'main-test',
    component: MainTest,
    meta: {
      requireAuth: true // 标记需要登录才能访问的页面
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token"); // 判断用户是否已经登录
  const requireAuth = to.meta.requireAuth; // 判断目标路由是否需要登录才能访问
  console.log("token:", token);
  console.log("requireAuth:", requireAuth);
  console.log("to:", to);
  console.log("from:", from);

  if (requireAuth && !token) {
    // 目标路由需要登录才能访问，但是用户没有登录
    console.log("redirect to login");
    next({ name: "login" });
  } else if (!requireAuth && token && to.path === "/login") {
    // 用户已经登录，但是还跳转登录页,直接跳到首页去
    next({ name: "main" });
  } else {
    console.log("next, to:", to);
    // 其他情况放行
    next();
  }
});


export default router
