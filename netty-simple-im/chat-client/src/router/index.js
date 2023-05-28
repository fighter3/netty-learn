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
    path: '/m',
    name: 'main',
    component: MainView,
    meta: {
      requireAuth: true // 标记需要登录才能访问的页面
    }
  },
  {
    path: '/',
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
  const token = localStorage.getItem('token') // 判断用户是否已经登录
  if (to.matched.some(record => record.meta.requireAuth)) { // 如果访问需要登录才能访问的页面，但是用户没有登录
    console.log('token:', token)
    console.log('to:', to)
    console.log('from:', from)
    if (!token) { // 跳转到登录页
      console.log("redirect to login")
      next({ path: '/login' })
    } else if (to.path === '/login') {
      //用户已经登录，但是还跳转登录页
      next({ path: '/' })
    } else {
      console.log("next,to:", to)
      // 否则放行
      next()
    }
  } else { // 其他情况均放行
    next()
  }
})


export default router
