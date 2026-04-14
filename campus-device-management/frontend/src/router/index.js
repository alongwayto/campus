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
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('../views/profile/UserProfile.vue')
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
