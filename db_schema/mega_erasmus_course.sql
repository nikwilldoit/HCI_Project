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
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erasmus_course`
--

LOCK TABLES `erasmus_course` WRITE;
/*!40000 ALTER TABLE `erasmus_course` DISABLE KEYS */;
INSERT INTO `erasmus_course` VALUES (1,1,'Introduction to Computer Science',5.0,'TBA',''),(2,1,'Data Structures and Algorithms',5.0,'TBA',''),(3,2,'Programming Fundamentals',5.0,'TBA',''),(4,2,'Database Systems',5.0,'TBA',''),(5,3,'Algorithms and Complexity',5.0,'TBA',''),(6,3,'Software Engineering',5.0,'TBA',''),(7,6,'Computer Networks',5.0,'TBA',''),(8,6,'Distributed Systems',5.0,'TBA',''),(9,7,'Human-Computer Interaction',5.0,'TBA',''),(10,7,'Machine Learning Basics',5.0,'TBA',''),(11,8,'Web Technologies',5.0,'TBA',''),(12,8,'Operating Systems',5.0,'TBA',''),(13,9,'Object-Oriented Programming',5.0,'TBA',''),(14,9,'Computer Architecture',5.0,'TBA',''),(15,10,'Database Design',5.0,'TBA',''),(16,10,'Mobile Application Development',5.0,'TBA',''),(17,11,'Artificial Intelligence',5.0,'TBA',''),(18,11,'Information Systems',5.0,'TBA',''),(19,12,'Cloud Computing',5.0,'TBA',''),(20,12,'Software Architecture',5.0,'TBA',''),(21,13,'Computer Graphics',5.0,'TBA',''),(22,13,'Parallel Programming',5.0,'TBA',''),(23,14,'Cybersecurity',5.0,'TBA',''),(24,14,'Databases and Information Systems',5.0,'TBA',''),(25,15,'Formal Methods',5.0,'TBA',''),(26,15,'Software Testing',5.0,'TBA',''),(27,16,'Data Mining',5.0,'TBA',''),(28,16,'Networks and Security',5.0,'TBA',''),(29,17,'Computer Vision',5.0,'TBA',''),(30,17,'Big Data Systems',5.0,'TBA',''),(31,18,'Human Computer Interaction',5.0,'TBA',''),(32,18,'Applied Artificial Intelligence',5.0,'TBA',''),(33,19,'Software Engineering Project',5.0,'TBA',''),(34,19,'Information Retrieval',5.0,'TBA',''),(35,20,'Programming Languages',5.0,'TBA',''),(36,20,'Data Visualization',5.0,'TBA',''),(37,21,'Operating Systems Design',5.0,'TBA',''),(38,21,'Networks Lab',5.0,'TBA',''),(39,22,'Compiler Design',5.0,'TBA',''),(40,22,'Human-Computer Interaction',5.0,'TBA',''),(41,23,'Distributed Algorithms',5.0,'TBA',''),(42,23,'Database Management Systems',5.0,'TBA',''),(43,24,'Software Product Management',5.0,'TBA',''),(44,24,'Machine Learning',5.0,'TBA',''),(45,25,'Cyber-Physical Systems',5.0,'TBA',''),(46,25,'Advanced Databases',5.0,'TBA',''),(47,26,'Software Architecture and Design',5.0,'TBA',''),(48,26,'Information Security',5.0,'TBA',''),(49,27,'Parallel and Distributed Computing',5.0,'TBA',''),(50,27,'Interactive Systems',5.0,'TBA',''),(51,28,'Data Engineering',5.0,'TBA',''),(52,28,'Computer Networks',5.0,'TBA',''),(53,29,'Internet of Things',5.0,'TBA',''),(54,29,'Software Engineering Practices',5.0,'TBA',''),(55,30,'Artificial Intelligence and Society',5.0,'TBA',''),(56,30,'Database Applications',5.0,'TBA',''),(57,31,'Web Application Security',5.0,'TBA',''),(58,31,'Operating Systems Concepts',5.0,'TBA',''),(59,32,'Cloud Infrastructure',5.0,'TBA',''),(60,32,'Data Analytics',5.0,'TBA',''),(61,33,'Formal Verification',5.0,'TBA',''),(62,33,'Software Design Patterns',5.0,'TBA',''),(63,37,'Advanced Programming',5.0,'TBA',''),(64,37,'Machine Learning Engineering',5.0,'TBA',''),(65,38,'Computer Security',5.0,'TBA',''),(66,38,'Software Testing and QA',5.0,'TBA',''),(67,39,'Information Systems Analysis',5.0,'TBA',''),(68,39,'Database Programming',5.0,'TBA',''),(69,40,'Advanced Algorithms',5.0,'TBA',''),(70,40,'Systems Software',5.0,'TBA',''),(71,41,'Human-Computer Interaction Lab',5.0,'TBA',''),(72,41,'Artificial Intelligence Lab',5.0,'TBA',''),(73,42,'Distributed Systems and Cloud',5.0,'TBA',''),(74,42,'Security Engineering',5.0,'TBA',''),(75,43,'Parallel Programming Models',5.0,'TBA',''),(76,43,'Computational Mathematics',5.0,'TBA',''),(77,44,'Computer Networks and Protocols',5.0,'TBA',''),(78,44,'Software Engineering Management',5.0,'TBA',''),(79,45,'AI for Engineers',5.0,'TBA',''),(80,45,'Data Structures in Practice',5.0,'TBA','');
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

-- Dump completed on 2026-03-21 10:24:50
