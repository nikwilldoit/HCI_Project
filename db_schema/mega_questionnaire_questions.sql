-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: pos_app
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `questionnaire_questions`
--

DROP TABLE IF EXISTS `questionnaire_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire_questions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `mode_type` enum('erasmus','master') NOT NULL,
  `question_id` int unsigned NOT NULL,
  `question` text NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_mode_question_order` (`mode_type`,`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questionnaire_questions`
--

LOCK TABLES `questionnaire_questions` WRITE;
/*!40000 ALTER TABLE `questionnaire_questions` DISABLE KEYS */;
INSERT INTO `questionnaire_questions` VALUES (1,'erasmus',1,'Σε ποιό μέρος της Ευρώπης θα ήθελες ιδανικά να πας για Erasmus;',1),(2,'erasmus',2,'Πόσο σημαντικό είναι για σένα το κόστος ζωής στη χώρα που θα πας;',1),(3,'erasmus',3,'Τι είδους πόλη θα προτιμούσες για την εμπειρία Erasmus;',1),(4,'erasmus',4,'Πόσο σημαντική είναι για σένα η φοιτητική ζωή και η κοινωνική εμπειρία στο Erasmus;',1),(5,'erasmus',5,'Πόσο σημαντικό είναι το επίπεδο και η φήμη του πανεπιστημίου που θα επιλέξεις;',1),(6,'erasmus',6,'Πόσο άνετα αισθάνεσαι να ζήσεις σε μια χώρα με διαφορετική γλώσσα και κουλτούρα δεδομένου ότι ζεις στην Ελλάδα;',1),(7,'erasmus',7,'Τι θα ήθελες να κερδίσεις κυρίως από την εμπειρία Erasmus;',1),(8,'master',1,'Σε ποιον τομέα της Πληροφορικής θα ήθελες να κάνεις μεταπτυχιακό;',1),(9,'master',2,'Σε ποιό μέρος της Ευρώπης θα προτιμούσες να κάνεις το μεταπτυχιακό σου;',1),(10,'master',3,'Πόσο σημαντική είναι για σένα η κατάταξη (ranking) του πανεπιστημίου;',1),(11,'master',4,'Ποιο είναι το μέγιστο budget που μπορείς να διαθέσεις για δίδακτρα και διαβίωση;',1),(12,'master',5,'Ποιος είναι ο βασικός σου στόχος μετά το μεταπτυχιακό;',1),(13,'master',6,'Πόσο σημαντικό είναι το πρόγραμμα να προσφέρει πρακτική άσκηση ή συνεργασία με εταιρείες;',1),(14,'master',7,'Πόσο σε ενδιαφέρει η δυνατότητα συνέχισης σε διδακτορικό (PhD) μετά το μεταπτυχιακό;',1);
/*!40000 ALTER TABLE `questionnaire_questions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-11 21:37:46
