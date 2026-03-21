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
-- Table structure for table `erasmus`
--

DROP TABLE IF EXISTS `erasmus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erasmus` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `university_id` bigint unsigned NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `website_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_erasmus_university` (`university_id`),
  CONSTRAINT `fk_erasmus_university` FOREIGN KEY (`university_id`) REFERENCES `universities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erasmus`
--

LOCK TABLES `erasmus` WRITE;
/*!40000 ALTER TABLE `erasmus` DISABLE KEYS */;
INSERT INTO `erasmus` VALUES (1,1,'Erasmus+ Informatics @ NKUA','https://www.di.uoa.gr/erasmus','English'),(2,2,'Erasmus+ CS @ AUEB','https://www.aueb.gr/erasmus','English'),(3,3,'Erasmus+ Informatics @ AUTH','https://www.csd.auth.gr/erasmus','English'),(4,4,'Erasmus+ Informatics @ TUM','https://www.in.tum.de/erasmus','English'),(5,5,'Erasmus+ Computer Science @ TU Delft','https://www.tudelft.nl/erasmus','English'),(6,6,'Erasmus+ CS @ KU Leuven','https://www.kuleuven.be/erasmus','English'),(7,7,'Erasmus+ CS @ Uni Stuttgart','https://www.uni-stuttgart.de/erasmus','English'),(8,8,'Erasmus+ IT @ Uppsala','https://www.it.uu.se/erasmus','English'),(9,9,'Erasmus+ CS @ Sorbonne University','https://www.sorbonne-universite.fr/erasmus','English'),(10,10,'Erasmus+ CS @ École Polytechnique','https://www.polytechnique.edu/erasmus','English'),(11,11,'Erasmus+ Informatics @ Université Paris-Saclay','https://www.universite-paris-saclay.fr/erasmus','English'),(12,12,'Erasmus+ Informatics @ Universidad Politécnica','https://www.upm.es/erasmus','English'),(13,13,'Erasmus+ CS @ University of Barcelona','https://www.ub.edu/erasmus','English'),(14,14,'Erasmus+ CS @ Autonomous University of Madrid','https://www.uam.es/erasmus','English'),(15,15,'Erasmus+ CS @ Sapienza University','https://www.uniroma1.it/erasmus','English'),(16,16,'Erasmus+ CS @ Politecnico di Milano','https://www.polimi.it/erasmus','English'),(17,17,'Erasmus+ CS @ University of Bologna','https://www.unibo.it/erasmus','English'),(18,18,'Erasmus+ CS @ University of','https://www.uw.edu.pl/erasmus','English'),(19,19,'Erasmus+ CS @ Warsaw University','https://www.pw.edu.pl/erasmus','English'),(20,20,'Erasmus+ CS @ Jagiellonian University','https://www.uj.edu.pl/erasmus','English'),(21,21,'Erasmus+ CS @ KTH Royal','https://www.kth.se/erasmus','English'),(22,22,'Erasmus+ CS @ Lund University','https://www.lunduniversity.lu.se/erasmus','English'),(23,23,'Erasmus+ CS @ University of','https://www.uio.no/erasmus','English'),(24,24,'Erasmus+ CS @ NTNU -','https://www.ntnu.edu/erasmus','English'),(25,25,'Erasmus+ CS @ University of','https://www.uib.no/erasmus','English'),(26,26,'Erasmus+ CS @ University of','https://www.helsinki.fi/erasmus','English'),(27,27,'Erasmus+ CS @ Aalto University','https://www.aalto.fi/erasmus','English'),(28,28,'Erasmus+ CS @ University of','https://www.utu.fi/erasmus','English'),(29,29,'Erasmus+ CS @ University of','https://www.unibuc.ro/erasmus','English'),(30,30,'Erasmus+ CS @ Politehnica University','https://upb.ro/erasmus','English'),(31,31,'Erasmus+ CS @ Babes-Bolyai University','https://www.ubbcluj.ro/erasmus','English'),(32,32,'Erasmus+ CS @ Taras Shevchenko','https://www.univ.kiev.ua/erasmus','English'),(33,33,'Erasmus+ CS @ Kyiv Polytechnic','https://kpi.ua/erasmus','English'),(34,34,'Erasmus+ CS @ Lviv Polytechnic','https://lpnu.ua/erasmus','English'),(35,35,'Erasmus+ CS @ University of','https://www.cs.ox.ac.uk/erasmus','English'),(36,36,'Erasmus+ CS @ University of','https://www.cl.cam.ac.uk/erasmus','English'),(37,37,'Erasmus+ CS @ Imperial College','https://www.imperial.ac.uk/erasmus','English'),(38,38,'Erasmus+ CS @ Lomonosov Moscow','https://www.msu.ru/erasmus','English'),(39,39,'Erasmus+ CS @ Saint Petersburg','https://spbu.ru/erasmus','English'),(40,40,'Erasmus+ CS @ MIPT -','https://mipt.ru/erasmus','English'),(41,41,'Erasmus+ CS @ RWTH Aachen','https://www.rwth-aachen.de/erasmus','English'),(42,42,'Erasmus+ CS @ University of','https://www.uva.nl/erasmus','English'),(43,43,'Erasmus+ CS @ Eindhoven University','https://www.tue.nl/erasmus','English'),(44,44,'Erasmus+ CS @ Ghent University','https://www.ugent.be/erasmus','English'),(45,45,'Erasmus+ CS @ Université catholique','https://www.uclouvain.be/erasmus','English');
/*!40000 ALTER TABLE `erasmus` ENABLE KEYS */;
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
