import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'devices',
        name: 'DeviceList',
        component: () => import('../views/device/DeviceList.vue')
      },
      {
        path: 'faults',
        name: 'FaultList',
        component: () => import('../views/fault/FaultList.vue')
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('../views/monitor/Monitor.vue')
      },
      {
        path: 'stats',
        name: 'Statistics',
        component: () => import('../views/stats/Statistics.vue')
      },
      {
        path: 'ai/diagnosis',
        name: 'AiDiagnosis',
        component: () => import('../views/ai/AiDiagnosis.vue')
      },
      {
        path: 'ai/prediction',
        name: 'AiPrediction',
        component: () => import('../views/ai/AiPrediction.vue')
      },
      {
        path: 'ai/anomaly',
        name: 'AiAnomaly',
        component: () => import('../views/ai/AiAnomaly.vue')
      },
      {
        path: 'user/profile',
        name: 'UserProfile',
        component: () => import('../views/user/UserProfile.vue')
      },
      {
        path: 'user/password',
        name: 'ChangePassword',
        component: () => import('../views/user/ChangePassword.vue')
      },
      {
        path: 'system/users',
        name: 'UserManage',
        component: () => import('../views/system/UserManage.vue')
      },
      {
        path: 'system/roles',
        name: 'RoleManage',
        component: () => import('../views/system/RoleManage.vue')
      },
      {
        path: 'system/logs',
        name: 'LogList',
        component: () => import('../views/system/LogList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
