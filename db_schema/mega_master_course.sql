-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mega
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `master_course`
--

DROP TABLE IF EXISTS `master_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `master_course` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `master_id` bigint unsigned NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `teacher` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ects` decimal(4,1) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `idx_master_course_master` (`master_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `master_course`
--

LOCK TABLES `master_course` WRITE;
/*!40000 ALTER TABLE `master_course` DISABLE KEYS */;
INSERT INTO `master_course` VALUES (21,1,'Advanced Algorithms','Dr. Papadopoulos',6.0,'Graph algorithms and optimization'),(22,2,'Power Electronics','Dr. Nikolaou',5.0,'Semiconductor devices and circuits'),(23,3,'Statistical Modeling','Dr. Georgiou',6.0,'Regression and stochastic models'),(24,4,'Quantum Mechanics','Dr. Koutso',5.0,'Quantum theory fundamentals'),(25,5,'Deep Learning','Dr. Vassilis',7.0,'Neural networks and architectures'),(26,6,'Genetics Laboratory','Dr. Maria',4.0,'Experimental genetics'),(27,7,'Clinical Statistics','Dr. Kostas',5.0,'Medical data analysis'),(28,8,'Robotics Control','Dr. Elena',6.0,'Control systems for robots'),(29,9,'Nutrition Biochemistry','Dr. Anna',5.0,'Human metabolism'),(30,10,'Tourism Economics','Dr. Dimitris',6.0,'Economic models in tourism');
/*!40000 ALTER TABLE `master_course` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-23 16:46:07
