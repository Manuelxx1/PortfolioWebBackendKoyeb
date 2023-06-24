-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Host: bymvbe8znkoia8bzc7gy-mysql.services.clever-cloud.com:3306
-- Generation Time: Jun 21, 2023 at 09:09 PM
-- Server version: 8.0.15-5
-- PHP Version: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bymvbe8znkoia8bzc7gy`
--

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequence`
--
-- Creation: Jun 21, 2022 at 09:17 PM
--

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(1),
(1),
(1),
(1),
(1);

-- --------------------------------------------------------

--
-- Table structure for table `persona`
--
-- Creation: Jul 13, 2022 at 02:30 AM
--

CREATE TABLE `persona` (
  `id` bigint(20) NOT NULL,
  `apellido` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `edad` int(11) NOT NULL,
  `educacion` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `experiencia` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `informacion` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nombre` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `dni` bigint(20) NOT NULL,
  `nombre_usuario` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `persona`
--

INSERT INTO `persona` (`id`, `apellido`, `edad`, `educacion`, `email`, `experiencia`, `informacion`, `nombre`, `password`, `dni`, `nombre_usuario`) VALUES
(1, 'Alegre', 39, 'Primaria,Secundaria,Terciaria completa, Educacion Terciaria 1:Auxiliar de Diseño Grafico en Instituto Padre Adolfo Kolping, Educacion Terciaria 2: Auxiliar Administrativo Contable en Fundacion Alfa, Educacion Terciaria 3: Tecnico de Smartphones en ISE Digital, IDIOMAS: Español,Ingles Intermedio ', 'manuelbaidoxx6@gmail.com', 'PUESTO: Auxiliar de Diseño Grafico y Atencion al Publico en Taller de Diseño, AÑO: 2012-2016,PUESTO: Tecnico de Smartphones,Tablets,Notebooks,PC de Escritorio y Atencion al Publico en Servicio Tecnico,AÑO: 2018-2022 ', 'DESARROLLADOR WEB FULL STACK JR ', 'Manuel', 'ingresaraportfolio', 30790186, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_5i5dm95wvexstgngnatl7giel` (`nombre_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
