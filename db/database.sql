SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `airyscript` ;
CREATE SCHEMA IF NOT EXISTS `airyscript` DEFAULT CHARACTER SET utf8 ;
USE `airyscript` ;

-- -----------------------------------------------------
-- Table `airyscript`.`AirplaneType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`AirplaneType` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`AirplaneType` (
  `idAirplaneType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirplaneType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `airyscript`.`AirplaneType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`SeatType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`SeatType` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`SeatType` (
  `idSeatType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idSeatType`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `airyscript`.`SeatType` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Seat`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Seat` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Seat` (
  `idSeatType` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `seatNumber` INT NOT NULL ,
  PRIMARY KEY (`idAirplaneType`, `seatNumber`) ,
  CONSTRAINT `seat_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `airyscript`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `seat_seatType_fk`
    FOREIGN KEY (`idSeatType` )
    REFERENCES `airyscript`.`SeatType` (`idSeatType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `type_idx` ON `airyscript`.`Seat` (`idSeatType` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Airline`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Airline` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Airline` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirline`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `airyscript`.`City`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`City` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`City` (
  `idCity` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idCity`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `airyscript`.`City` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Airport`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Airport` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Airport` (
  `idAirport` VARCHAR(3) NOT NULL ,
  `idCity` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idAirport`) ,
  CONSTRAINT `airport_city_fk`
    FOREIGN KEY (`idCity` )
    REFERENCES `airyscript`.`City` (`idCity` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `city_idx` ON `airyscript`.`Airport` (`idCity` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Template`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Template` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Template` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `idAirportFrom` VARCHAR(3) NOT NULL ,
  `idAirportTo` VARCHAR(3) NOT NULL ,
  PRIMARY KEY (`idAirline`, `idTemplate`) ,
  CONSTRAINT `template_airlineCompany_fk`
    FOREIGN KEY (`idAirline` )
    REFERENCES `airyscript`.`Airline` (`idAirline` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportFrom_fk`
    FOREIGN KEY (`idAirportFrom` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airportTo_fk`
    FOREIGN KEY (`idAirportTo` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `template_airplanetype_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `airyscript`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirlineCompany_idx` ON `airyscript`.`Template` (`idAirline` ASC) ;

CREATE INDEX `airportFrom_idx` ON `airyscript`.`Template` (`idAirportFrom` ASC) ;

CREATE INDEX `airportTo_idx` ON `airyscript`.`Template` (`idAirportTo` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Flight`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Flight` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Flight` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `idAirplaneType` INT NULL ,
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `departure` DATETIME NULL ,
  `arrival` DATETIME NULL ,
  PRIMARY KEY (`idFlight`) ,
  CONSTRAINT `flight_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `airyscript`.`AirplaneType` (`idAirplaneType` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `flight_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `airyscript`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `AirplaneType_idx` ON `airyscript`.`Flight` (`idAirplaneType` ASC) ;

CREATE INDEX `Template_idx` ON `airyscript`.`Flight` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`SeatInstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`SeatInstance` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`SeatInstance` (
  `idFlight` INT NOT NULL AUTO_INCREMENT ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFlight`, `seatNumber`) ,
  CONSTRAINT `seatinstance_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `airyscript`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstance_flight_fk`
    FOREIGN KEY (`idFlight` )
    REFERENCES `airyscript`.`Flight` (`idFlight` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `bookingslot_seat_fk_idx` ON `airyscript`.`SeatInstance` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstance_flight_fk_idx` ON `airyscript`.`SeatInstance` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Period`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Period` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Period` (
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
    REFERENCES `airyscript`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `flightTemplate_idx` ON `airyscript`.`Period` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`Distance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`Distance` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`Distance` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `distance` DOUBLE NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`) ,
  CONSTRAINT `distance_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `distance_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `airyscript`.`Distance` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `airyscript`.`Distance` (`idToAirport` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`FlightTime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`FlightTime` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`FlightTime` (
  `idFromAirport` VARCHAR(3) NOT NULL ,
  `idToAirport` VARCHAR(3) NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `duration` TIME NOT NULL ,
  PRIMARY KEY (`idFromAirport`, `idToAirport`, `idAirplaneType`) ,
  CONSTRAINT `flightTime_fromAirport_fk`
    FOREIGN KEY (`idFromAirport` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_toAirport_fk`
    FOREIGN KEY (`idToAirport` )
    REFERENCES `airyscript`.`Airport` (`idAirport` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `flightTime_airplaneType_fk`
    FOREIGN KEY (`idAirplaneType` )
    REFERENCES `airyscript`.`AirplaneType` (`idAirplaneType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fromAirport_idx` ON `airyscript`.`FlightTime` (`idFromAirport` ASC) ;

CREATE INDEX `toAirport_idx` ON `airyscript`.`FlightTime` (`idToAirport` ASC) ;

CREATE INDEX `airplaneType_idx` ON `airyscript`.`FlightTime` (`idAirplaneType` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`SeatInstanceTemplate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`SeatInstanceTemplate` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`SeatInstanceTemplate` (
  `idAirline` VARCHAR(3) NOT NULL ,
  `idTemplate` DECIMAL(4,0) NOT NULL ,
  `seatNumber` INT NOT NULL ,
  `idAirplaneType` INT NOT NULL ,
  `price` DOUBLE NOT NULL ,
  PRIMARY KEY (`seatNumber`, `idAirline`, `idTemplate`) ,
  CONSTRAINT `seatinstancetemplate_seat_fk`
    FOREIGN KEY (`idAirplaneType` , `seatNumber` )
    REFERENCES `airyscript`.`Seat` (`idAirplaneType` , `seatNumber` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `seatinstancetemplate_template_fk`
    FOREIGN KEY (`idAirline` , `idTemplate` )
    REFERENCES `airyscript`.`Template` (`idAirline` , `idTemplate` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `seatinstancetemplate_seat_fk_idx` ON `airyscript`.`SeatInstanceTemplate` (`idAirplaneType` ASC, `seatNumber` ASC) ;

CREATE INDEX `seatinstancetemplate_template_fk_idx` ON `airyscript`.`SeatInstanceTemplate` (`idAirline` ASC, `idTemplate` ASC) ;


-- -----------------------------------------------------
-- Table `airyscript`.`PeriodGenerated`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `airyscript`.`PeriodGenerated` ;

CREATE  TABLE IF NOT EXISTS `airyscript`.`PeriodGenerated` (
  `idPeriod` INT NOT NULL ,
  `generatedDate` DATE NOT NULL ,
  `idFlight` INT NULL ,
  PRIMARY KEY (`idPeriod`, `generatedDate`) ,
  CONSTRAINT `fk_periodgenerated_flight`
    FOREIGN KEY (`idFlight` )
    REFERENCES `airyscript`.`Flight` (`idFlight` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_periodgenerated_period`
    FOREIGN KEY (`idPeriod` )
    REFERENCES `airyscript`.`Period` (`idPeriod` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_periodgenerated_flight_idx` ON `airyscript`.`PeriodGenerated` (`idFlight` ASC) ;

CREATE INDEX `fk_periodgenerated_period_idx` ON `airyscript`.`PeriodGenerated` (`idPeriod` ASC) ;

CREATE UNIQUE INDEX `idFlight_UNIQUE` ON `airyscript`.`PeriodGenerated` (`idFlight` ASC) ;


-- -----------------------------------------------------
-- Placeholder table for view `airyscript`.`ActualFlight`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airyscript`.`ActualFlight` (`idFlight` INT, `idAirplaneType` INT, `idPeriod` INT, `idAirpline` INT, `idTemplate` INT, `departure` INT, `arrival` INT);

-- -----------------------------------------------------
-- Placeholder table for view `airyscript`.`ActualSeatInstances`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `airyscript`.`ActualSeatInstances` (`idFlight` INT, `idTemplate` INT, `idAirplaneType` INT, `seatNumber` INT, `idSeatType` INT, `seatTypeName` INT, `price` INT);

-- -----------------------------------------------------
-- procedure generate_template_flights
-- -----------------------------------------------------

USE `airyscript`;
DROP procedure IF EXISTS `airyscript`.`generate_template_flights`;

DELIMITER $$
USE `airyscript`$$


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
-- procedure update_seat_instance_templates
-- -----------------------------------------------------

USE `airyscript`;
DROP procedure IF EXISTS `airyscript`.`update_seat_instance_templates`;

DELIMITER $$
USE `airyscript`$$
CREATE PROCEDURE update_seat_instance_templates(
	IN input_idAirline VARCHAR(3),
	IN input_idTemplate DECIMAL(4,0))
BEGIN
	-- Variable declarations
	DECLARE my_seatType INT;
	DECLARE my_done INT DEFAULT FALSE;

		-- Cursor declarions
	DECLARE pCursor CURSOR FOR (SELECT * FROM seattypes_to_process);
			
	-- Handler declarations
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET my_done = TRUE;
	
	DROP TABLE IF EXISTS seattypes_to_process;
	CREATE TEMPORARY TABLE seattypes_to_process AS (
		SELECT DISTINCT s.`idSeatType`
		FROM seat as s
			INNER JOIN template as t ON
				s.`idAirplaneType` = t.`idAirplaneType`
				AND t.`idAirline` = input_idAirline
				AND t.`idTemplate` = input_idTemplate);

	SELECT t.`idAirplaneType` INTO @newAirplaneType
	FROM template as t
	WHERE t.`idAirline` = input_idAirline
		AND t.`idTemplate` = input_idTemplate;

	SELECT sit.`idAirplaneType` INTO @oldAirplaneType
	FROM seatinstancetemplate as sit
	WHERE
		sit.`idAirline` = input_idAirline
		AND sit.`idTemplate` = input_idTemplate
	LIMIT 1;

	SELECT sit.`price` INTO @lowestPrice
	FROM seatinstancetemplate as sit
	WHERE
		sit.`idAirline` = input_idAirline
		AND sit.`idTemplate` = input_idTemplate
	ORDER BY sit.`price` ASC
	LIMIT 1;
	
	#-INNER JOIN seatinstancetemplate as sit
	#	ON 

	OPEN pCursor;

	-- Delete all seat instances of seats that are on the old airplane type
	-- but not on the new airplane type.
	DELETE sit1 FROM seatinstancetemplate AS sit1
	INNER JOIN seatinstancetemplate AS sit2 ON
		sit1.`idAirline` = sit2.`idAirline` AND
		sit1.`idTemplate` = sit2.`idTemplate`
	WHERE sit2.`idAirline` = input_idAirline
		AND sit2.`idTemplate` = input_idTemplate
		AND NOT EXISTS (
			SELECT *
			FROM Seat as s1
			INNER JOIN Seat as s2
				ON s2.`idAirplanetype` = @newAirplaneType
				AND s1.`idSeatType` = s2.`idSeatType`
			WHERE s1.`idAirplaneType` = sit2.`idAirplaneType`
		);

	my_loop : LOOP
		SET my_done = FALSE;
		FETCH pCursor INTO my_seatType;
		IF my_done THEN
			LEAVE my_loop;
		END IF;
		
		SELECT sit.price INTO @price
		FROM seatinstancetemplate AS sit
		INNER JOIN seat as s
			ON s.`seatNumber` = sit.`seatNumber`
			AND s.`idAirplaneType` = sit.`idAirplaneType`
			AND s.`idSeatType` = my_seatType
		WHERE
			sit.`idAirline` = input_idAirline AND
			sit.`idTemplate` = input_idTemplate
		ORDER BY sit.`seatNumber` ASC
		LIMIT 1;
 		
		-- The default price is free
 		SELECT IFNULL(@price,@lowestPrice) INTO @price;
		

		DELETE sit FROM seatinstancetemplate as sit
		WHERE sit.`idAirline` = input_idAirline
			AND sit.`idTemplate` = input_idTemplate
			AND sit.`seatNumber` IN(
				SELECT s.`seatNumber`
				FROM seat as s
				WHERE s.`idSeatType` = my_seatType
			);
-- 
		INSERT INTO seatinstancetemplate(
			`idAirline`,
			`idTemplate`,
			`seatNumber`,
			`idAirplaneType`,
			`price`)
		SELECT
			input_idAirline,
			input_idTemplate,
			s.`seatNumber`,
			@newAirplaneType,
			@price
		FROM seat AS s
		WHERE s.idAirplaneType = @newAirplaneType
			AND s.`idSeatType` = my_seatType;

	END LOOP;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure interval_between
-- -----------------------------------------------------

USE `airyscript`;
DROP procedure IF EXISTS `airyscript`.`interval_between`;

DELIMITER $$
USE `airyscript`$$
CREATE PROCEDURE interval_between(startdate TIMESTAMP, enddate TIMESTAMP, interval_size VARCHAR(10), interval_value INT)
BEGIN
	
END$$

DELIMITER ;

-- -----------------------------------------------------
-- View `airyscript`.`ActualFlight`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `airyscript`.`ActualFlight` ;
DROP TABLE IF EXISTS `airyscript`.`ActualFlight`;
USE `airyscript`;
CREATE  OR REPLACE VIEW `airyscript`.`ActualFlight` AS
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
-- View `airyscript`.`ActualSeatInstances`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `airyscript`.`ActualSeatInstances` ;
DROP TABLE IF EXISTS `airyscript`.`ActualSeatInstances`;
USE `airyscript`;
CREATE  OR REPLACE VIEW `airyscript`.`ActualSeatInstances` AS
SELECT
	f.`idFlight` AS idFlight,
	t.`idTemplate` AS idTemplate,
	IFNULL(f.`idAirplaneType`,
		t.`idAirplaneType`) AS idAirplaneType,
	s.`seatNumber` AS seatNumber,
	s.`idSeatType` AS idSeatType,
	st.`name` AS seatTypeName,
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
	on s.`idAirplaneType` = apt.`idAirplaneType`
JOIN seattype AS st
	on s.`idSeatType` = st.`idSeatType`;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
