<template>
  <div class="role-manage">
    <el-row :gutter="20">
      <el-col :span="8" v-for="role in roles" :key="role.name">
        <el-card class="role-card">
          <div class="role-header">
            <div class="role-icon" :style="{ background: role.color + '1a', color: role.color }">
              <el-icon :size="24"><component :is="role.icon" /></el-icon>
            </div>
            <div class="role-info">
              <div class="role-name">{{ role.label }}</div>
              <div class="role-code">{{ role.name }}</div>
            </div>
            <el-tag :type="role.tagType" size="small">{{ role.userCount }}人</el-tag>
          </div>
          <div class="role-desc">{{ role.description }}</div>
          <el-divider />
          <div class="perm-title">权限列表</div>
          <div class="perm-list">
            <el-tag
              v-for="perm in role.permissions"
              :key="perm"
              size="small"
              type="info"
              effect="plain"
              class="perm-tag"
            >{{ perm }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span class="card-title">权限矩阵</span>
      </template>
      <el-table :data="permMatrix" border stripe>
        <el-table-column prop="module" label="功能模块" width="150" />
        <el-table-column label="管理员" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.admin" style="color: #67C23A; font-size: 18px"><CircleCheck /></el-icon>
            <el-icon v-else style="color: #F56C6C; font-size: 18px"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="维修员" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.maintainer" style="color: #67C23A; font-size: 18px"><CircleCheck /></el-icon>
            <el-icon v-else style="color: #F56C6C; font-size: 18px"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="普通用户" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.user" style="color: #67C23A; font-size: 18px"><CircleCheck /></el-icon>
            <el-icon v-else style="color: #F56C6C; font-size: 18px"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="说明" min-width="200" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
const roles = [
  {
    name: 'ROLE_ADMIN',
    label: '系统管理员',
    icon: 'UserFilled',
    color: '#F56C6C',
    tagType: 'danger',
    userCount: 2,
    description: '拥有系统全部权限，可管理用户、角色、设备、故障等所有模块。',
    permissions: ['用户管理', '角色管理', '设备管理', '故障管理', '数据统计', '操作日志', '系统配置']
  },
  {
    name: 'ROLE_MAINTAINER',
    label: '维修员',
    icon: 'Tools',
    color: '#E6A23C',
    tagType: 'warning',
    userCount: 5,
    description: '负责设备维修和故障处理，可查看和处理分配给自己的故障工单。',
    permissions: ['设备查看', '故障处理', '故障派单', '数据统计', '状态监控']
  },
  {
    name: 'ROLE_USER',
    label: '普通用户',
    icon: 'User',
    color: '#409EFF',
    tagType: 'primary',
    userCount: 50,
    description: '校园普通用户，可查看设备信息和上报故障，权限受限。',
    permissions: ['设备查看', '故障上报', '数据统计']
  }
]

const permMatrix = [
  { module: '设备管理', admin: true, maintainer: false, user: false, desc: '新增、编辑、删除设备，导入导出' },
  { module: '设备查看', admin: true, maintainer: true, user: true, desc: '查看设备列表和详情' },
  { module: '故障上报', admin: true, maintainer: true, user: true, desc: '提交新的故障工单' },
  { module: '故障派单', admin: true, maintainer: true, user: false, desc: '将故障工单分配给维修员' },
  { module: '故障解决', admin: true, maintainer: true, user: false, desc: '标记故障工单已解决' },
  { module: '状态监控', admin: true, maintainer: true, user: false, desc: '实时查看设备在线状态' },
  { module: '数据统计', admin: true, maintainer: true, user: false, desc: '查看各类统计图表' },
  { module: '用户管理', admin: true, maintainer: false, user: false, desc: '管理系统用户账号' },
  { module: '角色管理', admin: true, maintainer: false, user: false, desc: '管理用户角色权限' },
  { module: '操作日志', admin: true, maintainer: false, user: false, desc: '查看系统操作记录' }
]
</script>

<style scoped>
.role-manage {
  display: flex;
  flex-direction: column;
}

.role-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  height: 100%;
}

.role-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.role-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.role-info {
  flex: 1;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.role-code {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.role-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.perm-title {
  font-size: 13px;
  font-weight: 600;
  color: #909399;
  margin-bottom: 10px;
}

.perm-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.perm-tag {
  cursor: default;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
