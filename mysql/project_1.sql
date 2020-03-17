-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- 생성 시간: 19-04-30 00:54
-- 서버 버전: 10.3.8-MariaDB
-- PHP 버전: 7.2.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 데이터베이스: `project_1`
--

-- --------------------------------------------------------
CREATE DATABASE project_1;

USE project_1;
--
-- 테이블 구조 `2002_traffic_state`
--

CREATE TABLE `2002_traffic_state` (
  `traffic_num` int(11) NOT NULL,
  `reader_id` varchar(11) NOT NULL,
  `state` int(11) NOT NULL,
  `located` int(11) NOT NULL,
  `user_id` varchar(10) DEFAULT NULL,
  `res_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `2002_traffic_state`
--

INSERT INTO `2002_traffic_state` (`traffic_num`, `reader_id`, `state`, `located`, `user_id`, `res_date`) VALUES
(2002, 'a0001', 0, 0, NULL, NULL),
(2002, 'a0002', 0, 1, NULL, NULL),
(2102, 'a0003', 0, 0, NULL, NULL),
(2102, 'a0004', 0, 1, NULL, NULL),
(2202, 'a0005', 0, 0, NULL, NULL),
(2202, 'a0006', 0, 1, NULL, NULL),
(2302, 'a0007', 0, 0, NULL, NULL),
(2302, 'a0008', 0, 1, NULL, NULL),
(2702, 'a0009', 0, 1, NULL, NULL),
(2402, 'a0010', 0, 0, NULL, NULL),
(2402, 'a0011', 0, 1, NULL, NULL),
(2502, 'a0012', 0, 0, NULL, NULL),
(2502, 'a0013', 0, 1, NULL, NULL),
(2602, 'a0014', 0, 0, NULL, NULL),
(2602, 'a0015', 0, 1, NULL, NULL),
(2802, 'a0016', 0, 0, NULL, NULL),
(2802, 'a0017', 0, 1, NULL, NULL),
(2902, 'a0018', 0, 0, NULL, NULL),
(2902, 'a0019', 0, 1, NULL, NULL),
(2702, 'a0020', 0, 0, NULL, NULL);

--
-- 트리거 `2002_traffic_state`
--
DELIMITER $$
CREATE TRIGGER `2002updateTrigger` AFTER UPDATE ON `2002_traffic_state` FOR EACH ROW update line2_train_state set empty_seat = (select count(if(state=0,state,null)) from 2002_traffic_state where traffic_num=NEW.traffic_num) where traffic_num=NEW.traffic_num
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `2004_traffic_state`
--

CREATE TABLE `2004_traffic_state` (
  `traffic_num` int(11) NOT NULL,
  `reader_id` varchar(11) NOT NULL,
  `state` int(11) NOT NULL,
  `located` int(11) NOT NULL,
  `user_id` varchar(10) DEFAULT NULL,
  `res_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `2004_traffic_state`
--

INSERT INTO `2004_traffic_state` (`traffic_num`, `reader_id`, `state`, `located`, `user_id`, `res_date`) VALUES
(2004, 'a0031', 0, 0, NULL, NULL),
(2004, 'a0032', 0, 1, NULL, NULL),
(2104, 'a0033', 0, 0, NULL, NULL),
(2104, 'a0034', 0, 1, NULL, NULL),
(2204, 'a0035', 0, 0, NULL, NULL),
(2204, 'a0036', 0, 1, NULL, NULL),
(2304, 'a0037', 0, 0, NULL, NULL),
(2304, 'a0038', 0, 1, NULL, NULL),
(2404, 'a0039', 0, 0, NULL, NULL),
(2404, 'a0040', 0, 1, NULL, NULL),
(2504, 'a0041', 0, 0, NULL, NULL),
(2504, 'a0042', 0, 1, NULL, NULL),
(2604, 'a0043', 0, 0, NULL, NULL),
(2604, 'a0044', 0, 1, NULL, NULL),
(2704, 'a0045', 0, 0, NULL, NULL),
(2704, 'a0046', 0, 1, NULL, NULL),
(2804, 'a0047', 0, 0, NULL, NULL),
(2804, 'a0048', 0, 1, NULL, NULL),
(2904, 'a0049', 0, 0, NULL, NULL),
(2904, 'a0050', 0, 1, NULL, NULL);

--
-- 트리거 `2004_traffic_state`
--
DELIMITER $$
CREATE TRIGGER `2004updateTrigger` AFTER UPDATE ON `2004_traffic_state` FOR EACH ROW update line2_train_state set empty_seat = (select count(if(state=0,state,null)) from 2004_traffic_state where traffic_num=NEW.traffic_num) where traffic_num=NEW.traffic_num
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `2007_traffic_state`
--

CREATE TABLE `2007_traffic_state` (
  `traffic_num` int(11) NOT NULL,
  `reader_id` varchar(11) NOT NULL,
  `state` int(11) NOT NULL,
  `located` int(11) NOT NULL,
  `user_id` varchar(10) DEFAULT NULL,
  `res_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `2007_traffic_state`
--

INSERT INTO `2007_traffic_state` (`traffic_num`, `reader_id`, `state`, `located`, `user_id`, `res_date`) VALUES
(2007, 'a0061', 0, 0, NULL, NULL),
(2007, 'a0062', 0, 1, NULL, NULL),
(2107, 'a0063', 0, 0, NULL, NULL),
(2107, 'a0064', 0, 1, NULL, NULL),
(2207, 'a0065', 0, 0, NULL, NULL),
(2207, 'a0066', 0, 1, NULL, NULL),
(2307, 'a0067', 0, 0, NULL, NULL),
(2307, 'a0068', 0, 1, NULL, NULL),
(2407, 'a0069', 0, 0, NULL, NULL),
(2407, 'a0070', 0, 1, NULL, NULL),
(2507, 'a0071', 0, 0, NULL, NULL),
(2507, 'a0072', 0, 1, NULL, NULL),
(2607, 'a0073', 0, 0, NULL, NULL),
(2607, 'a0074', 0, 1, NULL, NULL),
(2707, 'a0075', 0, 0, NULL, NULL),
(2707, 'a0076', 0, 1, NULL, NULL),
(2807, 'a0077', 0, 0, NULL, NULL),
(2807, 'a0078', 0, 1, NULL, NULL),
(2907, 'a0079', 0, 0, NULL, NULL),
(2907, 'a0080', 0, 1, NULL, NULL);

--
-- 트리거 `2007_traffic_state`
--
DELIMITER $$
CREATE TRIGGER `2007updateTrigger` AFTER UPDATE ON `2007_traffic_state` FOR EACH ROW update line2_train_state set empty_seat = (select count(if(state=0,state,null)) from 2007_traffic_state where traffic_num=NEW.traffic_num) where traffic_num=NEW.traffic_num
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `2010_traffic_state`
--

CREATE TABLE `2010_traffic_state` (
  `traffic_num` int(11) NOT NULL,
  `reader_id` varchar(11) NOT NULL,
  `state` int(11) NOT NULL,
  `located` int(11) NOT NULL,
  `user_id` varchar(10) DEFAULT NULL,
  `res_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `2010_traffic_state`
--

INSERT INTO `2010_traffic_state` (`traffic_num`, `reader_id`, `state`, `located`, `user_id`, `res_date`) VALUES
(2010, 'a0091', 0, 0, NULL, NULL),
(2010, 'a0092', 0, 1, NULL, NULL),
(2110, 'a0093', 0, 0, NULL, NULL),
(2110, 'a0094', 0, 1, NULL, NULL),
(2210, 'a0095', 0, 0, NULL, NULL),
(2210, 'a0096', 0, 1, NULL, NULL),
(2310, 'a0097', 0, 0, NULL, NULL),
(2310, 'a0098', 0, 1, NULL, NULL),
(2410, 'a0099', 0, 0, NULL, NULL),
(2410, 'a0100', 0, 1, NULL, NULL),
(2510, 'a0101', 0, 0, NULL, NULL),
(2510, 'a0102', 0, 1, NULL, NULL),
(2610, 'a0103', 0, 0, NULL, NULL),
(2610, 'a0104', 0, 1, NULL, NULL),
(2710, 'a0105', 0, 0, NULL, NULL),
(2710, 'a0106', 0, 1, NULL, NULL),
(2810, 'a0107', 0, 0, NULL, NULL),
(2810, 'a0108', 0, 1, NULL, NULL),
(2910, 'a0109', 0, 0, NULL, NULL),
(2910, 'a0110', 0, 1, NULL, NULL);

--
-- 트리거 `2010_traffic_state`
--
DELIMITER $$
CREATE TRIGGER `2010updateTrigger` AFTER UPDATE ON `2010_traffic_state` FOR EACH ROW update line2_train_state set empty_seat = (select count(if(state=0,state,null)) from 2010_traffic_state where traffic_num=NEW.traffic_num) where traffic_num=NEW.traffic_num
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `entire_state`
--

CREATE TABLE `entire_state` (
  `first_traffic_num` int(11) NOT NULL,
  `train_num` int(11) NOT NULL,
  `empty_seat` int(11) NOT NULL,
  `total_seat` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `entire_state`
--

INSERT INTO `entire_state` (`first_traffic_num`, `train_num`, `empty_seat`, `total_seat`) VALUES
(2002, 2331, 20, 20),
(2004, 2019, 20, 20),
(2007, 2314, 20, 20),
(2010, 2318, 20, 20),
(2012, 2333, 8, 20),
(2031, 2661, 15, 20);

-- --------------------------------------------------------

--
-- 테이블 구조 `line2_train_state`
--

CREATE TABLE `line2_train_state` (
  `first_traffic_num` int(11) NOT NULL,
  `traffic_num` int(11) NOT NULL,
  `empty_seat` int(11) NOT NULL,
  `total_seat` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

--
-- 테이블의 덤프 데이터 `line2_train_state`
--

INSERT INTO `line2_train_state` (`first_traffic_num`, `traffic_num`, `empty_seat`, `total_seat`) VALUES
(2002, 2002, 2, 2),
(2004, 2004, 2, 2),
(2007, 2007, 2, 2),
(2010, 2010, 2, 2),
(2012, 2012, 2, 2),
(2031, 2031, 2, 2),
(2002, 2102, 2, 2),
(2004, 2104, 2, 2),
(2007, 2107, 2, 2),
(2010, 2110, 2, 2),
(2012, 2112, 2, 2),
(2031, 2131, 1, 2),
(2002, 2202, 2, 2),
(2004, 2204, 2, 2),
(2007, 2207, 2, 2),
(2010, 2210, 2, 2),
(2012, 2212, 2, 2),
(2031, 2231, 1, 2),
(2002, 2302, 2, 2),
(2004, 2304, 2, 2),
(2007, 2307, 2, 2),
(2010, 2310, 2, 2),
(2012, 2312, 2, 2),
(2031, 2331, 1, 2),
(2002, 2402, 2, 2),
(2004, 2404, 2, 2),
(2007, 2407, 2, 2),
(2010, 2410, 2, 2),
(2012, 2412, 1, 2),
(2031, 2431, 0, 2),
(2002, 2502, 2, 2),
(2004, 2504, 2, 2),
(2007, 2507, 2, 2),
(2010, 2510, 2, 2),
(2012, 2512, 2, 2),
(2031, 2531, 0, 2),
(2002, 2602, 2, 2),
(2004, 2604, 2, 2),
(2007, 2607, 2, 2),
(2010, 2610, 2, 2),
(2012, 2612, 0, 2),
(2031, 2631, 0, 2),
(2002, 2702, 2, 2),
(2004, 2704, 2, 2),
(2007, 2707, 2, 2),
(2010, 2710, 2, 2),
(2012, 2712, 2, 2),
(2031, 2731, 2, 2),
(2002, 2802, 2, 2),
(2004, 2804, 2, 2),
(2007, 2807, 2, 2),
(2010, 2810, 2, 2),
(2012, 2812, 1, 2),
(2031, 2831, 1, 2),
(2002, 2902, 2, 2),
(2004, 2904, 2, 2),
(2007, 2907, 2, 2),
(2010, 2910, 2, 2),
(2012, 2912, 1, 2),
(2031, 2931, 2, 2);

--
-- 트리거 `line2_train_state`
--
DELIMITER $$
CREATE TRIGGER `updateTrigger2` AFTER UPDATE ON `line2_train_state` FOR EACH ROW update entire_state set empty_seat = (select sum(empty_seat) from line2_train_state where first_traffic_num=NEW.first_traffic_num) where first_traffic_num=NEW.first_traffic_num
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `reserve_list`
--

CREATE TABLE `reserve_list` (
  `user_id` varchar(10) NOT NULL,
  `train_num` int(11) NOT NULL,
  `reader_id` varchar(10) NOT NULL,
  `res_date` datetime NOT NULL,
  `traffic_num` int(11) NOT NULL,
  `located` int(11) NOT NULL,
  `station_name` varchar(10) NOT NULL,
  `next_station` varchar(10) NOT NULL,
  `drawable_id` int(11) NOT NULL,
  `arrival_time` int(11) NOT NULL,
  `first_traffic_num` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 트리거 `reserve_list`
--
DELIMITER $$
CREATE TRIGGER `updateTrigger3` BEFORE DELETE ON `reserve_list` FOR EACH ROW BEGIN
IF OLD.first_traffic_num = 2002 THEN
UPDATE 2002_traffic_state set state = 0, res_date=NULL, user_id=NULL where user_id=OLD.user_id;
END IF;
IF OLD.first_traffic_num = 2004 THEN
UPDATE 2004_traffic_state set state = 0, res_date=NULL, user_id=NULL where user_id=OLD.user_id;
END IF;
IF OLD.first_traffic_num = 2007 THEN
UPDATE 2007_traffic_state set state = 0, res_date=NULL, user_id=NULL where user_id=OLD.user_id;
END IF;
IF OLD.first_traffic_num = 2010 THEN
UPDATE 2010_traffic_state set state = 0, res_date=NULL, user_id=NULL where user_id=OLD.user_id;
END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- 테이블 구조 `user_list`
--

CREATE TABLE `user_list` (
  `pregnum` varchar(20) NOT NULL,
  `name` varchar(11) NOT NULL,
  `pregdate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `user_list`
--

INSERT INTO `user_list` (`pregnum`, `name`, `pregdate`) VALUES
('j940h919w', '우주현', '2018-07-01');

-- --------------------------------------------------------

--
-- 테이블 구조 `welfare_server`
--

CREATE TABLE `welfare_server` (
  `pregnum` varchar(20) NOT NULL,
  `name` varchar(11) NOT NULL,
  `pregdate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 테이블의 덤프 데이터 `welfare_server`
--

INSERT INTO `welfare_server` (`pregnum`, `name`, `pregdate`) VALUES
('111111111', '임산부1', '2019-01-01'),
('222222222', '임산부2', '2019-02-02'),
('333333333', '임산부3', '2019-03-03'),
('444444444', '임산부4', '2019-04-04');


--
-- 덤프된 테이블의 인덱스
--

--
-- 테이블의 인덱스 `2002_traffic_state`
--
ALTER TABLE `2002_traffic_state`
  ADD PRIMARY KEY (`reader_id`),
  ADD KEY `traffic_num_foreign_key` (`traffic_num`);

--
-- 테이블의 인덱스 `2004_traffic_state`
--
ALTER TABLE `2004_traffic_state`
  ADD PRIMARY KEY (`reader_id`),
  ADD KEY `traffic_num_foreign_key` (`traffic_num`);

--
-- 테이블의 인덱스 `2007_traffic_state`
--
ALTER TABLE `2007_traffic_state`
  ADD PRIMARY KEY (`reader_id`),
  ADD KEY `traffic_num_foreign_key` (`traffic_num`);

--
-- 테이블의 인덱스 `2010_traffic_state`
--
ALTER TABLE `2010_traffic_state`
  ADD PRIMARY KEY (`reader_id`),
  ADD KEY `traffic_num_foreign_key` (`traffic_num`);

--
-- 테이블의 인덱스 `entire_state`
--
ALTER TABLE `entire_state`
  ADD PRIMARY KEY (`first_traffic_num`),
  ADD UNIQUE KEY `train_num` (`train_num`);

--
-- 테이블의 인덱스 `line2_train_state`
--
ALTER TABLE `line2_train_state`
  ADD PRIMARY KEY (`traffic_num`),
  ADD KEY `first_traffic_num_foreign_key` (`first_traffic_num`);

--
-- 테이블의 인덱스 `reserve_list`
--
ALTER TABLE `reserve_list`
  ADD PRIMARY KEY (`user_id`);

--
-- 테이블의 인덱스 `user_list`
--
ALTER TABLE `user_list`
  ADD PRIMARY KEY (`pregnum`);

--
-- 테이블의 인덱스 `welfare_server`
--
ALTER TABLE `welfare_server`
  ADD PRIMARY KEY (`pregnum`);

--
-- 덤프된 테이블의 제약사항
--

--
-- 테이블의 제약사항 `2002_traffic_state`
--
ALTER TABLE `2002_traffic_state`
  ADD CONSTRAINT `traffic_num_foreign_key` FOREIGN KEY (`traffic_num`) REFERENCES `line2_train_state` (`traffic_num`) ON DELETE CASCADE;

--
-- 테이블의 제약사항 `line2_train_state`
--
ALTER TABLE `line2_train_state`
  ADD CONSTRAINT `first_traffic_foreign` FOREIGN KEY (`first_traffic_num`) REFERENCES `entire_state` (`first_traffic_num`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
