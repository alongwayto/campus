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
-- 用户数据
-- 密码均使用 BCrypt 加密（强度 10）：
--   admin      → admin123
--   maintainer → maint123
--   user1      → user123
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
   (SELECT `id` FROM `role` WHERE `name` = 'ROLE_USER'), 1);

-- ----------------------------
-- 设备类型数据
-- ----------------------------
INSERT INTO `device_type` (`name`, `description`) VALUES
  ('计算机设备', '台式机、笔记本电脑、服务器等计算设备'),
  ('网络设备',   '路由器、交换机、无线接入点等网络基础设施'),
  ('打印设备',   '激光打印机、喷墨打印机、复印机等输出设备'),
  ('安防设备',   '监控摄像头、门禁系统、报警器等安全防护设备'),
  ('教学设备',   '投影仪、电子白板、智能讲台等教学辅助设备');

-- ----------------------------
-- 设备数据（每类 2 台，共 10 台）
-- ----------------------------
INSERT INTO `device` (`name`, `device_type_id`, `location`, `status`, `serial_number`, `manufacturer`, `purchase_date`, `warranty_expiry`, `description`) VALUES
  -- 计算机设备
  ('教学楼A-101 教师机',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '教学楼A 101室', 1, 'PC-2023-0001', '联想', '2023-03-01', '2026-03-01',
   '教室主讲台教师用计算机'),
  ('图书馆查询终端-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '计算机设备'),
   '图书馆一楼大厅', 2, 'PC-2022-0088', '戴尔', '2022-06-15', '2025-06-15',
   '图书检索自助查询终端，当前存在故障'),

  -- 网络设备
  ('行政楼核心交换机',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '行政楼网络机房', 1, 'SW-2021-0005', '华为', '2021-09-10', '2024-09-10',
   '校园网核心层交换机，承载全校流量'),
  ('宿舍楼B区无线AP-03',
   (SELECT `id` FROM `device_type` WHERE `name` = '网络设备'),
   '宿舍楼B 3楼走廊', 0, 'AP-2023-0031', '锐捷', '2023-01-20', '2026-01-20',
   '宿舍楼无线覆盖接入点，当前离线'),

  -- 打印设备
  ('行政办公室激光打印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '行政楼 201室', 1, 'PR-2022-0012', 'HP', '2022-11-05', '2025-11-05',
   'A4黑白激光打印机'),
  ('教务处复印机',
   (SELECT `id` FROM `device_type` WHERE `name` = '打印设备'),
   '行政楼 103室', 1, 'PR-2021-0007', '佳能', '2021-07-18', '2024-07-18',
   'A3彩色复合机，支持打印/复印/扫描'),

  -- 安防设备
  ('校门口监控摄像头-主',
   (SELECT `id` FROM `device_type` WHERE `name` = '安防设备'),
   '正门岗亭', 1, 'CAM-2023-0001', '海康威视', '2023-05-01', '2026-05-01',
   '200万像素高清球机，支持夜视'),
  ('实验楼门禁系统-01',
   (SELECT `id` FROM `device_type` WHERE `name` = '安防设备'),
   '实验楼入口', 2, 'ACC-2022-0003', '大华', '2022-08-22', '2025-08-22',
   '刷卡+人脸识别双模门禁，当前人脸模块故障'),

  -- 教学设备
  ('报告厅投影仪',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '报告厅', 1, 'PJ-2023-0002', '爱普生', '2023-02-28', '2026-02-28',
   '5000流明激光投影仪'),
  ('教学楼B-205 电子白板',
   (SELECT `id` FROM `device_type` WHERE `name` = '教学设备'),
   '教学楼B 205室', 1, 'WB-2022-0018', '鸿合', '2022-10-10', '2025-10-10',
   '86英寸交互式电子白板');

