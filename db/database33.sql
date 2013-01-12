SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `mydb` ;
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`AirplaneType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`AirplaneType` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`AirplaneType` (
  `idAirplaneType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirplaneType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`AirplaneType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatType` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatType` (
  `idSeatType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idSeatType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`SeatType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Seat`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Seat` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Seat` (
  `idSeatType` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  PRIMARY KEY (`idAirplaneType`, `seatNumber`) ,
  CONSTRAINT `seat_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `seat_seatType_fk`
    FOREIGN KEY (`idSeatType` )
    REFERENCES `mydb`.`SeatType` (`idSeatType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `type_idx` ON `mydb`.`Seat` (`idSeatType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Airline`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Airline` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Airline` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirline`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`City`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`City` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`City` (
  `idCity` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idCity`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`City` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Airport`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Airport` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Airport` (
  `idAirport` VARCHAR(3) NOT NULL ,
  `idCity` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirport`) ,
  CONSTRAINT `airport_city_fk`
    FOREIGN KEY (`idCity` )
    REFERENCES `mydb`.`City` (`idCity` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `city_idx` ON `mydb`.`Airport` (`idCity` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Template`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Template` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Template` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `idAirportFrom` VARCHAR(3) NOT NULL ,
  `idAirportTo` VARCHAR(3) NOT NULL ,
  PRIMARY KEY (`idAirline`, `idTemplate`) ,
  CONSTRAINT `template_airlineCompany_fk`
    FOREIGN KEY (`idAirline` )
    REFERENCES `mydb`.`Airline` (`idAirline` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportFrom_fk`
    FOREIGN KEY (`idAirportFrom` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportTo_fk`
    FOREIGN KEY (`idAirportTo` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airplanetype_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirlineCompany_idx` ON `mydb`.`Template` (`idAirline` ASC) ;

CREATE INDEX `airportFrom_idx` ON `mydb`.`Template` (`idAirportFrom` ASC) ;

CREATE INDEX `airportTo_idx` ON `mydb`.`Template` (`idAirportTo` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Flight`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Flight` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Flight` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `idAirplaneType` INT NULL ,
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `departure` DATETIME NULL ,
  `arrival` DATETIME NULL ,
  PRIMARY KEY (`idFlight`) ,
  CONSTRAINT `flight_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `flight_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirplaneType_idx` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `Template_idx` ON `mydb`.`Flight` (`idAirline` ASC, `idTemplate` ASC) ;

CREATE UNIQUE INDEX `idAirplaneType_UNIQUE` ON `mydb`.`Flight` (`idAirplaneType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstance` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFlight`, `seatNumber`) ,
  CONSTRAINT `seatinstance_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstance_flight_fk`
    FOREIGN KEY (`idFlight` )
    REFERENCES `mydb`.`Flight` (`idFlight` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `bookingslot_seat_fk_idx` ON `mydb`.`SeatInstance` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstance_flight_fk_idx` ON `mydb`.`SeatInstance` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Period`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Period` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Period` (
  `idPeriod` INT NOT NULL AUTO_INCREMENT ,
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `fromDate` DATE NOT NULL COMMENT 'If fromDate is null, the period is valid from the beginning of time until toDate.' ,
  `toDate` DATE NULL COMMENT 'If toDate is null, period is valid from fromDate until the end of times.' ,
  `day` TINYINT NULL COMMENT 'If day is null, period spans all days.' ,
  `flightStartTime` TIME NOT NULL ,
  PRIMARY KEY (`idPeriod`) ,
  CONSTRAINT `period_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `flightTemplate_idx` ON `mydb`.`Period` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`Distance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Distance` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`Distance` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `distance` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`) ,
  CONSTRAINT `distance_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `distance_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`Distance` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`Distance` (`idToAirport` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`FlightTime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`FlightTime` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`FlightTime` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `duration` TIME NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`, `idAirplaneType`) ,
  CONSTRAINT `flightTime_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `mydb`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `mydb`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `mydb`.`FlightTime` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `mydb`.`FlightTime` (`idToAirport` ASC) ;

CREATE INDEX `airplaneType_idx` ON `mydb`.`FlightTime` (`idAirplaneType` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`SeatInstanceTemplate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`SeatInstanceTemplate` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`SeatInstanceTemplate` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`seatNumber`, `idAirline`, `idTemplate`) ,
  CONSTRAINT `seatinstancetemplate_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `mydb`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstancetemplate_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `mydb`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `seatinstancetemplate_seat_fk_idx` ON `mydb`.`SeatInstanceTemplate` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstancetemplate_template_fk_idx` ON `mydb`.`SeatInstanceTemplate` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `mydb`.`PeriodGenerated`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`PeriodGenerated` ;

CREATE  TABLE IF NOT EXISTS `mydb`.`PeriodGenerated` (
  `idPeriod` INT NOT NULL ,
  `generatedDate` DATE NOT NULL ,
  `idFlight` INT NULL ,
  PRIMARY KEY (`idPeriod`, `generatedDate`) ,
  CONSTRAINT `fk_periodgenerated_flight`
    FOREIGN KEY (`idFlight` )
    REFERENCES `mydb`.`Flight` (`idFlight` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_periodgenerated_period`
    FOREIGN KEY (`idPeriod` )
    REFERENCES `mydb`.`Period` (`idPeriod` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_periodgenerated_flight_idx` ON `mydb`.`PeriodGenerated` (`idFlight` ASC) ;

CREATE INDEX `fk_periodgenerated_period_idx` ON `mydb`.`PeriodGenerated` (`idPeriod` ASC) ;

CREATE UNIQUE INDEX `idFlight_UNIQUE` ON `mydb`.`PeriodGenerated` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Placeholder table for view `mydb`.`ActualFlight`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`ActualFlight` (`idFlight` INT, `idAirplaneType` INT, `idPeriod` INT, `idAirpline` INT, `idTemplate` INT, `departure` INT, `arrival` INT);

-- -----------------------------------------------------
-- Placeholder table for view `mydb`.`ActualSeatInstances`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`ActualSeatInstances` (`idFlight` INT, `seatNumber` INT, `idAirplaneType` INT, `price` INT);

-- -----------------------------------------------------
-- procedure interval_between
-- -----------------------------------------------------

USE `mydb`;
DROP procedure IF EXISTS `mydb`.`interval_between`;

DELIMITER $$
USE `mydb`$$
 
-- -------------------------------------------------------------------------------------------------------
-- Stored procedure to create a table with dated between two given time stamps and the specifified
-- interval
--
-- parameter
--   startdate: The first date in the list
--   enddate: The end of the timespan
--   interval_size: one of: MICROSECOND,SECOND,MINUTE,HOUR,DAY, WEEK, MONTH, YEAR
--   interval_value: the interval, in interval_size units
-- -------------------------------------------------------------------------------------------------------
CREATE PROCEDURE interval_between(startdate TIMESTAMP, enddate TIMESTAMP, interval_size VARCHAR(10), interval_value INT)
BEGIN
    DECLARE thisDate TIMESTAMP;
    SET thisDate = startdate;
 
    -- create the table for the dates if is not yet existing
    CREATE TEMPORARY TABLE IF NOT EXISTS time_intervals (
        interval_from TIMESTAMP
    );
 
    -- empty it from old data
    DELETE FROM time_intervals;
 
    WHILE thisDate <= enddate DO
        INSERT INTO time_intervals SELECT thisDate;
        SELECT
            CASE interval_size
                WHEN 'MICROSECOND' THEN TIMESTAMPadd(MICROSECOND, interval_value, thisDate)
                WHEN 'SECOND'      THEN TIMESTAMPadd(SECOND, interval_value, thisDate)
                WHEN 'MINUTE'      THEN TIMESTAMPadd(MINUTE, interval_value, thisDate)
                WHEN 'HOUR'        THEN TIMESTAMPadd(HOUR, interval_value, thisDate)
                WHEN 'DAY'         THEN TIMESTAMPadd(DAY, interval_value, thisDate)
                WHEN 'WEEK'        THEN TIMESTAMPadd(WEEK, interval_value, thisDate)
                WHEN 'MONTH'       THEN TIMESTAMPadd(MONTH, interval_value, thisDate)
                WHEN 'YEAR'        THEN TIMESTAMPadd(YEAR, interval_value, thisDate)
            END INTO thisDate;
    END WHILE;
 
 END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure generate_template_flights
-- -----------------------------------------------------

USE `mydb`;
DROP procedure IF EXISTS `mydb`.`generate_template_flights`;

DELIMITER $$
USE `mydb`$$


CREATE PROCEDURE generate_template_flights(
	IN input_idAirline VARCHAR(3),
	IN input_idTemplate DECIMAL(4,0),
	IN input_selectFrom DATE,
	IN input_selectTo DATE)
BEGIN
	-- Variable declarations
	DECLARE my_idPeriod INT;
	DECLARE done INT DEFAULT FALSE;
	-- Cursor declarions
	DECLARE pCursor CURSOR FOR
		SELECT p.`idPeriod`
		FROM period as p
		WHERE 	p.`idAirline` = input_idAirline
			AND p.`idTemplate` = input_idTemplate;
	-- Handler declarations
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN pCursor;

	periodLoop : LOOP
		SET done = FALSE;
		FETCH pCursor INTO my_idPeriod;
		IF done THEN
			LEAVE periodLoop;
		END IF;
		
		CALL interval_between(
			GREATEST(
				(SELECT `fromDate` FROM `period`
					WHERE `idPeriod` = my_idPeriod),
				input_selectFrom),
			IFNULL(
				LEAST(
					(SELECT `toDate` FROM `period`
						WHERE `idPeriod` = my_idPeriod),
					input_selectTo),
				input_selectTo
			),
			'DAY',1
		);
		
		SET @num = -1;
		DROP TABLE IF EXISTS dates_to_insert;
		CREATE TEMPORARY TABLE dates_to_insert AS (
			SELECT
				@num := @num + 1 as rowNr,
				DATE(interval_from) as missingDate
				FROM time_intervals as datesTable JOIN period as p ON p.idPeriod = my_idPeriod
					WHERE NOT EXISTS (
						SELECT * FROM periodgenerated as pg
						WHERE pg.generatedDate = datesTable.interval_from
							AND pg.idPeriod = my_idPeriod
					) AND IF((p.`day`) IS NULL,
						TRUE,
						(SELECT DAYOFWEEK(DATE(interval_from)) = p.`day`)
						# 0 and 1 are equivalent to FALSE and TRUE in MySQL
						# IF ((SELECT DAYOFWEEK(DATE(interval_from)) = p.`day` = '1'), TRUE, FALSE)
					)
		);
		
		#SET @airline = input_idAirline;
		#SET @template = input_idTemplate;
		INSERT INTO flight(`idAirline`, `idTemplate`)
			SELECT input_idAirline, input_idTemplate FROM `dates_to_insert`;
		
		INSERT INTO periodgenerated(`idPeriod`,`generatedDate`,`idFlight`)
			SELECT
				my_idPeriod,
				missingDate,
				LAST_INSERT_ID() + rowNr
			FROM dates_to_insert;
		
	END LOOP;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- View `mydb`.`ActualFlight`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `mydb`.`ActualFlight` ;
DROP TABLE IF EXISTS `mydb`.`ActualFlight`;
USE `mydb`;
CREATE  OR REPLACE VIEW `mydb`.`ActualFlight` AS
SELECT
	F.`idFlight` as idFlight,
	IFNULL(F.`idAirplaneType`,T.`idairplaneType`) as idAirplaneType,
	PG.`idPeriod` as idPeriod,
	F.`idAirline` as idAirpline,
	F.`idTemplate` as idTemplate,
	IFNULL(
		F.`departure`,
		CONCAT(PG.`generatedDate`, ' ',P.`flightStartTime`)) as departure,
	IFNULL(
		F.`arrival`,
		ADDTIME(
			IFNULL(
				F.`departure`,
				CONCAT(PG.`generatedDate`, ' ',P.`flightStartTime`)
			),
			FT.`duration`
		)
	) as arrival
FROM
	Flight AS F
	INNER JOIN Template AS T
		ON F.`idAirline` = T.`idAirline` AND F.`idTemplate` = T.`idTemplate`
	LEFT OUTER JOIN PeriodGenerated AS PG
		ON F.`idFlight` = PG.`idFlight`
	LEFT OUTER JOIN Period AS P
		ON PG.`idPeriod` = P.`idPeriod`
	LEFT OUTER JOIN FlightTime AS FT
		ON T.`idAirportFrom` = FT.`idFromAirport`
			AND T.`idAirportTo` = FT.`idToAirport`
			AND T.`idAirplaneType` = FT.`idAirplaneType`;

-- -----------------------------------------------------
-- View `mydb`.`ActualSeatInstances`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `mydb`.`ActualSeatInstances` ;
DROP TABLE IF EXISTS `mydb`.`ActualSeatInstances`;
USE `mydb`;
CREATE  OR REPLACE VIEW `mydb`.`ActualSeatInstances` AS
SELECT
	f.`idFlight` AS idFlight,
	s.`seatNumber` AS seatNumber,
	apt.`idAirplaneType` AS idAirplaneType,
	IFNULL(
		(SELECT `price`
		FROM seatinstance AS si
		WHERE si.`idFlight` = f.`idFlight`
			AND si.`seatNumber` = s.`seatNumber`)
		,
		(SELECT `price`
		FROM seatinstancetemplate as sit
		WHERE sit.`idAirline` = f.`idAirline`
			AND sit.`idTemplate` = f.`idTemplate`
			AND sit.`seatNumber` = s.`seatNumber`)
	) as price
FROM flight as f
JOIN template as t
	ON f.`idAirline` = t.`idAirline`
	AND f.`idTemplate` = t.`idTemplate`
JOIN airplaneType AS apt
	ON	apt.`idAirplaneType` = IFNULL(f.`idAirplaneType`, t.`idAirplaneType`)
JOIN seat AS s
	on s.`idAirplaneType` = apt.`idAirplaneType`;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
