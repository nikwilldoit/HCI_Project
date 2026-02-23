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
-- Table structure for table `erasmus_course`
--

DROP TABLE IF EXISTS `erasmus_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erasmus_course` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `erasmus_id` bigint unsigned NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ects` decimal(4,1) DEFAULT NULL,
  `teacher` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `idx_erasmus_course_erasmus` (`erasmus_id`),
  CONSTRAINT `fk_erasmus_course_erasmus` FOREIGN KEY (`erasmus_id`) REFERENCES `erasmus` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erasmus_course`
--

LOCK TABLES `erasmus_course` WRITE;
/*!40000 ALTER TABLE `erasmus_course` DISABLE KEYS */;
INSERT INTO `erasmus_course` VALUES (1,1,'Distributed Systems',6.0,'Dr. Kostas','Cloud and distributed computing'),(2,2,'Circuit Design',5.0,'Dr. Nikos','Hardware design principles'),(3,3,'Numerical Methods',6.0,'Dr. Giorgos','Scientific computation'),(4,4,'Modern Physics',5.0,'Dr. Andreas','Relativity and quantum basics'),(5,5,'Machine Learning',7.0,'Dr. Ioanna','Supervised and unsupervised learning'),(6,6,'Molecular Biology',5.0,'Dr. Helen','Cell biology studies'),(7,7,'Medical Imaging',6.0,'Dr. Petros','MRI and imaging analysis'),(8,8,'Robot Kinematics',6.0,'Dr. Sofia','Robot movement mathematics'),(9,9,'Dietary Science',5.0,'Dr. Eleni','Nutrition and health'),(10,10,'Tourism Marketing',6.0,'Dr. Manolis','Marketing strategies in tourism');
/*!40000 ALTER TABLE `erasmus_course` ENABLE KEYS */;
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
