-- ============================================================
-- 智能校园设备管理系统 - 数据库初始化脚本
-- MySQL 8.0+
--
-- 注意：所有用户密码均使用 BCrypt 算法加密存储。
-- 若需自定义密码，可使用以下方式生成 BCrypt 哈希：
--   Spring Boot: new BCryptPasswordEncoder().encode("yourPassword")
--   在线工具: https://bcrypt-generator.com/
-- ============================================================

CREATE DATABASE IF NOT EXISTS `campus_device`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `campus_device`;

-- ============================================================
-- 表结构定义
-- ============================================================

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
-- Table: user_profile (新增)
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id`              bigint       NOT NULL AUTO_INCREMENT,
  `user_id`         bigint       NOT NULL,
  `avatar_url`      varchar(500) DEFAULT NULL,
  `department`      varchar(100) DEFAULT NULL,
  `position`        varchar(100) DEFAULT NULL,
  `last_login_time` datetime     DEFAULT NULL,
  `last_login_ip`   varchar(50)  DEFAULT NULL,
  `created_at`      datetime     DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户档案表';

-- ----------------------------
-- Table: login_record (新增)
-- ----------------------------
DROP TABLE IF EXISTS `login_record`;
CREATE TABLE IF NOT EXISTS `login_record` (
  `id`           bigint       NOT NULL AUTO_INCREMENT,
  `user_id`      bigint       NOT NULL,
  `username`     varchar(50)  DEFAULT NULL,
  `login_time`   datetime     DEFAULT CURRENT_TIMESTAMP,
  `login_ip`     varchar(50)  DEFAULT NULL,
  `login_device` varchar(200) DEFAULT NULL,
  `status`       tinyint      DEFAULT 1 COMMENT '1=success, 0=failure',
  `message`      varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录记录表';

-- ----------------------------
-- Table: maintenance_history (新增)
-- ----------------------------
DROP TABLE IF EXISTS `maintenance_history`;
CREATE TABLE IF NOT EXISTS `maintenance_history` (
  `id`               bigint        NOT NULL AUTO_INCREMENT,
  `device_id`        bigint        NOT NULL,
  `maintenance_type` varchar(50)   NOT NULL COMMENT 'routine/repair/upgrade',
  `description`      text          DEFAULT NULL,
  `maintainer_id`    bigint        DEFAULT NULL,
  `cost`             decimal(10,2) DEFAULT NULL,
  `start_time`       datetime      DEFAULT NULL,
  `end_time`         datetime      DEFAULT NULL,
  `status`           tinyint       DEFAULT 0 COMMENT '0=planned, 1=in_progress, 2=completed',
  `notes`            text          DEFAULT NULL,
  `created_at`       datetime      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_maint_device` (`device_id`),
  KEY `fk_maint_user` (`maintainer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维护历史表';

-- ============================================================
-- 初始数据
-- ============================================================

-- ----------------------------
-- 角色数据（3 条）
-- ----------------------------
INSERT INTO `role` (`name`, `description`) VALUES
  ('ROLE_ADMIN',      '系统管理员，拥有所有权限'),
  ('ROLE_MAINTAINER', '维护员，负责设备维修与保养'),
  ('ROLE_USER',       '普通用户，可上报故障和查询设备');

-- ----------------------------
-- 用户数据（10 条）
-- 密码均使用 BCrypt 加密（强度 10）：
--   admin      → admin123
--   maintainer → maint123
--   user1      → user123
--   其余用户   → campus123
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `phone`, `role_id`, `enabled`) VALUES
  ('admin',
   '$2b$10$lEXaHyrwCamDRPISIx6V6e3kjJxbYfcLU2dZR0Q/7O6LgolpJavw6',
   '系统管理员', 'admin@campus.edu', '13800000001',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_ADMIN'), 1),
  ('maintainer',
   '$2b$10$w9d1fckjWZWtEUAYvPUCHeWTNUywQHoQ36uwy93S5V/2y0Gn5DJ.e',
   '张维修', 'maintainer@campus.edu', '13800000002',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_MAINTAINER'), 1),
  ('user1',
   '$2b$10$mltsEliCiCi8IL5UCVH7hOzPLTWcNsIHW3LBnMp2Ol59nTA3GGRZa',
   '李同学', 'user1@campus.edu', '13800000003',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1),
  ('wangwei',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '王维', 'wangwei@campus.edu', '13800000004',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_MAINTAINER'), 1),
  ('liuqiang',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '刘强', 'liuqiang@campus.edu', '13800000005',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_MAINTAINER'), 1),
  ('zhangsan',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '张三', 'zhangsan@campus.edu', '13800000006',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1),
  ('lisi',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '李四', 'lisi@campus.edu', '13800000007',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1),
  ('wangwu',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '王五', 'wangwu@campus.edu', '13800000008',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1),
  ('zhaoliu',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '赵六', 'zhaoliu@campus.edu', '13800000009',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1),
  ('sunqi',
   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
   '孙七', 'sunqi@campus.edu', '13800000010',
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1);

-- ----------------------------
-- 设备类型数据（7 条）
-- ----------------------------
INSERT INTO `device_type` (`name`, `description`) VALUES
  ('计算机设备', '台式机、笔记本电脑、服务器等计算设备'),
  ('网络设备',   '路由器、交换机、无线接入点等网络基础设施'),
  ('打印设备',   '激光打印机、喷墨打印机、复印机等输出设备'),
  ('安防设备',   '监控摄像头、门禁系统、报警器等安全防护设备'),
  ('教学设备',   '投影仪、电子白板、智能讲台等教学辅助设备'),
  ('多媒体设备', '音响系统、视频会议终端、多媒体中控等设备'),
  ('实验室设备', '示波器、通风柜、光学平台等实验室专用设备');

-- ----------------------------
-- 设备数据（25 条）
-- 状态分布：约 60% online(1), 20% offline(0), 20% fault(2)
-- ----------------------------
INSERT INTO `device` (`name`, `device_type_id`, `location`, `status`, `serial_number`, `manufacturer`, `purchase_date`, `warranty_expiry`, `description`) VALUES
  -- 计算机设备（4 台）
  ('教学楼A-101 教师机',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '教学楼A 101室', 1, 'PC-2023-0001', '联想', '2023-03-01', '2026-03-01',
   '教室主讲台教师用计算机'),
  ('图书馆查询终端-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '图书馆一楼大厅', 2, 'PC-2022-0088', '戴尔', '2022-06-15', '2025-06-15',
   '图书检索自助查询终端，当前存在故障'),
  ('实验楼计算机-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '实验楼 201室', 1, 'PC-2024-0015', '联想', '2024-01-10', '2027-01-10',
   '实验楼公共计算机，供学生上机使用'),
  ('教学楼C-301 教师机',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '教学楼C 301室', 0, 'PC-2024-0023', '惠普', '2024-03-15', '2027-03-15',
   '教学楼C区教室教师用计算机，当前离线'),

  -- 网络设备（4 台）
  ('行政楼核心交换机',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '行政楼网络机房', 1, 'SW-2021-0005', '华为', '2021-09-10', '2024-09-10',
   '校园网核心层交换机，承载全校流量'),
  ('宿舍楼B区无线AP-03',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '宿舍楼B 3楼走廊', 0, 'AP-2023-0031', '锐捷', '2023-01-20', '2026-01-20',
   '宿舍楼无线覆盖接入点，当前离线'),
  ('图书馆汇聚交换机',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '图书馆网络机柜', 1, 'SW-2024-0010', '华三', '2024-02-20', '2027-02-20',
   '图书馆区域汇聚层交换机，连接馆内所有终端'),
  ('教学楼A无线AP-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '教学楼A 2楼走廊', 2, 'AP-2024-0045', '锐捷', '2024-04-10', '2027-04-10',
   '教学楼A区无线接入点，当前信号异常'),

  -- 打印设备（4 台）
  ('行政办公室激光打印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '行政楼 201室', 1, 'PR-2022-0012', 'HP', '2022-11-05', '2025-11-05',
   'A4黑白激光打印机'),
  ('教务处复印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '行政楼 103室', 1, 'PR-2021-0007', '佳能', '2021-07-18', '2024-07-18',
   'A3彩色复合机，支持打印/复印/扫描'),
  ('图书馆自助打印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '图书馆二楼自助区', 1, 'PR-2024-0020', 'HP', '2024-05-12', '2027-05-12',
   '自助扫码打印终端，支持微信/支付宝付费'),
  ('实验楼打印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '实验楼 105室', 2, 'PR-2023-0035', '兄弟', '2023-08-25', '2026-08-25',
   '黑白激光打印机，当前卡纸故障'),

  -- 安防设备（3 台）
  ('校门口监控摄像头-主',
   (SELECT `id` FROM `device_type` WHERE `name` = '安防设备'),
   '正门岗亭', 1, 'CAM-2023-0001', '海康威视', '2023-05-01', '2026-05-01',
   '200万像素高清球机，支持夜视'),
  ('实验楼门禁系统-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '安防设备'),
   '实验楼入口', 2, 'ACC-2022-0003', '大华', '2022-08-22', '2025-08-22',
   '刷卡+人脸识别双模门禁，当前人脸模块故障'),
  ('实验楼走廊监控-02',
   (SELECT `id` FROM `device_type` WHERE `name` = '安防设备'),
   '实验楼 2楼走廊', 1, 'CAM-2024-0012', '海康威视', '2024-06-01', '2027-06-01',
   '400万像素枪机，支持智能移动侦测'),

  -- 教学设备（4 台）
  ('报告厅投影仪',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '报告厅', 1, 'PJ-2023-0002', '爱普生', '2023-02-28', '2026-02-28',
   '5000流明激光投影仪'),
  ('教学楼B-205 电子白板',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '教学楼B 205室', 1, 'WB-2022-0018', '鸿合', '2022-10-10', '2025-10-10',
   '86英寸交互式电子白板'),
  ('教学楼C-201 投影仪',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '教学楼C 201室', 0, 'PJ-2024-0008', '明基', '2024-07-15', '2027-07-15',
   '4500流明激光投影仪，当前灯泡待更换'),
  ('教学楼A-301 电子白板',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '教学楼A 301室', 1, 'WB-2024-0025', '希沃', '2024-09-01', '2027-09-01',
   '75英寸智慧黑板，支持触控书写'),

  -- 多媒体设备（3 台）
  ('报告厅音响系统',
   (SELECT `id` FROM `device_type` WHERE `name` = '多媒体设备'),
   '报告厅', 1, 'MM-2024-0001', 'BOSE', '2024-01-20', '2027-01-20',
   '专业扩声系统，含功放、音箱、无线话筒'),
  ('教学楼B-101 多媒体中控',
   (SELECT `id` FROM `device_type` WHERE `name` = '多媒体设备'),
   '教学楼B 101室', 0, 'MM-2024-0002', '快捷', '2024-03-10', '2027-03-10',
   '教室多媒体集中控制面板，管理投影、音响、灯光'),
  ('会议室视频会议系统',
   (SELECT `id` FROM `device_type` WHERE `name` = '多媒体设备'),
   '行政楼 会议室301', 1, 'MM-2024-0003', '华为', '2024-05-20', '2027-05-20',
   '视频会议终端，支持4K画面和远程协作'),

  -- 实验室设备（3 台）
  ('电子实验室示波器-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '实验室设备'),
   '实验楼 301室', 1, 'LAB-2024-0001', '泰克', '2024-02-15', '2027-02-15',
   '四通道数字存储示波器，200MHz带宽'),
  ('化学实验室通风柜-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '实验室设备'),
   '实验楼 401室', 1, 'LAB-2024-0002', '赛默飞', '2024-04-08', '2027-04-08',
   '全钢通风柜，配备VAV变风量控制系统'),
  ('物理实验室光学平台',
   (SELECT `id` FROM `device_type` WHERE `name` = '实验室设备'),
   '实验楼 302室', 0, 'LAB-2024-0003', '卓立汉光', '2024-06-20', '2027-06-20',
   '隔振光学平台，用于精密光学实验');

-- ----------------------------
-- 故障记录数据（30 条）
-- 状态分布：pending(0)×6, assigned(1)×6, processing(2)×6, resolved(3)×6, closed(4)×6
-- 严重程度：low(1)×10, medium(2)×10, high(3)×10
-- ----------------------------
INSERT INTO `fault_record` (`device_id`, `reporter_id`, `assignee_id`, `title`, `description`, `severity`, `status`, `reported_at`, `assigned_at`, `resolved_at`, `notes`) VALUES
  -- ====== pending（待处理）×6 ======
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2024-0015'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   NULL,
   '实验楼计算机蓝屏重启',
   '上机课期间计算机频繁出现蓝屏，错误代码KERNEL_DATA_INPAGE_ERROR，疑似硬盘故障。',
   2, 0, '2026-04-12 09:15:00', NULL, NULL, NULL),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'AP-2024-0045'),
   (SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   NULL,
   '教学楼A无线网络信号极弱',
   '教学楼A 2楼教室内Wi-Fi信号不稳定，频繁断连，严重影响在线教学。',
   3, 0, '2026-04-13 10:30:00', NULL, NULL, NULL),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'MM-2024-0002'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   NULL,
   '多媒体中控触摸屏无响应',
   '教学楼B-101多媒体中控面板触摸屏点击无反应，物理按键正常。',
   1, 0, '2026-04-13 14:00:00', NULL, NULL, NULL),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'LAB-2024-0002'),
   (SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   NULL,
   '通风柜排风量不足',
   '化学实验室通风柜排风量明显低于正常值，面风速低于0.3m/s，存在安全隐患。',
   2, 0, '2026-04-14 08:30:00', NULL, NULL, NULL),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2024-0020'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhaoliu'),
   NULL,
   '自助打印机扫码支付失败',
   '图书馆自助打印机微信扫码后提示"支付通道异常"，支付宝同样无法使用。',
   1, 0, '2026-04-14 09:45:00', NULL, NULL, NULL),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'WB-2024-0025'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   NULL,
   '电子白板触控漂移严重',
   '教学楼A-301电子白板触控点偏移约2cm，校准后仍无改善，影响正常书写。',
   3, 0, '2026-04-14 11:00:00', NULL, NULL, NULL),

  -- ====== assigned（已指派）×6 ======
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'AP-2023-0031'),
   (SELECT `id` FROM `user` WHERE `username` = 'user1'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '宿舍B区3楼网络断连',
   '宿舍B区3楼全层无法连接无线网络，有线连接正常，疑似AP故障。',
   3, 1, '2026-04-10 14:00:00', '2026-04-10 15:30:00', NULL,
   '已派维护员现场排查'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'CAM-2024-0012'),
   (SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '实验楼走廊监控画面模糊',
   '实验楼2楼走廊监控摄像头画面持续模糊，清洁镜头后未改善，疑似对焦模块故障。',
   2, 1, '2026-04-11 08:20:00', '2026-04-11 10:00:00', NULL,
   '已安排维护员携带备件前往检修'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'LAB-2024-0001'),
   (SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '示波器通道2无信号输出',
   '电子实验室示波器CH2通道接入信号后无波形显示，CH1/3/4正常。',
   1, 1, '2026-04-11 13:45:00', '2026-04-12 09:00:00', NULL,
   '已通知维护员，等待检测'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2024-0023'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '教师机无法连接投影仪',
   '教学楼C-301教师机HDMI输出无信号，更换线缆后问题依旧，疑似显卡接口损坏。',
   3, 1, '2026-04-12 10:00:00', '2026-04-12 14:30:00', NULL,
   '维护员已领取备用显卡，计划下午更换'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'MM-2024-0003'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '视频会议系统音频回声严重',
   '行政楼会议室视频会议时远端反馈严重回声，本地扬声器有明显啸叫。',
   2, 1, '2026-04-12 16:00:00', '2026-04-13 09:00:00', NULL,
   '初步判断为麦克风与扬声器距离过近导致'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PJ-2024-0008'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhaoliu'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '投影仪亮度不足画面偏暗',
   '教学楼C-201投影仪投射画面明显偏暗，调高亮度设置后改善不大，灯泡可能需要更换。',
   1, 1, '2026-04-13 08:00:00', '2026-04-13 10:30:00', NULL,
   '已订购更换灯泡，预计2天内到货'),

  -- ====== processing（处理中）×6 ======
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'ACC-2022-0003'),
   (SELECT `id` FROM `user` WHERE `username` = 'user1'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '实验楼门禁人脸识别失败',
   '刷卡功能正常，人脸识别模块无响应，屏幕显示"模块初始化失败"。',
   2, 2, '2026-04-08 08:00:00', '2026-04-08 09:00:00', NULL,
   '等待原厂工程师寄回配件'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2022-0088'),
   (SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '图书馆查询终端无法开机',
   '按下电源键后无任何反应，电源指示灯不亮，已检查电源线和插座均正常。',
   1, 2, '2026-04-09 10:30:00', '2026-04-09 14:00:00', NULL,
   '初步判断为电源模块损坏，已拆机检测中'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'SW-2024-0010'),
   (SELECT `id` FROM `user` WHERE `username` = 'admin'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '图书馆交换机端口告警',
   '网管系统告警：图书馆汇聚交换机Gi0/0/12端口CRC错误包持续增长，影响下联终端。',
   3, 2, '2026-04-09 16:00:00', '2026-04-10 08:30:00', NULL,
   '已更换跳线，正在观察是否继续报错'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'LAB-2024-0003'),
   (SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '光学平台隔振效果下降',
   '物理实验室光学平台隔振性能明显下降，激光干涉条纹抖动严重，影响精密实验。',
   2, 2, '2026-04-10 09:00:00', '2026-04-10 11:00:00', NULL,
   '正在检查气浮系统气压和密封性'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2023-0035'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '实验楼打印机持续卡纸',
   '打印3-5页后必定卡纸，已清理纸路和搓纸轮，问题依旧。',
   3, 2, '2026-04-10 13:30:00', '2026-04-10 15:00:00', NULL,
   '搓纸轮磨损严重，已订购配件，等待更换'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'MM-2024-0001'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '报告厅音响有杂音',
   '报告厅音响系统播放时有明显电流杂音，音量越大杂音越明显。',
   1, 2, '2026-04-11 09:00:00', '2026-04-11 11:30:00', NULL,
   '正在逐一排查音频线缆和接口'),

  -- ====== resolved（已解决）×6 ======
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'SW-2021-0005'),
   (SELECT `id` FROM `user` WHERE `username` = 'admin'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '核心交换机端口频繁丢包',
   '网络监控系统告警，Gi0/0/5端口丢包率超过15%，影响下联设备通信。',
   3, 3, '2026-04-01 10:00:00', '2026-04-01 10:30:00', '2026-04-02 17:00:00',
   '已更换受损光模块，测试通过，恢复正常'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PJ-2023-0002'),
   (SELECT `id` FROM `user` WHERE `username` = 'user1'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '报告厅投影仪色彩失真',
   '投影画面颜色严重偏绿，调整色彩设置无效，疑似色轮组件老化。',
   2, 3, '2026-04-02 08:30:00', '2026-04-02 10:00:00', '2026-04-03 14:00:00',
   '已重新校准色彩参数并更换色轮，画面恢复正常'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'CAM-2023-0001'),
   (SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '校门口监控夜间画面全黑',
   '夜间19:00后监控画面全黑，红外补光灯未亮起，白天画面正常。',
   1, 3, '2026-04-03 11:00:00', '2026-04-03 14:00:00', '2026-04-04 16:00:00',
   '红外灯板接线松动，重新固定后恢复正常'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'WB-2022-0018'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '电子白板系统卡顿严重',
   '教学楼B-205电子白板操作延迟超过3秒，书写和翻页均卡顿。',
   2, 3, '2026-04-04 09:30:00', '2026-04-04 11:00:00', '2026-04-06 10:00:00',
   '清理系统缓存并升级固件至v3.2.1，运行流畅'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2022-0012'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhaoliu'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '激光打印机打印出白纸',
   '行政办公室打印机打印作业正常接收，但输出全为白纸，碳粉盒已确认有余量。',
   3, 3, '2026-04-05 08:00:00', '2026-04-05 09:30:00', '2026-04-07 15:00:00',
   '转印辊老化导致碳粉无法附着，已更换转印组件'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2023-0001'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '教师机开机后进入修复模式',
   '教学楼A-101教师机开机后反复进入Windows自动修复，无法正常进入桌面。',
   1, 3, '2026-04-06 14:00:00', '2026-04-06 16:00:00', '2026-04-08 11:00:00',
   '系统引导文件损坏，已通过PE修复引导，系统恢复正常'),

  -- ====== closed（已关闭）×6 ======
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2021-0007'),
   (SELECT `id` FROM `user` WHERE `username` = 'user1'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '复印机卡纸且提示墨粉不足',
   '复印时频繁卡纸，同时面板提示青色墨粉即将耗尽，请及时处理。',
   1, 4, '2026-04-01 11:00:00', '2026-04-01 14:00:00', '2026-04-02 10:00:00',
   '已清理卡纸并更换墨粉盒，设备恢复正常，问题关闭'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'AP-2023-0031'),
   (SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '宿舍B区无线网络间歇性断开',
   '宿舍B区3楼无线网络每隔约30分钟断连一次，持续约5分钟后自动恢复。',
   2, 4, '2026-04-01 15:00:00', '2026-04-01 16:30:00', '2026-04-03 09:00:00',
   '固件bug导致DHCP租约异常，升级AP固件后问题消除，已验证关闭'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2022-0088'),
   (SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '图书馆终端触摸屏失灵',
   '图书馆查询终端触摸屏部分区域无法响应点击，影响读者自助查询。',
   3, 4, '2026-04-02 09:00:00', '2026-04-02 10:30:00', '2026-04-04 14:00:00',
   '触摸屏排线接触不良，重新插拔固定后恢复，持续观察一周无复发，关闭'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'ACC-2022-0003'),
   (SELECT `id` FROM `user` WHERE `username` = 'user1'),
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '门禁刷卡开门延迟过长',
   '实验楼门禁刷卡后等待超过5秒才开锁，有时需要多次刷卡。',
   1, 4, '2026-04-03 08:30:00', '2026-04-03 10:00:00', '2026-04-05 11:00:00',
   '控制器缓存溢出导致响应缓慢，清理缓存并优化配置后恢复，关闭'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'SW-2021-0005'),
   (SELECT `id` FROM `user` WHERE `username` = 'admin'),
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '核心交换机CPU占用率过高',
   '网管系统告警核心交换机CPU持续高于85%，部分VLAN间路由出现延迟。',
   2, 4, '2026-04-04 07:30:00', '2026-04-04 08:00:00', '2026-04-05 16:00:00',
   '排查发现ARP广播风暴，定位到异常终端并隔离，优化ACL策略后恢复，关闭'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'CAM-2023-0001'),
   (SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '校门口监控录像存储异常',
   '校门口主监控录像回放发现近3天录像缺失，NVR磁盘状态显示异常。',
   3, 4, '2026-04-05 10:00:00', '2026-04-05 11:30:00', '2026-04-07 09:00:00',
   'NVR硬盘出现坏道，更换硬盘并恢复部分录像，加装监控预警脚本，关闭');

-- ----------------------------
-- 用户档案数据（10 条）
-- ----------------------------
INSERT INTO `user_profile` (`user_id`, `avatar_url`, `department`, `position`, `last_login_time`, `last_login_ip`) VALUES
  ((SELECT `id` FROM `user` WHERE `username` = 'admin'),
   '/avatars/admin.png', '信息技术中心', '中心主任',
   '2026-04-14 08:00:00', '192.168.1.100'),
  ((SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   '/avatars/maintainer.png', '后勤管理处', '设备维护工程师',
   '2026-04-14 07:45:00', '192.168.1.101'),
  ((SELECT `id` FROM `user` WHERE `username` = 'user1'),
   '/avatars/user1.png', '计算机学院', '学生',
   '2026-04-13 09:10:00', '10.20.30.45'),
  ((SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   '/avatars/wangwei.png', '后勤管理处', '设备维护工程师',
   '2026-04-13 08:00:00', '192.168.1.102'),
  ((SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   '/avatars/liuqiang.png', '信息技术中心', '网络维护工程师',
   '2026-04-13 08:05:00', '192.168.1.103'),
  ((SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   '/avatars/zhangsan.png', '计算机学院', '学生',
   '2026-04-12 10:00:00', '10.20.30.50'),
  ((SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   '/avatars/lisi.png', '电子工程学院', '学生',
   '2026-04-14 09:30:00', '10.20.30.51'),
  ((SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   '/avatars/wangwu.png', '图书馆', '图书管理员',
   '2026-04-12 14:30:00', '10.20.30.55'),
  ((SELECT `id` FROM `user` WHERE `username` = 'zhaoliu'),
   '/avatars/zhaoliu.png', '教务处', '行政助理',
   '2026-04-13 09:00:00', '10.20.30.60'),
  ((SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   '/avatars/sunqi.png', '化学学院', '学生',
   '2026-04-12 10:20:00', '10.20.30.65');

-- ----------------------------
-- 登录记录数据（15 条）
-- ----------------------------
INSERT INTO `login_record` (`user_id`, `username`, `login_time`, `login_ip`, `login_device`, `status`, `message`) VALUES
  ((SELECT `id` FROM `user` WHERE `username` = 'admin'),
   'admin', '2026-04-01 08:00:00', '192.168.1.100',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   'maintainer', '2026-04-01 08:30:00', '192.168.1.101',
   'Chrome 120.0 / Windows 10', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'admin'),
   'admin', '2026-04-02 07:55:00', '192.168.1.100',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'user1'),
   'user1', '2026-04-03 09:10:00', '10.20.30.45',
   'Safari 17.4 / macOS Sonoma', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   'zhangsan', '2026-04-03 10:00:00', '10.20.30.50',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   'lisi', '2026-04-04 08:15:00', '10.20.30.51',
   'Firefox 124.0 / Ubuntu 22.04', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   'wangwei', '2026-04-05 08:00:00', '192.168.1.102',
   'Chrome 120.0 / Windows 10', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   'liuqiang', '2026-04-05 08:05:00', '192.168.1.103',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'wangwu'),
   'wangwu', '2026-04-06 14:30:00', '10.20.30.55',
   '微信内置浏览器 / Android 14', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'zhaoliu'),
   'zhaoliu', '2026-04-07 09:00:00', '10.20.30.60',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'sunqi'),
   'sunqi', '2026-04-08 10:20:00', '10.20.30.65',
   'Safari 17.4 / iOS 17', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'admin'),
   'admin', '2026-04-10 08:00:00', '192.168.1.100',
   'Chrome 120.0 / Windows 11', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'zhangsan'),
   'zhangsan', '2026-04-10 20:30:00', '10.20.30.50',
   'Chrome 120.0 / Android 14', 0, '密码错误'),
  ((SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   'maintainer', '2026-04-12 07:45:00', '192.168.1.101',
   'Chrome 120.0 / Windows 10', 1, '登录成功'),
  ((SELECT `id` FROM `user` WHERE `username` = 'lisi'),
   'lisi', '2026-04-14 09:30:00', '10.20.30.51',
   'Firefox 124.0 / Ubuntu 22.04', 1, '登录成功');

-- ----------------------------
-- 维护历史数据（10 条）
-- 类型：routine=例行保养, repair=故障维修, upgrade=升级改造
-- ----------------------------
INSERT INTO `maintenance_history` (`device_id`, `maintenance_type`, `description`, `maintainer_id`, `cost`, `start_time`, `end_time`, `status`, `notes`) VALUES
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'SW-2021-0005'),
   'routine', '核心交换机季度例行巡检与保养',
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   500.00, '2026-04-01 09:00:00', '2026-04-01 12:00:00', 2,
   '清洁设备、检查端口状态、备份配置、更新固件补丁'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2023-0001'),
   'repair', '教师机系统引导修复',
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   200.00, '2026-04-02 14:00:00', '2026-04-03 11:00:00', 2,
   '修复Windows引导文件，检查硬盘健康状态，清理系统垃圾'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2021-0007'),
   'routine', '复印机定期清洁保养',
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   150.00, '2026-04-03 14:00:00', '2026-04-03 16:00:00', 2,
   '清理纸路、搓纸轮、充电辊，检查各部件磨损情况'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'CAM-2023-0001'),
   'upgrade', '校门监控系统升级改造',
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   3000.00, '2026-04-04 08:00:00', '2026-04-06 17:00:00', 2,
   '升级至400万像素摄像头，增加AI人形检测功能，更新NVR固件'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'WB-2022-0018'),
   'repair', '电子白板固件升级与性能优化',
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   800.00, '2026-04-05 09:00:00', '2026-04-07 15:00:00', 2,
   '升级系统固件至v3.2.1，扩展内存，优化触控响应速度'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PJ-2023-0002'),
   'routine', '报告厅投影仪定期保养',
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   100.00, '2026-04-08 14:00:00', '2026-04-08 16:00:00', 2,
   '清洁防尘滤网、校准色彩、检查灯泡使用时长'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'LAB-2024-0001'),
   'upgrade', '示波器固件与探头升级',
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   5000.00, '2026-04-09 09:00:00', '2026-04-11 17:00:00', 1,
   '升级示波器固件，更换高精度无源探头，校准各通道'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'MM-2024-0001'),
   'repair', '报告厅音响系统杂音排查维修',
   (SELECT `id` FROM `user` WHERE `username` = 'wangwei'),
   1200.00, '2026-04-10 10:00:00', NULL, 1,
   '逐一排查音频线缆，更换疑似老化的XLR接头'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'AP-2023-0031'),
   'routine', '宿舍区无线AP批量巡检',
   (SELECT `id` FROM `user` WHERE `username` = 'liuqiang'),
   300.00, '2026-04-13 09:00:00', NULL, 0,
   '计划对宿舍B区所有AP进行信号覆盖测试和固件统一升级'),

  ((SELECT `id` FROM `device` WHERE `serial_number` = 'LAB-2024-0003'),
   'upgrade', '光学平台气浮系统升级',
   (SELECT `id` FROM `user` WHERE `username` = 'maintainer'),
   8000.00, '2026-04-14 08:00:00', NULL, 0,
   '计划更换气浮隔振器，升级气压控制系统，提升隔振性能');
