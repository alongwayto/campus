-- ============================================================
-- 智能校园设备管理系统 - 数据库初始化脚本
-- MySQL 8.0+
--
-- 注意：所有用户密码均使用 BCrypt 算法加密存储。
-- 默认密码均为 admin123
-- ============================================================

CREATE DATABASE IF NOT EXISTS `campus_device`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `campus_device`;

-- ----------------------------
-- Table: role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `role` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `name`        varchar(50)  NOT NULL COMMENT 'ROLE_ADMIN/ROLE_MAINTAINER/ROLE_USER',
  `description` varchar(200) DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- Table: user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`         bigint       NOT NULL AUTO_INCREMENT,
  `username`   varchar(50)  NOT NULL,
  `password`   varchar(255) NOT NULL COMMENT 'BCrypt hashed',
  `real_name`  varchar(100) DEFAULT NULL,
  `email`      varchar(100) DEFAULT NULL,
  `phone`      varchar(20)  DEFAULT NULL,
  `role_id`    bigint       DEFAULT NULL,
  `enabled`    tinyint(1)   DEFAULT 1,
  `created_at` datetime     DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `fk_user_role` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Table: device_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device_type` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `name`        varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备类型表';

-- ----------------------------
-- Table: device
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device` (
  `id`              bigint        NOT NULL AUTO_INCREMENT,
  `name`            varchar(200)  NOT NULL,
  `device_type_id`  bigint        DEFAULT NULL,
  `location`        varchar(200)  DEFAULT NULL,
  `status`          tinyint       DEFAULT 1 COMMENT '0=offline, 1=online, 2=fault',
  `serial_number`   varchar(100)  DEFAULT NULL,
  `manufacturer`    varchar(100)  DEFAULT NULL,
  `purchase_date`   date          DEFAULT NULL,
  `warranty_expiry` date          DEFAULT NULL,
  `description`     varchar(1000) DEFAULT NULL,
  `created_at`      datetime      DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_device_type` (`device_type_id`),
  CONSTRAINT `fk_device_type` FOREIGN KEY (`device_type_id`) REFERENCES `device_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- ----------------------------
-- Table: fault_record
-- ----------------------------
CREATE TABLE IF NOT EXISTS `fault_record` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `device_id`   bigint       NOT NULL,
  `reporter_id` bigint       NOT NULL,
  `assignee_id` bigint       DEFAULT NULL,
  `title`       varchar(200) NOT NULL,
  `description` text         DEFAULT NULL,
  `severity`    tinyint      DEFAULT 2 COMMENT '1=low, 2=medium, 3=high',
  `status`      tinyint      DEFAULT 0 COMMENT '0=pending, 1=assigned, 2=processing, 3=resolved, 4=closed',
  `reported_at` datetime     DEFAULT CURRENT_TIMESTAMP,
  `assigned_at` datetime     DEFAULT NULL,
  `resolved_at` datetime     DEFAULT NULL,
  `notes`       text         DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_fault_device`   (`device_id`),
  KEY `fk_fault_reporter` (`reporter_id`),
  KEY `fk_fault_assignee` (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障记录表';

-- ----------------------------
-- Table: operation_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id`            bigint       NOT NULL AUTO_INCREMENT,
  `user_id`       bigint       DEFAULT NULL,
  `username`      varchar(50)  DEFAULT NULL,
  `operation`     varchar(100) DEFAULT NULL,
  `method`        varchar(200) DEFAULT NULL,
  `request_url`   varchar(500) DEFAULT NULL,
  `request_param` text         DEFAULT NULL,
  `result`        varchar(10)  DEFAULT NULL,
  `ip`            varchar(50)  DEFAULT NULL,
  `created_at`    datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username`   (`username`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- Table: device_attachment (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device_attachment` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `device_id`   bigint       NOT NULL,
  `file_name`   varchar(200) NOT NULL,
  `file_path`   varchar(500) NOT NULL,
  `file_type`   varchar(50)  DEFAULT NULL,
  `file_size`   bigint       DEFAULT 0,
  `uploaded_by` bigint       DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备附件表';

-- ----------------------------
-- Table: fault_attachment (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `fault_attachment` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `fault_id`    bigint       NOT NULL,
  `file_name`   varchar(200) NOT NULL,
  `file_path`   varchar(500) NOT NULL,
  `file_type`   varchar(50)  DEFAULT NULL,
  `file_size`   bigint       DEFAULT 0,
  `uploaded_by` bigint       DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_fault_id` (`fault_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障附件表';

-- ----------------------------
-- Table: maintenance_history (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `maintenance_history` (
  `id`                bigint       NOT NULL AUTO_INCREMENT,
  `device_id`         bigint       NOT NULL,
  `maintenance_type`  varchar(50)  NOT NULL COMMENT 'preventive/corrective/emergency',
  `description`       text         DEFAULT NULL,
  `performed_by`      bigint       DEFAULT NULL,
  `cost`              decimal(10,2) DEFAULT 0.00,
  `started_at`        datetime     DEFAULT NULL,
  `completed_at`      datetime     DEFAULT NULL,
  `next_maintenance`  date         DEFAULT NULL,
  `result`            varchar(50)  DEFAULT 'completed',
  `notes`             text         DEFAULT NULL,
  `created_at`        datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_performed_by` (`performed_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维护历史表';

-- ----------------------------
-- Table: device_spare_parts (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device_spare_parts` (
  `id`            bigint        NOT NULL AUTO_INCREMENT,
  `name`          varchar(200)  NOT NULL,
  `model`         varchar(100)  DEFAULT NULL,
  `category`      varchar(100)  DEFAULT NULL,
  `stock_quantity` int          DEFAULT 0,
  `min_stock`     int           DEFAULT 5,
  `unit_price`    decimal(10,2) DEFAULT 0.00,
  `supplier`      varchar(200)  DEFAULT NULL,
  `location`      varchar(200)  DEFAULT NULL,
  `description`   varchar(500)  DEFAULT NULL,
  `created_at`    datetime      DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件管理表';

-- ----------------------------
-- Table: user_profile (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id`                bigint       NOT NULL AUTO_INCREMENT,
  `user_id`           bigint       NOT NULL,
  `avatar_url`        varchar(500) DEFAULT NULL,
  `department`        varchar(100) DEFAULT NULL,
  `position`          varchar(100) DEFAULT NULL,
  `last_login_time`   datetime     DEFAULT NULL,
  `last_login_ip`     varchar(50)  DEFAULT NULL,
  `last_login_device` varchar(200) DEFAULT NULL,
  `created_at`        datetime     DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户档案表';

-- ----------------------------
-- Table: login_record (NEW)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `login_record` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `user_id`     bigint       NOT NULL,
  `username`    varchar(50)  DEFAULT NULL,
  `login_time`  datetime     DEFAULT CURRENT_TIMESTAMP,
  `login_ip`    varchar(50)  DEFAULT NULL,
  `device_info` varchar(500) DEFAULT NULL,
  `status`      tinyint      DEFAULT 1 COMMENT '1=success, 0=failure',
  `message`     varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录记录表';

-- ============================================================
-- 初始数据
-- ============================================================

-- ----------------------------
-- 角色数据
-- ----------------------------
INSERT INTO `role` (`name`, `description`) VALUES
  ('ROLE_ADMIN',      '系统管理员，拥有所有权限'),
  ('ROLE_MAINTAINER', '维护员，负责设备维修与保养'),
  ('ROLE_USER',       '普通用户，可上报故障和查询设备');

-- ----------------------------
-- 用户数据（10个用户，密码均为 admin123）
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `phone`, `role_id`, `enabled`, `created_at`, `updated_at`) VALUES
  ('admin',      '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '系统管理员', 'admin@campus.edu',    '13800000001', 1, 1, '2026-04-01 08:00:00', '2026-04-01 08:00:00'),
  ('zhangwei',   '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '张维修',   'zhangwei@campus.edu',  '13800000002', 2, 1, '2026-04-01 08:30:00', '2026-04-01 08:30:00'),
  ('liming',     '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '李明',     'liming@campus.edu',    '13800000003', 3, 1, '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
  ('wangfang',   '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '王芳',     'wangfang@campus.edu',  '13800000004', 3, 1, '2026-04-02 08:00:00', '2026-04-02 08:00:00'),
  ('liuqiang',   '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '刘强',     'liuqiang@campus.edu',  '13800000005', 2, 1, '2026-04-02 09:00:00', '2026-04-02 09:00:00'),
  ('chenxia',    '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '陈霞',     'chenxia@campus.edu',   '13800000006', 3, 1, '2026-04-02 10:00:00', '2026-04-02 10:00:00'),
  ('zhaojun',    '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '赵军',     'zhaojun@campus.edu',   '13800000007', 2, 1, '2026-04-03 08:00:00', '2026-04-03 08:00:00'),
  ('sunli',      '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '孙丽',     'sunli@campus.edu',     '13800000008', 3, 1, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
  ('zhouping',   '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '周平',     'zhouping@campus.edu',  '13800000009', 1, 1, '2026-04-03 10:00:00', '2026-04-03 10:00:00'),
  ('huangyong',  '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6', '黄勇',     'huangyong@campus.edu', '13800000010', 2, 1, '2026-04-04 08:00:00', '2026-04-04 08:00:00');

-- ----------------------------
-- 用户档案数据
-- ----------------------------
INSERT INTO `user_profile` (`user_id`, `department`, `position`, `last_login_time`, `last_login_ip`) VALUES
  (1,  '信息中心',   '中心主任',   '2026-04-14 08:30:00', '192.168.1.100'),
  (2,  '后勤部门',   '维修工程师', '2026-04-14 08:45:00', '192.168.1.101'),
  (3,  '计算机学院', '学生',       '2026-04-13 14:20:00', '192.168.1.102'),
  (4,  '外语学院',   '教师',       '2026-04-13 09:15:00', '192.168.1.103'),
  (5,  '后勤部门',   '高级维修师', '2026-04-14 07:50:00', '192.168.1.104'),
  (6,  '图书馆',     '馆员',       '2026-04-12 16:30:00', '192.168.1.105'),
  (7,  '后勤部门',   '维修工程师', '2026-04-14 08:00:00', '192.168.1.106'),
  (8,  '行政办公室', '行政助理',   '2026-04-11 10:00:00', '192.168.1.107'),
  (9,  '信息中心',   '副主任',     '2026-04-14 09:00:00', '192.168.1.108'),
  (10, '后勤部门',   '维修技师',   '2026-04-13 08:30:00', '192.168.1.109');

-- ----------------------------
-- 设备类型数据
-- ----------------------------
INSERT INTO `device_type` (`name`, `description`) VALUES
  ('计算机设备', '台式机、笔记本电脑、服务器等计算设备'),
  ('网络设备',   '路由器、交换机、无线接入点等网络基础设施'),
  ('打印设备',   '激光打印机、喷墨打印机、复印机等输出设备'),
  ('安防设备',   '监控摄像头、门禁系统、报警器等安全防护设备'),
  ('教学设备',   '投影仪、电子白板、智能讲台等教学辅助设备'),
  ('空调设备',   '中央空调、分体空调、新风系统等温控设备'),
  ('实验设备',   '示波器、信号发生器、万用表等实验室设备');

-- ----------------------------
-- 设备数据（25台设备，状态均衡分布）
-- ----------------------------
INSERT INTO `device` (`name`, `device_type_id`, `location`, `status`, `serial_number`, `manufacturer`, `purchase_date`, `warranty_expiry`, `description`, `created_at`, `updated_at`) VALUES
  ('教学楼A-101 教师机',       1, '教学楼A 101室',     1, 'PC-2024-0001', '联想',     '2024-03-01', '2027-03-01', '教室主讲台教师用计算机',                   '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('图书馆查询终端-01',        1, '图书馆一楼大厅',    2, 'PC-2023-0088', '戴尔',     '2023-06-15', '2026-06-15', '图书检索自助查询终端',                     '2026-04-01 08:10:00', '2026-04-10 09:00:00'),
  ('教学楼B-201 学生机-01',    1, '教学楼B 201室',     1, 'PC-2024-0002', '联想',     '2024-05-10', '2027-05-10', '计算机教室学生用台式机',                   '2026-04-01 08:20:00', '2026-04-14 08:20:00'),
  ('行政楼办公电脑-03',        1, '行政楼 302室',      1, 'PC-2024-0003', '惠普',     '2024-01-15', '2027-01-15', '行政办公用电脑',                           '2026-04-01 08:30:00', '2026-04-14 08:30:00'),
  ('数据中心服务器-01',        1, '数据中心机房',      1, 'SV-2023-0001', '戴尔',     '2023-08-20', '2028-08-20', 'PowerEdge R740 机架式服务器',              '2026-04-01 08:40:00', '2026-04-14 08:40:00'),
  ('行政楼核心交换机',         2, '行政楼网络机房',    1, 'SW-2022-0005', '华为',     '2022-09-10', '2027-09-10', '校园网核心层交换机，承载全校流量',         '2026-04-01 09:00:00', '2026-04-14 09:00:00'),
  ('宿舍楼B区无线AP-03',       2, '宿舍楼B 3楼走廊',  0, 'AP-2024-0031', '锐捷',     '2024-01-20', '2027-01-20', '宿舍楼无线覆盖接入点',                     '2026-04-01 09:10:00', '2026-04-08 14:00:00'),
  ('教学楼A区无线AP-01',       2, '教学楼A 2楼走廊',  1, 'AP-2024-0032', '锐捷',     '2024-03-15', '2027-03-15', '教学楼无线覆盖接入点',                     '2026-04-01 09:20:00', '2026-04-14 09:20:00'),
  ('图书馆汇聚交换机',         2, '图书馆网络机柜',   1, 'SW-2023-0012', '华三',     '2023-11-05', '2026-11-05', '图书馆汇聚层交换机',                       '2026-04-01 09:30:00', '2026-04-14 09:30:00'),
  ('实验楼路由器-01',          2, '实验楼网络机柜',   0, 'RT-2023-0008', '思科',     '2023-07-01', '2026-07-01', '实验楼出口路由器',                         '2026-04-02 08:00:00', '2026-04-12 10:00:00'),
  ('行政办公室激光打印机',     3, '行政楼 201室',      1, 'PR-2023-0012', 'HP',       '2023-11-05', '2026-11-05', 'A4黑白激光打印机',                         '2026-04-02 08:10:00', '2026-04-14 08:10:00'),
  ('教务处复印机',             3, '行政楼 103室',      2, 'PR-2022-0007', '佳能',     '2022-07-18', '2025-07-18', 'A3彩色复合机，支持打印/复印/扫描',         '2026-04-02 08:20:00', '2026-04-13 11:00:00'),
  ('图书馆自助打印机',         3, '图书馆二楼',       1, 'PR-2024-0015', '联想',     '2024-02-28', '2027-02-28', '自助打印复印一体机',                       '2026-04-02 08:30:00', '2026-04-14 08:30:00'),
  ('校门口监控摄像头-主',      4, '正门岗亭',         1, 'CAM-2024-0001', '海康威视', '2024-05-01', '2027-05-01', '400万像素高清球机，支持夜视',              '2026-04-02 09:00:00', '2026-04-14 09:00:00'),
  ('实验楼门禁系统-01',        4, '实验楼入口',       2, 'ACC-2023-0003', '大华',     '2023-08-22', '2026-08-22', '刷卡+人脸识别双模门禁',                    '2026-04-02 09:10:00', '2026-04-11 15:00:00'),
  ('操场监控摄像头-01',        4, '操场东侧',         1, 'CAM-2024-0002', '海康威视', '2024-06-10', '2027-06-10', '室外防水枪机',                             '2026-04-02 09:20:00', '2026-04-14 09:20:00'),
  ('宿舍楼A门禁系统',          4, '宿舍楼A入口',      1, 'ACC-2024-0005', '中控',     '2024-04-15', '2027-04-15', '人脸识别门禁系统',                         '2026-04-03 08:00:00', '2026-04-14 08:00:00'),
  ('报告厅投影仪',             5, '报告厅',           1, 'PJ-2024-0002', '爱普生',   '2024-02-28', '2027-02-28', '5000流明激光投影仪',                        '2026-04-03 08:10:00', '2026-04-14 08:10:00'),
  ('教学楼B-205 电子白板',     5, '教学楼B 205室',    0, 'WB-2023-0018', '鸿合',     '2023-10-10', '2026-10-10', '86英寸交互式电子白板',                      '2026-04-03 08:20:00', '2026-04-09 16:00:00'),
  ('教学楼A-301 智能讲台',     5, '教学楼A 301室',    1, 'DK-2024-0006', '中庆',     '2024-03-20', '2027-03-20', '多媒体智能讲台系统',                        '2026-04-03 08:30:00', '2026-04-14 08:30:00'),
  ('教学楼C-102 投影仪',       5, '教学楼C 102室',    2, 'PJ-2023-0009', '明基',     '2023-09-15', '2026-09-15', '4000流明教室投影仪',                        '2026-04-03 09:00:00', '2026-04-12 14:00:00'),
  ('行政楼中央空调主机',       6, '行政楼地下室',     1, 'AC-2022-0001', '格力',     '2022-06-01', '2027-06-01', '行政楼中央空调系统主机',                    '2026-04-04 08:00:00', '2026-04-14 08:00:00'),
  ('教学楼A 壁挂空调-301',     6, '教学楼A 301室',    0, 'AC-2023-0015', '美的',     '2023-05-20', '2026-05-20', '1.5匹壁挂式冷暖空调',                      '2026-04-04 08:10:00', '2026-04-10 11:00:00'),
  ('电子实验室示波器-01',      7, '实验楼 301室',     1, 'OS-2024-0001', '泰克',     '2024-01-10', '2029-01-10', '100MHz数字示波器',                          '2026-04-04 08:20:00', '2026-04-14 08:20:00'),
  ('物理实验室万用表-05',      7, '实验楼 202室',     2, 'MM-2023-0005', '福禄克',   '2023-03-25', '2026-03-25', '高精度数字万用表',                          '2026-04-04 08:30:00', '2026-04-13 09:30:00');

-- ----------------------------
-- 故障记录数据（30条，状态/严重程度均衡分布）
-- ----------------------------
INSERT INTO `fault_record` (`device_id`, `reporter_id`, `assignee_id`, `title`, `description`, `severity`, `status`, `reported_at`, `assigned_at`, `resolved_at`, `notes`, `created_at`) VALUES
  (2, 3, NULL, '图书馆查询终端无法开机', '按下电源键后无任何反应，电源指示灯不亮，已尝试更换插座无效。', 2, 0, '2026-04-05 09:30:00', NULL, NULL, NULL, '2026-04-05 09:30:00'),
  (7, 3, 2, '宿舍B区3楼网络断连', '宿舍B区3楼全层无法连接无线网络，有线连接正常，疑似AP故障。', 3, 1, '2026-04-05 14:00:00', '2026-04-05 15:30:00', NULL, '已派维护员现场排查', '2026-04-05 14:00:00'),
  (15, 4, 2, '实验楼门禁人脸识别失败', '刷卡功能正常，人脸识别模块无响应，屏幕显示"模块初始化失败"。', 2, 2, '2026-04-04 08:00:00', '2026-04-04 09:00:00', NULL, '等待原厂工程师寄回配件', '2026-04-04 08:00:00'),
  (6, 1, 2, '核心交换机端口频繁丢包', '网络监控系统告警，Gi0/0/5 端口丢包率超过15%，影响下联设备通信。', 3, 3, '2026-04-01 10:00:00', '2026-04-01 10:30:00', '2026-04-02 17:00:00', '已更换受损光模块，测试通过，恢复正常', '2026-04-01 10:00:00'),
  (12, 8, 5, '复印机卡纸且提示墨粉不足', '复印时频繁卡纸，同时面板提示青色墨粉即将耗尽。', 1, 4, '2026-04-01 11:00:00', '2026-04-01 14:00:00', '2026-04-02 10:00:00', '已清理卡纸并更换墨粉盒，设备恢复正常', '2026-04-01 11:00:00'),
  (19, 4, NULL, '电子白板触摸失灵', '电子白板开机正常但触摸无反应，重启后问题依旧。', 2, 0, '2026-04-06 08:15:00', NULL, NULL, NULL, '2026-04-06 08:15:00'),
  (21, 6, 7, '投影仪色彩异常', '投影画面偏黄，调整色温后仍无改善，疑似灯泡老化。', 2, 2, '2026-04-06 10:30:00', '2026-04-06 11:00:00', NULL, '需要更换投影灯泡，已申请采购', '2026-04-06 10:30:00'),
  (10, 1, 5, '实验楼路由器频繁重启', '实验楼路由器每隔约30分钟自动重启，影响实验课程网络使用。', 3, 1, '2026-04-07 09:00:00', '2026-04-07 09:30:00', NULL, '初步判断为电源模块不稳定', '2026-04-07 09:00:00'),
  (23, 4, 7, '壁挂空调不制冷', '教学楼A-301空调运行但不出冷风，外机不启动。', 2, 3, '2026-04-03 14:00:00', '2026-04-03 14:30:00', '2026-04-04 16:00:00', '氟利昂泄漏，已补充制冷剂并修复泄漏点', '2026-04-03 14:00:00'),
  (25, 3, 2, '数字万用表读数不准', '物理实验室万用表电压测量偏差超过5%，影响实验精度。', 1, 4, '2026-04-02 15:00:00', '2026-04-02 15:30:00', '2026-04-03 11:00:00', '已校准表头，恢复正常精度', '2026-04-02 15:00:00'),
  (1, 4, NULL, '教师机蓝屏死机', '教学楼A-101教师机在使用PPT课件时频繁蓝屏，错误代码KERNEL_DATA_INPAGE_ERROR。', 3, 0, '2026-04-08 08:20:00', NULL, NULL, NULL, '2026-04-08 08:20:00'),
  (5, 1, 5, '服务器硬盘告警', '数据中心服务器RAID阵列中一块硬盘出现SMART预警，需及时更换。', 3, 2, '2026-04-08 10:00:00', '2026-04-08 10:15:00', NULL, '已联系供应商采购替换硬盘', '2026-04-08 10:00:00'),
  (14, 8, 7, '校门监控画面模糊', '校门口主监控摄像头画面变得模糊，夜间尤为明显。', 2, 3, '2026-04-07 07:30:00', '2026-04-07 08:00:00', '2026-04-07 15:00:00', '镜头脏污，已清洁镜头并调焦', '2026-04-07 07:30:00'),
  (9, 1, 2, '图书馆交换机端口故障', '图书馆交换机3号端口无法连接，LED指示灯不亮。', 2, 1, '2026-04-09 09:00:00', '2026-04-09 09:30:00', NULL, '疑似端口硬件损坏，已跳线至备用端口', '2026-04-09 09:00:00'),
  (13, 6, NULL, '自助打印机卡纸', '图书馆自助打印机频繁卡纸，纸张输送异常。', 1, 0, '2026-04-09 14:20:00', NULL, NULL, NULL, '2026-04-09 14:20:00'),
  (18, 4, 5, '报告厅投影仪不亮', '报告厅投影仪开机后灯泡不亮，风扇正常运转。', 3, 2, '2026-04-10 08:00:00', '2026-04-10 08:30:00', NULL, '灯泡已到使用寿命，等待新灯泡到货', '2026-04-10 08:00:00'),
  (3, 3, 7, '学生机无法联网', '教学楼B-201教室多台学生机无法连接校园网，显示IP地址获取失败。', 2, 3, '2026-04-08 13:00:00', '2026-04-08 13:30:00', '2026-04-08 17:00:00', 'DHCP服务器租约已满，已清理过期租约', '2026-04-08 13:00:00'),
  (22, 8, 10, '中央空调噪音异常', '行政楼中央空调主机运行时噪音明显增大，有异常震动。', 2, 1, '2026-04-10 10:00:00', '2026-04-10 10:30:00', NULL, '初步检查疑似压缩机轴承磨损', '2026-04-10 10:00:00'),
  (17, 3, NULL, '宿舍门禁无法刷卡', '宿舍楼A门禁系统刷卡无反应，LED指示灯闪烁异常。', 3, 0, '2026-04-11 07:30:00', NULL, NULL, NULL, '2026-04-11 07:30:00'),
  (4, 8, 2, '办公电脑运行缓慢', '行政楼302室办公电脑开机后运行极其缓慢，开机时间超过5分钟。', 1, 3, '2026-04-09 08:30:00', '2026-04-09 09:00:00', '2026-04-09 16:00:00', '已清理磁盘、查杀病毒、优化启动项', '2026-04-09 08:30:00'),
  (8, 4, 5, 'AP信号弱覆盖不全', '教学楼A 2楼部分区域WiFi信号很弱，影响正常上网。', 1, 4, '2026-04-05 16:00:00', '2026-04-05 16:30:00', '2026-04-06 14:00:00', '已调整AP发射功率和信道，信号恢复正常', '2026-04-05 16:00:00'),
  (16, 6, 7, '操场摄像头画面抖动', '操场东侧监控摄像头画面持续抖动，无法稳定监控。', 1, 3, '2026-04-11 09:00:00', '2026-04-11 09:30:00', '2026-04-11 14:00:00', '固定支架螺丝松动，已加固', '2026-04-11 09:00:00'),
  (24, 3, 10, '示波器探头损坏', '电子实验室1号示波器探头接触不良，波形显示异常。', 2, 2, '2026-04-12 08:00:00', '2026-04-12 08:30:00', NULL, '探头BNC接口氧化，需要更换探头', '2026-04-12 08:00:00'),
  (11, 8, NULL, '打印机打印模糊', '行政办公室激光打印机打印文档模糊不清，有竖线条纹。', 1, 0, '2026-04-12 10:00:00', NULL, NULL, NULL, '2026-04-12 10:00:00'),
  (20, 4, 5, '智能讲台话筒无声', '教学楼A-301智能讲台无线话筒无声音输出，有线话筒正常。', 2, 1, '2026-04-12 13:00:00', '2026-04-12 13:30:00', NULL, '疑似无线接收器故障', '2026-04-12 13:00:00'),
  (2, 3, 2, '查询终端屏幕闪烁', '图书馆查询终端开机后屏幕持续闪烁，无法正常使用。', 2, 2, '2026-04-13 08:30:00', '2026-04-13 09:00:00', NULL, '排查中，疑似显卡或排线问题', '2026-04-13 08:30:00'),
  (7, 6, 10, 'AP固件需要升级', '宿舍B区无线AP运行不稳定，厂家建议升级最新固件。', 1, 3, '2026-04-07 11:00:00', '2026-04-07 11:30:00', '2026-04-08 10:00:00', '已升级至最新固件版本v3.2.1，运行稳定', '2026-04-07 11:00:00'),
  (15, 3, 7, '门禁刷卡延迟严重', '实验楼门禁刷卡后需等待3-5秒才能开门，高峰期造成拥堵。', 1, 4, '2026-04-06 17:00:00', '2026-04-06 17:30:00', '2026-04-07 11:00:00', '已清理数据库缓存，响应时间恢复至0.5秒', '2026-04-06 17:00:00'),
  (5, 1, 5, '服务器内存报错', '数据中心服务器系统日志显示ECC内存纠错频率异常增高。', 3, 0, '2026-04-13 15:00:00', NULL, NULL, NULL, '2026-04-13 15:00:00'),
  (12, 4, 10, '复印机扫描功能故障', '教务处复印机扫描到邮箱功能失效，错误代码E301。', 2, 2, '2026-04-14 08:00:00', '2026-04-14 08:30:00', NULL, '网络扫描模块配置异常，正在修复', '2026-04-14 08:00:00');

-- ----------------------------
-- 维护历史记录
-- ----------------------------
INSERT INTO `maintenance_history` (`device_id`, `maintenance_type`, `description`, `performed_by`, `cost`, `started_at`, `completed_at`, `next_maintenance`, `result`, `notes`, `created_at`) VALUES
  (6,  'preventive', '核心交换机季度巡检', 2, 0.00, '2026-04-01 09:00:00', '2026-04-01 11:00:00', '2026-07-01', 'completed', '各端口状态正常，固件版本最新', '2026-04-01 09:00:00'),
  (22, 'preventive', '中央空调年度保养', 10, 2500.00, '2026-04-02 08:00:00', '2026-04-02 17:00:00', '2027-04-02', 'completed', '清洗滤网、检查制冷剂、润滑部件', '2026-04-02 08:00:00'),
  (5,  'corrective', '服务器硬盘更换', 5, 1800.00, '2026-04-03 14:00:00', '2026-04-03 16:00:00', NULL, 'completed', '更换故障硬盘，RAID重建完成', '2026-04-03 14:00:00'),
  (14, 'corrective', '监控摄像头镜头清洁', 7, 0.00, '2026-04-07 08:00:00', '2026-04-07 09:00:00', '2026-07-07', 'completed', '清洁镜头表面污渍，调整焦距', '2026-04-07 08:00:00'),
  (23, 'corrective', '空调制冷剂补充', 7, 350.00, '2026-04-03 15:00:00', '2026-04-04 12:00:00', NULL, 'completed', '检测到制冷剂泄漏，补充R410a制冷剂并修复泄漏点', '2026-04-03 15:00:00');

-- ----------------------------
-- 备件数据
-- ----------------------------
INSERT INTO `device_spare_parts` (`name`, `model`, `category`, `stock_quantity`, `min_stock`, `unit_price`, `supplier`, `location`, `description`, `created_at`, `updated_at`) VALUES
  ('投影仪灯泡',       'ELPLP96',     '投影配件', 3,  2, 680.00,  '爱普生授权经销商', '备件仓库A-01', '适用于爱普生CB-X41/X05系列', '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('交换机光模块',     'SFP-10G-SR',  '网络配件', 5,  3, 420.00,  '华为授权代理',     '备件仓库A-02', '万兆多模光模块',             '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('硒鼓',             'CF230A',      '打印耗材', 8,  5, 280.00,  'HP官方商城',       '备件仓库B-01', '适用于HP M203/M227系列',     '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('服务器硬盘',       'ST2000NM0008','存储配件', 2,  2, 1800.00, '戴尔官方渠道',     '备件仓库A-03', '2TB 7200RPM SAS企业级硬盘',  '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('网络水晶头',       'RJ45-CAT6',   '网络配件', 200,50, 0.80,   '安普布线',         '备件仓库B-02', '超六类屏蔽水晶头',           '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('示波器探头',       'TPP0200',     '实验配件', 4,  2, 350.00,  '泰克授权代理',     '备件仓库A-04', '200MHz无源电压探头',         '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('门禁IC卡',         'M1-S50',      '安防配件', 500,100,2.50,   '中控智慧',         '备件仓库B-03', 'Mifare S50 IC感应卡',        '2026-04-01 08:00:00', '2026-04-14 08:00:00'),
  ('空调制冷剂',       'R410A-10kg',  '空调配件', 3,  2, 280.00,  '格力官方渠道',     '备件仓库C-01', 'R410A环保制冷剂 10kg/罐',    '2026-04-01 08:00:00', '2026-04-14 08:00:00');

-- ----------------------------
-- 登录记录示例
-- ----------------------------
INSERT INTO `login_record` (`user_id`, `username`, `login_time`, `login_ip`, `device_info`, `status`, `message`) VALUES
  (1, 'admin',    '2026-04-14 08:30:00', '192.168.1.100', 'Chrome 124.0 / Windows 10', 1, '登录成功'),
  (1, 'admin',    '2026-04-13 08:25:00', '192.168.1.100', 'Chrome 124.0 / Windows 10', 1, '登录成功'),
  (2, 'zhangwei', '2026-04-14 08:45:00', '192.168.1.101', 'Firefox 125.0 / Ubuntu',    1, '登录成功'),
  (3, 'liming',   '2026-04-13 14:20:00', '192.168.1.102', 'Chrome 124.0 / macOS',      1, '登录成功'),
  (5, 'liuqiang', '2026-04-14 07:50:00', '192.168.1.104', 'Edge 124.0 / Windows 11',   1, '登录成功'),
  (1, 'admin',    '2026-04-12 18:00:00', '10.0.0.55',     'Chrome 124.0 / Android',    0, '密码错误');