-- ----------------------------
-- 故障记录数据
-- ----------------------------
INSERT INTO `fault_record` (`device_id`, `reporter_id`, `assignee_id`, `title`, `description`, `severity`, `status`, `reported_at`, `assigned_at`, `resolved_at`, `notes`) VALUES
  -- 故障1：图书馆查询终端（status=pending）
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PC-2022-0088'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'user1'),
   NULL,
   '图书馆查询终端无法开机',
   '按下电源键后无任何反应，电源指示灯不亮，已尝试更换插座无效。',
   2, 0, '2024-05-10 09:30:00', NULL, NULL, NULL),

  -- 故障2：宿舍楼无线AP（status=assigned）
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'AP-2023-0031'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'user1'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'maintainer'),
   '宿舍B区3楼网络断连',
   '宿舍B区3楼全层无法连接无线网络，有线连接正常，疑似AP故障。',
   3, 1, '2024-05-11 14:00:00', '2024-05-11 15:30:00', NULL, '已派维护员现场排查'),

  -- 故障3：实验楼门禁（status=processing）
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'ACC-2022-0003'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'user1'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'maintainer'),
   '实验楼门禁人脸识别失败',
   '刷卡功能正常，人脸识别模块无响应，屏幕显示"模块初始化失败"。',
   2, 2, '2024-05-08 08:00:00', '2024-05-08 09:00:00', NULL, '等待原厂工程师寄回配件'),

  -- 故障4：行政楼核心交换机（status=resolved）
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'SW-2021-0005'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'admin'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'maintainer'),
   '核心交换机端口频繁丢包',
   '网络监控系统告警，Gi0/0/5 端口丢包率超过 15%，影响下联设备通信。',
   3, 3, '2024-05-01 10:00:00', '2024-05-01 10:30:00', '2024-05-02 17:00:00',
   '已更换受损光模块，测试通过，恢复正常'),

  -- 故障5：教务处复印机（status=closed）
  ((SELECT `id` FROM `device` WHERE `serial_number` = 'PR-2021-0007'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'user1'),
   (SELECT `id` FROM `user`   WHERE `username`      = 'maintainer'),
   '复印机卡纸且提示墨粉不足',
   '复印时频繁卡纸，同时面板提示青色墨粉即将耗尽，请及时处理。',
   1, 4, '2024-04-25 11:00:00', '2024-04-25 14:00:00', '2024-04-26 10:00:00',
   '已清理卡纸并更换墨粉盒，设备恢复正常，问题关闭');

-- ============================================================
-- AI 和扩展功能表
-- ============================================================

