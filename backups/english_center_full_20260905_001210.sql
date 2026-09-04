-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: English_center
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Current Database: `English_center`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `English_center` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `English_center`;

--
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classId` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `dueDate` datetime DEFAULT NULL,
  `createdBy` bigint DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxAssignmentsClassId` (`classId`),
  KEY `idxAssignmentsCreatedBy` (`createdBy`),
  KEY `idxAssignmentsDueDate` (`dueDate`),
  CONSTRAINT `fkAssignmentsClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkAssignmentsTeacher` FOREIGN KEY (`createdBy`) REFERENCES `teacherprofiles` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignments`
--

LOCK TABLES `assignments` WRITE;
/*!40000 ALTER TABLE `assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendancerecords`
--

DROP TABLE IF EXISTS `attendancerecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendancerecords` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classId` bigint NOT NULL,
  `studentId` bigint NOT NULL,
  `sessionDate` date NOT NULL,
  `attendanceStatus` enum('PRESENT','ABSENT','LATE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PRESENT',
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `markedBy` bigint DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqAttendanceClassStudentDate` (`classId`,`studentId`,`sessionDate`),
  KEY `idxAttendanceRecordsClassId` (`classId`),
  KEY `idxAttendanceRecordsStudentId` (`studentId`),
  KEY `idxAttendanceRecordsSessionDate` (`sessionDate`),
  KEY `idxAttendanceRecordsMarkedBy` (`markedBy`),
  CONSTRAINT `fkAttendanceRecordsClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkAttendanceRecordsMarkedBy` FOREIGN KEY (`markedBy`) REFERENCES `teacherprofiles` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fkAttendanceRecordsStudent` FOREIGN KEY (`studentId`) REFERENCES `studentprofiles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendancerecords`
--

LOCK TABLES `attendancerecords` WRITE;
/*!40000 ALTER TABLE `attendancerecords` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendancerecords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `className` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `courseId` bigint NOT NULL,
  `teacherId` bigint DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `capacity` int NOT NULL DEFAULT '20',
  `roomName` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PLANNED','ONGOING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PLANNED',
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqClassesClassCode` (`classCode`),
  KEY `idxClassesCourseId` (`courseId`),
  KEY `idxClassesTeacherId` (`teacherId`),
  KEY `idxClassesStatus` (`status`),
  CONSTRAINT `fkClassesCourse` FOREIGN KEY (`courseId`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fkClassesTeacher` FOREIGN KEY (`teacherId`) REFERENCES `teacherprofiles` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classschedules`
--

DROP TABLE IF EXISTS `classschedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classschedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classId` bigint NOT NULL,
  `dayOfWeek` enum('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `meetingUrl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqClassSchedulesClassTime` (`classId`,`dayOfWeek`,`startTime`,`endTime`,`startDate`),
  KEY `idxClassSchedulesClassId` (`classId`),
  CONSTRAINT `fkClassSchedulesClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chkClassSchedulesTime` CHECK ((`startTime` < `endTime`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classschedules`
--

LOCK TABLES `classschedules` WRITE;
/*!40000 ALTER TABLE `classschedules` DISABLE KEYS */;
/*!40000 ALTER TABLE `classschedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `courseCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `courseName` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `durationWeeks` int DEFAULT '0',
  `tuitionFee` decimal(12,2) DEFAULT '0.00',
  `status` enum('OPEN','CLOSED','ARCHIVED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OPEN',
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqCoursesCourseCode` (`courseCode`),
  KEY `idxCoursesStatus` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `studentId` bigint NOT NULL,
  `classId` bigint NOT NULL,
  `enrolledAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('ENROLLED','STUDYING','COMPLETED','DROPPED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ENROLLED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqEnrollmentsStudentClass` (`studentId`,`classId`),
  KEY `idxEnrollmentsStudentId` (`studentId`),
  KEY `idxEnrollmentsClassId` (`classId`),
  KEY `idxEnrollmentsStatus` (`status`),
  CONSTRAINT `fkEnrollmentsClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkEnrollmentsStudent` FOREIGN KEY (`studentId`) REFERENCES `studentprofiles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollments`
--

LOCK TABLES `enrollments` WRITE;
/*!40000 ALTER TABLE `enrollments` DISABLE KEYS */;
/*!40000 ALTER TABLE `enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `graderecords`
--

DROP TABLE IF EXISTS `graderecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `graderecords` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `studentId` bigint NOT NULL,
  `classId` bigint NOT NULL,
  `gradeType` enum('QUIZ','MIDTERM','FINAL','ASSIGNMENT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `scoreValue` decimal(5,2) NOT NULL,
  `comment` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gradedBy` bigint DEFAULT NULL,
  `gradedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `sentAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqGradeRecordsStudentClassTypeTitle` (`studentId`,`classId`,`gradeType`,`title`),
  KEY `idxGradeRecordsStudentId` (`studentId`),
  KEY `idxGradeRecordsClassId` (`classId`),
  KEY `idxGradeRecordsGradeType` (`gradeType`),
  KEY `idxGradeRecordsGradedBy` (`gradedBy`),
  CONSTRAINT `fkGradeRecordsClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkGradeRecordsStudent` FOREIGN KEY (`studentId`) REFERENCES `studentprofiles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkGradeRecordsTeacher` FOREIGN KEY (`gradedBy`) REFERENCES `teacherprofiles` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chkGradeRecordsScore` CHECK (((`scoreValue` >= 0) and (`scoreValue` <= 100)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graderecords`
--

LOCK TABLES `graderecords` WRITE;
/*!40000 ALTER TABLE `graderecords` DISABLE KEYS */;
/*!40000 ALTER TABLE `graderecords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `studentId` bigint NOT NULL,
  `classId` bigint NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `dueDate` date DEFAULT NULL,
  `paidDate` date DEFAULT NULL,
  `paymentMethod` enum('CASH','BANK_TRANSFER','MOMO','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CASH',
  `status` enum('PAID','UNPAID','PARTIAL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNPAID',
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxPaymentsStudentId` (`studentId`),
  KEY `idxPaymentsClassId` (`classId`),
  KEY `idxPaymentsStatus` (`status`),
  KEY `idxPaymentsDueDate` (`dueDate`),
  CONSTRAINT `fkPaymentsClass` FOREIGN KEY (`classId`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkPaymentsStudent` FOREIGN KEY (`studentId`) REFERENCES `studentprofiles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chkPaymentsAmount` CHECK ((`amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studentassignments`
--

DROP TABLE IF EXISTS `studentassignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studentassignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignmentId` bigint NOT NULL,
  `studentId` bigint NOT NULL,
  `submissionText` text COLLATE utf8mb4_unicode_ci,
  `submittedAt` datetime DEFAULT NULL,
  `status` enum('PENDING','SUBMITTED','LATE','GRADED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `scoreValue` decimal(5,2) DEFAULT NULL,
  `feedback` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqStudentAssignmentsAssignmentStudent` (`assignmentId`,`studentId`),
  KEY `idxStudentAssignmentsAssignmentId` (`assignmentId`),
  KEY `idxStudentAssignmentsStudentId` (`studentId`),
  KEY `idxStudentAssignmentsStatus` (`status`),
  CONSTRAINT `fkStudentAssignmentsAssignment` FOREIGN KEY (`assignmentId`) REFERENCES `assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkStudentAssignmentsStudent` FOREIGN KEY (`studentId`) REFERENCES `studentprofiles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chkStudentAssignmentsScore` CHECK (((`scoreValue` is null) or ((`scoreValue` >= 0) and (`scoreValue` <= 100))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studentassignments`
--

LOCK TABLES `studentassignments` WRITE;
/*!40000 ALTER TABLE `studentassignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `studentassignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studentprofiles`
--

DROP TABLE IF EXISTS `studentprofiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studentprofiles` (
  `id` bigint NOT NULL,
  `studentCode` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dateOfBirth` date DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parentName` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parentPhone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqStudentProfilesStudentCode` (`studentCode`),
  CONSTRAINT `fkStudentProfilesUser` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studentprofiles`
--

LOCK TABLES `studentprofiles` WRITE;
/*!40000 ALTER TABLE `studentprofiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `studentprofiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacherprofiles`
--

DROP TABLE IF EXISTS `teacherprofiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacherprofiles` (
  `id` bigint NOT NULL,
  `teacherCode` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `specialization` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qualification` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `experienceYears` int DEFAULT NULL,
  `documentFolderId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documentFolderUrl` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqTeacherProfilesTeacherCode` (`teacherCode`),
  CONSTRAINT `fkTeacherProfilesUser` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacherprofiles`
--

LOCK TABLES `teacherprofiles` WRITE;
/*!40000 ALTER TABLE `teacherprofiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacherprofiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `passwordHash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phoneNumber` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` enum('MALE','FEMALE','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT 'OTHER',
  `role` enum('MANAGER','TEACHER','STUDENT') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT',
  `status` enum('ACTIVE','INACTIVE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `avatarUrl` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqUsersEmail` (`email`),
  KEY `idxUsersRole` (`role`),
  KEY `idxUsersStatus` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'System Manager','manager@example.com','$2a$12$SG3xuyy4xxhbw5k7t8HzGemS24hCqjlsFM2e/Q2jvMdcKIi1QzQly','0900000000','MALE','MANAGER','ACTIVE',NULL,'2026-07-14 00:45:13','2026-07-16 17:18:22'),(2,'Nguyen Van Teacher','teacher1@example.com','$2a$12$gpyhjG4xQ0X84J3ZVOMnbuPdbRAz8Rf0tMFzpdLBKsXpPToQV/GCi','0901234567','MALE','TEACHER','ACTIVE',NULL,'2026-07-14 00:58:12','2026-07-15 15:48:32'),(3,'Nguyen Van Student','student1@example.com','$2a$12$j3XT/teIHSnZu/d9ZqGjV.MTyd6IW5e5CbViq7bEvpUzuCK7HYsRi','0987654321','MALE','STUDENT','ACTIVE',NULL,'2026-07-16 17:30:12','2026-07-16 17:30:12');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'English_center'
--

--
-- Dumping routines for database 'English_center'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-05 00:12:10
