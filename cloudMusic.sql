-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: wyy
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '专辑唯一标识（自增主键）',
  `title` varchar(255) NOT NULL COMMENT '专辑名称（必填）',
  `author_id` int NOT NULL,
  `cover_url` varchar(255) DEFAULT NULL COMMENT '专辑封面URL（可选，存储图片文件路径）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动填充）',
  `is_public` tinyint DEFAULT (0),
  PRIMARY KEY (`id`),
  KEY `author_id` (`author_id`),
  CONSTRAINT `album_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储专辑信息，仅音乐人可创建';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (10,'周杰伦',12,NULL,'2025-05-01 00:59:38',0),(11,'颜人中',12,NULL,'2025-05-01 18:17:04',0),(13,'sss',12,NULL,'2025-05-01 18:33:53',0),(15,'aaa',12,NULL,'2025-05-01 21:17:44',1);
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assist_record`
--

DROP TABLE IF EXISTS `assist_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assist_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coupon_id` int NOT NULL,
  `friend_id` int NOT NULL,
  `assist_value` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `assist_record_ibfk_1` (`coupon_id`),
  KEY `assist_record_ibfk_2` (`friend_id`),
  CONSTRAINT `assist_record_ibfk_1` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assist_record_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assist_record`
--

LOCK TABLES `assist_record` WRITE;
/*!40000 ALTER TABLE `assist_record` DISABLE KEYS */;
INSERT INTO `assist_record` VALUES (1,1,13,'ASSIST_TOKEN_1746456923504','2025-05-05 23:06:26'),(2,1,12,'ASSIST_TOKEN_1746456923504','2025-05-05 23:06:26'),(3,7,13,'ASSIST_TOKEN_1746456923504','2025-05-05 23:35:57'),(4,7,12,'ASSIST_TOKEN_1746456923504','2025-05-05 23:35:57');
/*!40000 ALTER TABLE `assist_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `coupon_code` varchar(50) NOT NULL,
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '10.00',
  `is_used` tinyint(1) DEFAULT '0',
  `expiration_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,12,'6OEWJ1YKZI',10.00,0,'2025-05-12 23:06:27'),(7,12,'2YBF7EOQ7H',10.00,0,'2025-05-12 23:35:57');
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关注记录唯一标识（自增主键）',
  `follower_id` int NOT NULL,
  `followee_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间（自动填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `follower_id` (`follower_id`,`followee_id`),
  KEY `followee_id` (`followee_id`),
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`followee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='记录用户关注关系（粉丝-被关注者）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES (4,12,13,'2025-04-29 20:24:40'),(14,12,1,'2025-05-03 16:46:52');
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `like_song`
--

DROP TABLE IF EXISTS `like_song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like_song` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '红心记录唯一标识（自增主键）',
  `user_id` int NOT NULL,
  `song_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间（自动填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`song_id`),
  KEY `like_song_ibfk_2` (`song_id`),
  CONSTRAINT `like_song_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `like_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='记录用户收藏的歌曲（多对多关系中间表）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_song`
--

