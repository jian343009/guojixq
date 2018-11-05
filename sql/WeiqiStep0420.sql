-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: miracle_weiqi
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `WeiqiStep`
--

DROP TABLE IF EXISTS `WeiqiStep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WeiqiStep` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device` int(11) DEFAULT NULL,
  `lesson` int(11) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=151 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WeiqiStep`
--

LOCK TABLES `WeiqiStep` WRITE;
/*!40000 ALTER TABLE `WeiqiStep` DISABLE KEYS */;
INSERT INTO `WeiqiStep` VALUES (1,3607,2,2,'2014-04-17 15:36:28'),(2,3607,2,1,'2014-04-17 15:36:28'),(3,3607,2,13,'2014-04-17 15:36:32'),(4,3607,2,14,'2014-04-17 15:36:32'),(5,3607,2,5,'2014-04-17 15:36:36'),(6,3607,2,2,'2014-04-17 15:37:00'),(7,3604,2,15,'2014-04-17 15:44:12'),(8,3604,2,7,'2014-04-17 15:44:12'),(9,3604,2,10,'2014-04-17 15:44:15'),(10,3604,2,3,'2014-04-17 15:47:35'),(11,3604,2,13,'2014-04-17 15:47:41'),(12,3604,2,14,'2014-04-17 15:47:41'),(13,3604,2,3,'2014-04-17 15:47:48'),(14,3607,2,2,'2014-04-17 15:54:46'),(15,3607,2,21,'2014-04-17 15:56:39'),(16,3607,2,3,'2014-04-17 16:01:47'),(17,3607,2,14,'2014-04-17 16:01:59'),(18,3607,2,13,'2014-04-17 16:01:59'),(19,3607,2,2,'2014-04-17 16:02:05'),(20,3607,2,1,'2014-04-17 16:02:15'),(21,3607,2,6,'2014-04-17 16:02:20'),(22,3607,2,2,'2014-04-17 16:02:29'),(23,3607,2,1,'2014-04-17 16:02:55'),(24,3607,2,5,'2014-04-17 16:02:58'),(25,3604,2,19,'2014-04-17 16:09:09'),(26,3604,2,13,'2014-04-17 16:09:55'),(27,3604,2,14,'2014-04-17 16:09:55'),(28,3604,2,7,'2014-04-17 16:10:05'),(29,3604,2,5,'2014-04-17 16:10:05'),(30,3604,2,10,'2014-04-17 16:10:07'),(31,3604,2,2,'2014-04-17 16:28:45'),(32,3604,2,1,'2014-04-17 16:28:45'),(33,3604,2,3,'2014-04-17 16:29:01'),(34,3604,2,7,'2014-04-17 16:29:09'),(35,3604,2,15,'2014-04-17 16:29:09'),(36,3604,2,10,'2014-04-17 16:29:10'),(37,3604,2,21,'2014-04-17 16:29:19'),(38,3604,2,24,'2014-04-17 16:29:21'),(39,3604,2,21,'2014-04-17 16:29:22'),(40,3604,2,24,'2014-04-17 16:29:23'),(41,3604,2,21,'2014-04-17 16:30:09'),(42,3604,2,24,'2014-04-17 16:30:10'),(43,3604,2,15,'2014-04-17 16:30:37'),(44,3604,2,7,'2014-04-17 16:30:37'),(45,3604,2,10,'2014-04-17 16:30:39'),(46,3604,2,21,'2014-04-17 16:31:18'),(47,3604,2,24,'2014-04-17 16:31:19'),(48,3604,2,2,'2014-04-17 16:32:32'),(49,3604,2,1,'2014-04-17 16:32:33'),(50,3604,2,13,'2014-04-17 16:32:36'),(51,3604,2,14,'2014-04-17 16:32:36'),(52,3604,2,5,'2014-04-17 16:32:37'),(53,3604,2,7,'2014-04-17 16:32:37'),(54,3604,2,10,'2014-04-17 16:32:40'),(55,3604,2,6,'2014-04-17 16:32:43'),(56,3604,2,2,'2014-04-17 16:32:48'),(57,3604,2,16,'2014-04-17 16:32:48'),(58,3604,2,1,'2014-04-17 16:32:48'),(59,3604,2,5,'2014-04-17 16:32:53'),(60,3604,2,7,'2014-04-17 16:32:53'),(61,3604,2,10,'2014-04-17 16:32:56'),(62,3604,2,6,'2014-04-17 16:33:03'),(63,3604,2,16,'2014-04-17 16:33:06'),(64,3604,2,1,'2014-04-17 16:33:06'),(65,3604,2,2,'2014-04-17 16:33:06'),(66,3604,2,6,'2014-04-17 16:33:14'),(67,3604,2,2,'2014-04-17 16:33:41'),(68,3604,2,16,'2014-04-17 16:33:41'),(69,3604,2,1,'2014-04-17 16:33:41'),(70,3604,2,13,'2014-04-17 16:52:01'),(71,3604,2,14,'2014-04-17 16:52:01'),(72,3604,2,24,'2014-04-17 16:52:06'),(73,3604,2,21,'2014-04-17 16:52:08'),(74,3604,2,10,'2014-04-17 16:52:10'),(75,3604,2,22,'2014-04-17 16:52:12'),(76,3604,2,10,'2014-04-17 16:52:14'),(77,3604,2,23,'2014-04-17 16:52:15'),(78,3604,2,10,'2014-04-17 16:52:15'),(79,3604,2,7,'2014-04-17 16:52:21'),(80,3604,2,15,'2014-04-17 16:52:21'),(81,3604,2,10,'2014-04-17 16:52:22'),(82,3604,2,13,'2014-04-17 16:52:53'),(83,3604,2,14,'2014-04-17 16:52:53'),(84,3604,2,1,'2014-04-17 16:53:14'),(85,3604,2,2,'2014-04-17 16:53:14'),(86,3604,2,24,'2014-04-17 16:53:21'),(87,3604,2,13,'2014-04-17 16:53:30'),(88,3604,2,14,'2014-04-17 16:53:30'),(89,3604,2,24,'2014-04-17 16:53:32'),(90,3604,2,3,'2014-04-17 16:53:42'),(91,3604,2,14,'2014-04-17 16:53:44'),(92,3604,2,5,'2014-04-17 16:53:54'),(93,3604,2,7,'2014-04-17 16:53:54'),(94,3604,2,9,'2014-04-17 16:54:33'),(95,3604,2,9,'2014-04-17 16:54:33'),(96,3604,2,9,'2014-04-17 16:54:34'),(97,3604,2,9,'2014-04-17 16:54:34'),(98,3604,2,8,'2014-04-17 16:54:44'),(99,3604,2,2,'2014-04-17 16:54:54'),(100,3604,2,1,'2014-04-17 16:54:54'),(101,3604,2,6,'2014-04-17 16:55:34'),(102,3604,2,2,'2014-04-17 16:55:38'),(103,3604,2,1,'2014-04-17 16:55:38'),(104,3604,2,16,'2014-04-17 16:55:38'),(105,3604,2,7,'2014-04-17 16:56:12'),(106,3604,2,5,'2014-04-17 16:56:12'),(107,3604,2,10,'2014-04-17 16:56:15'),(108,3604,2,24,'2014-04-17 16:56:23'),(109,3604,2,2,'2014-04-17 16:58:19'),(110,3604,2,1,'2014-04-17 16:58:19'),(111,3604,2,2,'2014-04-17 16:58:44'),(112,3604,2,1,'2014-04-17 16:58:44'),(113,3604,2,13,'2014-04-17 17:01:42'),(114,3604,2,14,'2014-04-17 17:01:42'),(115,3604,2,5,'2014-04-17 17:01:42'),(116,3604,2,7,'2014-04-17 17:01:42'),(117,3604,2,10,'2014-04-17 17:01:43'),(118,3604,2,13,'2014-04-17 17:03:36'),(119,3604,2,14,'2014-04-17 17:03:36'),(120,3604,2,5,'2014-04-17 17:03:40'),(121,3604,2,7,'2014-04-17 17:03:40'),(122,3604,2,10,'2014-04-17 17:03:41'),(123,3604,2,5,'2014-04-17 17:03:48'),(124,3604,2,7,'2014-04-17 17:03:49'),(125,3604,2,10,'2014-04-17 17:03:50'),(126,3604,2,5,'2014-04-17 17:04:04'),(127,3604,2,7,'2014-04-17 17:04:04'),(128,3604,2,10,'2014-04-17 17:04:05'),(129,3604,2,24,'2014-04-17 17:04:09'),(130,3604,2,3,'2014-04-17 17:04:10'),(131,3604,2,14,'2014-04-17 17:04:12'),(132,3604,2,5,'2014-04-17 17:04:13'),(133,3604,2,7,'2014-04-17 17:04:13'),(134,3604,2,10,'2014-04-17 17:04:16'),(135,3604,2,24,'2014-04-17 17:04:17'),(136,3604,2,1,'2014-04-17 17:38:13'),(137,3604,2,2,'2014-04-17 17:38:13'),(138,3604,2,2,'2014-04-17 17:38:18'),(139,3604,2,1,'2014-04-17 17:38:18'),(140,3604,2,19,'2014-04-17 17:38:23'),(141,3604,2,3,'2014-04-17 17:38:30'),(142,3604,2,3,'2014-04-17 17:38:33'),(143,3604,2,14,'2014-04-17 17:38:45'),(144,3604,2,24,'2014-04-17 17:38:47'),(145,3604,2,21,'2014-04-17 17:38:51'),(146,3604,2,8,'2014-04-17 17:39:01'),(147,3604,2,2,'2014-04-17 17:39:06'),(148,3604,2,1,'2014-04-17 17:39:06'),(149,3604,2,2,'2014-04-17 18:30:52'),(150,3604,2,1,'2014-04-17 18:30:52');
/*!40000 ALTER TABLE `WeiqiStep` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-04-20 22:39:50