-- ----------------------------
-- Table: device_metrics (设备实时指标)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device_metrics` (
  `id`                bigint       NOT NULL AUTO_INCREMENT,
  `device_id`         bigint       NOT NULL,
  `cpu_usage`         decimal(5,2) DEFAULT NULL COMMENT 'CPU使用率(%)',
  `memory_usage`      decimal(5,2) DEFAULT NULL COMMENT '内存使用率(%)',
  `temperature`       decimal(5,2) DEFAULT NULL COMMENT '温度(°C)',
  `power_consumption` decimal(8,2) DEFAULT NULL COMMENT '功耗(W)',
  `error_count`       int          DEFAULT 0 COMMENT '错误次数',
  `network_status`    varchar(20)  DEFAULT NULL COMMENT 'online/offline/unstable',
  `recorded_at`       datetime     DEFAULT CURRENT_TIMESTAMP,
  `created_at`        datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id`   (`device_id`),
  KEY `idx_recorded_at` (`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备实时指标表';

-- ----------------------------
-- Table: ai_model_data (AI训练数据)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ai_model_data` (
  `id`           bigint       NOT NULL AUTO_INCREMENT,
  `device_id`    bigint       DEFAULT NULL,
  `fault_id`     bigint       DEFAULT NULL,
  `feature_data` json         DEFAULT NULL COMMENT '特征数据(JSON)',
  `label`        varchar(100) DEFAULT NULL COMMENT '标签/分类',
  `data_type`    varchar(50)  DEFAULT NULL COMMENT 'train/test/validate',
  `created_at`   datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_data_type` (`data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI训练数据表';

-- ----------------------------
-- Table: ai_predictions (AI预测结果)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ai_predictions` (
  `id`              bigint        NOT NULL AUTO_INCREMENT,
  `device_id`       bigint        NOT NULL,
  `prediction_type` varchar(50)   NOT NULL COMMENT 'failure/anomaly/maintenance',
  `probability`     decimal(5,4)  DEFAULT NULL COMMENT '概率(0-1)',
  `severity`        varchar(20)   DEFAULT NULL COMMENT '风险等级',
  `description`     text          DEFAULT NULL COMMENT '预测描述',
  `recommendation`  text          DEFAULT NULL COMMENT '建议措施',
  `model_version`   varchar(50)   DEFAULT NULL,
  `predicted_at`    datetime      DEFAULT CURRENT_TIMESTAMP,
  `created_at`      datetime      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_device_id`   (`device_id`),
  KEY `idx_predicted_at` (`predicted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI预测结果表';

-- ----------------------------
-- Table: alert_config (告警配置)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alert_config` (
  `id`           bigint        NOT NULL AUTO_INCREMENT,
  `name`         varchar(100)  NOT NULL COMMENT '告警名称',
  `device_type`  varchar(100)  DEFAULT NULL COMMENT '适用设备类型',
  `metric`       varchar(100)  NOT NULL COMMENT '监控指标',
  `threshold`    decimal(10,2) NOT NULL COMMENT '告警阈值',
  `operator`     varchar(10)   NOT NULL COMMENT '>/</>=/<=',
  `severity`     varchar(20)   NOT NULL COMMENT 'low/medium/high/critical',
  `enabled`      tinyint(1)    DEFAULT 1,
  `created_at`   datetime      DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警配置表';

-- ----------------------------
-- Table: system_config (系统配置)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `system_config` (
  `id`           bigint        NOT NULL AUTO_INCREMENT,
  `config_key`   varchar(100)  NOT NULL UNIQUE,
  `config_value` varchar(1000) NOT NULL,
  `description`  varchar(500)  DEFAULT NULL,
  `created_at`   datetime      DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- Table: maintenance_record (维护记录)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `maintenance_record` (
  `id`               bigint        NOT NULL AUTO_INCREMENT,
  `device_id`        bigint        NOT NULL,
  `maintainer_id`    bigint        DEFAULT NULL,
  `maintenance_type` varchar(50)   NOT NULL COMMENT 'routine/repair/upgrade/inspection',
  `title`            varchar(200)  NOT NULL,
  `description`      text          DEFAULT NULL,
  `parts_replaced`   varchar(500)  DEFAULT NULL COMMENT '更换零件',
  `cost`             decimal(10,2) DEFAULT NULL COMMENT '维护费用',
  `duration_hours`   decimal(5,2)  DEFAULT NULL COMMENT '工时(小时)',
  `maintained_at`    datetime      DEFAULT CURRENT_TIMESTAMP,
  `next_maintenance` date          DEFAULT NULL COMMENT '下次维护日期',
  `created_at`       datetime      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_mr_device`     (`device_id`),
  KEY `fk_mr_maintainer` (`maintainer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维护记录表';

-- ----------------------------
-- Table: device_attachment (设备附件)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `device_attachment` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `device_id`   bigint       NOT NULL,
  `file_name`   varchar(255) NOT NULL,
  `file_path`   varchar(500) NOT NULL,
  `file_type`   varchar(50)  DEFAULT NULL COMMENT 'manual/photo/certificate/other',
  `file_size`   bigint       DEFAULT NULL COMMENT '文件大小(bytes)',
  `uploader_id` bigint       DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_da_device` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备附件表';

-- ----------------------------
-- Table: fault_attachment (故障附件)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `fault_attachment` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `fault_id`    bigint       NOT NULL,
  `file_name`   varchar(255) NOT NULL,
  `file_path`   varchar(500) NOT NULL,
  `file_type`   varchar(50)  DEFAULT NULL,
  `file_size`   bigint       DEFAULT NULL,
  `uploader_id` bigint       DEFAULT NULL,
  `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_fa_fault` (`fault_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障附件表';

-- ============================================================
-- 初始系统配置数据
-- ============================================================
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
  ('system.name',            '智能校园设备管理系统',         '系统名称'),
  ('system.version',         '1.0.0',                       '系统版本'),
  ('ai.prediction.enabled',  'true',                        'AI预测功能开关'),
  ('ai.threshold.high',      '0.7',                         'AI高风险阈值'),
  ('alert.email.enabled',    'false',                       '邮件告警开关'),
  ('maintenance.interval',   '90',                          '默认维护间隔天数');

-- 告警配置数据
INSERT INTO `alert_config` (`name`, `metric`, `threshold`, `operator`, `severity`) VALUES
  ('CPU使用率过高', 'cpu_usage', 90.00, '>', 'high'),
  ('内存使用率过高', 'memory_usage', 95.00, '>', 'high'),
  ('设备温度过高', 'temperature', 85.00, '>', 'critical'),
  ('错误次数过多', 'error_count', 10.00, '>', 'medium');