LOCK TABLES `like_song` WRITE;
/*!40000 ALTER TABLE `like_song` DISABLE KEYS */;
/*!40000 ALTER TABLE `like_song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL DEFAULT '0',
  `receiver_id` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (7,21,13,'审核通知','歌曲《兰亭序》审核中',1,'2025-05-03 11:28:54'),(9,21,13,'审核结果通知','歌曲《兰亭序》通过审核',1,'2025-05-03 11:28:55'),(11,21,12,'审核结果通知','歌曲晴未通过审核，拒绝原因：null',0,'2025-05-04 23:41:30'),(12,21,12,'审核通知','歌曲《q》审核中',0,'2025-05-04 23:59:16'),(13,21,12,'审核结果通知','歌曲q未通过审核，拒绝原因：null',0,'2025-05-05 00:02:36'),(14,21,12,'审核通知','歌曲《天》审核中',0,'2025-05-05 00:03:14'),(15,21,12,'审核结果通知','歌曲天未通过审核，拒绝原因：null',0,'2025-05-05 00:03:28'),(16,21,12,'审核通知','歌曲《晴天》审核中',0,'2025-05-05 11:11:33'),(17,21,12,'审核结果通知','歌曲《晴天》通过审核',0,'2025-05-05 11:11:34'),(18,21,12,'审核通知','歌曲《晴》审核中',0,'2025-05-05 11:11:35'),(19,21,12,'审核结果通知','歌曲《晴》通过审核',1,'2025-05-05 11:11:36'),(20,21,12,'审核通知','歌曲《唯一》审核中',1,'2025-05-05 11:11:39'),(21,21,12,'审核结果通知','歌曲《唯一》通过审核',1,'2025-05-05 11:11:40'),(22,21,12,'审核通知','歌曲《有些》审核中',1,'2025-05-05 11:11:41'),(23,21,12,'审核结果通知','歌曲《有些》通过审核',1,'2025-05-05 11:11:42'),(24,21,12,'审核通知','歌曲《yx》审核中',1,'2025-05-05 11:11:44'),(25,21,12,'审核结果通知','歌曲yx未通过审核，拒绝原因：未有版权',1,'2025-05-05 11:12:02'),(26,12,13,'好友助力通知','用户 hh 邀请您助力！\n请点击下方链接完成助力，您将帮助他获得一张10元无门槛VIP优惠券。\n🔗 助力链接: <a href=\'/XuJunxi_CloudMusic/coupon/completeAssist?inviterId=12&friendId=13&token=ASSIST_TOKEN_1746456923504\'>/XuJunxi_CloudMusic/coupon/completeAssist?inviterId=12&friendId=13&token=ASSIST_TOKEN_1746456923504</a>',1,'2025-05-05 22:55:23'),(28,13,12,'优惠券通知','您已获得一张优惠券，请前往我的优惠券查看。',0,'2025-05-05 23:35:57');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '歌单唯一标识（自增主键）',
  `user_id` int NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '歌单名称（必填）',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '歌单封面URL（可选，存储图片文件路径）',
  `is_public` tinyint(1) DEFAULT '1' COMMENT '是否公开（1=公开，0=私密，默认公开）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动填充）',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储用户创建的歌单（支持公开/私密）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (1,12,'周杰伦','/XuJunxi_CloudMusic/img/1746174300550_1316.png',1,'2025-05-02 16:25:00');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_song`
--

DROP TABLE IF EXISTS `playlist_song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_song` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识（自增主键）',
  `playlist_id` int NOT NULL,
  `song_id` int NOT NULL,
  `added_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间（自动填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `playlist_id` (`playlist_id`,`song_id`),
  UNIQUE KEY `playlist_id_2` (`playlist_id`,`song_id`),
  KEY `playlist_song_ibfk_2` (`song_id`),
  CONSTRAINT `playlist_song_ibfk_1` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `playlist_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='维护歌单与歌曲的多对多关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_song`
--

LOCK TABLES `playlist_song` WRITE;
/*!40000 ALTER TABLE `playlist_song` DISABLE KEYS */;
INSERT INTO `playlist_song` VALUES (5,1,42,'2025-05-05 11:19:14');
/*!40000 ALTER TABLE `playlist_song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `uploadwork_id` int NOT NULL,
  `content` varchar(255) NOT NULL,
  `status` int NOT NULL DEFAULT '0',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `result` tinyint NOT NULL DEFAULT '0',
  `version` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (10,12,19,'歌曲',1,'2025-04-27 20:40:11',0,1),(13,13,22,'歌曲',1,'2025-04-27 22:24:34',0,1),(14,13,23,'歌曲',1,'2025-04-27 22:25:00',0,1),(15,12,24,'歌曲',1,'2025-04-27 22:33:51',0,1),(16,12,25,'歌曲',1,'2025-04-27 22:36:49',0,1),(17,13,26,'歌曲',1,'2025-04-28 00:34:35',1,1),(18,13,27,'歌曲',1,'2025-04-28 00:34:50',1,1),(19,12,28,'歌曲',1,'2025-04-28 18:27:06',1,1),(20,12,29,'歌曲',1,'2025-04-29 00:39:01',1,1),(23,12,31,'歌曲',1,'2025-05-01 18:33:39',1,1),(24,12,13,'专辑',1,'2025-05-01 18:33:53',0,1),(25,12,32,'歌曲',1,'2025-05-01 20:34:30',0,2),(26,12,14,'专辑',1,'2025-05-01 20:34:47',1,1),(27,12,33,'歌曲',1,'2025-05-01 21:16:58',1,1),(28,12,15,'专辑',1,'2025-05-01 21:17:44',1,1),(31,12,36,'歌曲',1,'2025-05-02 00:26:30',1,1),(32,12,37,'歌曲',1,'2025-05-02 22:38:27',1,1),(33,13,38,'歌曲',1,'2025-05-03 11:28:49',1,1),(34,12,39,'歌曲',1,'2025-05-04 23:31:51',0,1),(35,12,40,'歌曲',1,'2025-05-04 23:58:23',0,1),(36,12,41,'歌曲',1,'2025-05-04 23:58:52',0,1),(37,12,42,'歌曲',1,'2025-05-05 10:56:14',1,1),(38,12,43,'歌曲',1,'2025-05-05 11:09:03',1,1),(39,12,44,'歌曲',1,'2025-05-05 11:10:17',1,1),(40,12,45,'歌曲',1,'2025-05-05 11:10:52',1,1),(41,12,46,'歌曲',1,'2025-05-05 11:11:09',0,1);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song`
--

DROP TABLE IF EXISTS `song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '歌曲唯一标识（自增主键）',
  `title` varchar(255) NOT NULL COMMENT '歌曲名称（必填）',
  `author_id` int NOT NULL,
  `album_id` int DEFAULT '0',
  `like_count` int DEFAULT '0' COMMENT '红心数（收藏次数，默认0，需原子更新）',
  `play_count` int DEFAULT '0' COMMENT '播放量（默认0，记录播放次数）',
  `audio_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音频文件URL（必填，如/music/123.mp3）',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间（自动填充）',
  `genre` varchar(255) NOT NULL,
  `is_public` tinyint(1) NOT NULL DEFAULT '0',
  `is_vip_only` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `author_id` (`author_id`),
  KEY `album_id` (`album_id`),
  CONSTRAINT `song_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储歌曲基本信息及统计数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song`
--

LOCK TABLES `song` WRITE;
/*!40000 ALTER TABLE `song` DISABLE KEYS */;
INSERT INTO `song` VALUES (42,'晴天',12,0,0,1,'/XuJunxi_CloudMusic/audio/1746413774284_9539.mp3','2025-05-05 10:56:14','流行歌曲',1,0),(43,'晴',12,0,0,0,'/XuJunxi_CloudMusic/audio/1746414543590_9678.mp3','2025-05-05 11:09:03','流行歌曲',1,0),(44,'唯一',12,0,0,1,'/XuJunxi_CloudMusic/audio/1746414617717_5415.mp3','2025-05-05 11:10:17','a',1,0),(45,'有些',12,0,0,0,'/XuJunxi_CloudMusic/audio/1746414652565_1310.mp3','2025-05-05 11:10:52','b',1,0),(46,'yx',12,0,0,0,'/XuJunxi_CloudMusic/audio/1746414669694_4976.mp3','2025-05-05 11:11:09','b',0,0);
/*!40000 ALTER TABLE `song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识（自增主键）',
  `username` varchar(50) NOT NULL COMMENT '用户名（唯一，用于登录，长度≤50字符）',
  `password` varchar(255) NOT NULL COMMENT '加密密码（SHA-256哈希值，长度256字符）',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL（可选，存储图片文件路径或第三方链接）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（可选，用于绑定账号）',
  `email` varchar(100) NOT NULL COMMENT '邮箱（唯一，用于注册/找回密码，长度≤100字符）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间（自动填充）',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` int DEFAULT '0',
  `wallet_balance` decimal(10,2) DEFAULT '0.00',
  `vip_expiry` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表，区分普通用户和音乐人';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'xjx','152bd5862d3ac36bcdb3ae6d8e9a8432','暂未设置','暂无','3461727951@qq.com','2025-04-22 22:09:25','30561a60-bf23-4725-afcb-d02c8e2ab3da',1,10.00,'2025-05-11 18:37:43'),(12,'hh','21d4a72ef20bc54945ff81d25d27fccd','/XuJunxi_CloudMusic/img/1746414865707_9944.png','15014274757','1234@qq.com','2025-04-23 23:14:34','440e01e0-bcb5-4c32-80a7-19742af9930a',1,90.00,'2025-05-25 18:47:18'),(13,'小明','31265d5069dfb7c82f2c8da91ac9c4e6',NULL,'暂无','1111@qq.com','2025-04-24 19:54:47','03d5c993-a4bb-4137-b022-c11f99c5daaf',1,0.00,'2025-05-13 00:12:07'),(21,'ad','074f8e9da95d3c2a44658ad9f3067f1c',NULL,NULL,'4567@qq.com','2025-04-25 17:06:15','12cf6dc8-cf91-460c-a489-a49d40d4197f',2,0.00,NULL),(22,'瓦学弟','a9244e5a785b7802656ca961eea1ab2a',NULL,NULL,'123456@qq.com','2025-04-30 09:30:18','40a42a06-269b-4bfd-a805-fed54a6d3bbe',0,0.00,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vip_plan`
--

DROP TABLE IF EXISTS `vip_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vip_plan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `duration_days` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vip_plan`
--

LOCK TABLES `vip_plan` WRITE;
/*!40000 ALTER TABLE `vip_plan` DISABLE KEYS */;
INSERT INTO `vip_plan` VALUES (1,'周卡',7,10.00),(2,'月卡',30,25.00),(3,'季卡',90,60.00),(4,'年卡',365,200.00);
/*!40000 ALTER TABLE `vip_plan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-06  0:16:34
