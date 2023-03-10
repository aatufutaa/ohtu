-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema vn
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `vn` ;

-- -----------------------------------------------------
-- Schema vn
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `vn` DEFAULT CHARACTER SET utf8 ;
USE `vn` ;

-- -----------------------------------------------------
-- Table `vn`.`alue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`alue` ;

CREATE TABLE IF NOT EXISTS `vn`.`alue` (
  `alue_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nimi` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`alue_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `alue_nimi_index` ON `vn`.`alue` (`nimi` ASC) VISIBLE;

-- -----------------------------------------------------
-- Table `vn`.`asiakas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`asiakas` ;

CREATE TABLE IF NOT EXISTS `vn`.`asiakas` (
  `asiakas_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `postinro` CHAR(5) NOT NULL,
  `etunimi` VARCHAR(20) NULL DEFAULT NULL,
  `sukunimi` VARCHAR(40) NULL DEFAULT NULL,
  `lahiosoite` VARCHAR(40) NULL DEFAULT NULL,
  `paikkakunta` VARCHAR(40) NULL DEFAULT NULL,
  `email` VARCHAR(50) NULL DEFAULT NULL,
  `puhelinnro` VARCHAR(15) NULL DEFAULT NULL,
  PRIMARY KEY (`asiakas_id`))

ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `fk_as_posti1_idx` ON `vn`.`asiakas` (`postinro` ASC) VISIBLE;

CREATE INDEX `asiakas_snimi_idx` ON `vn`.`asiakas` (`sukunimi` ASC) VISIBLE;

CREATE INDEX `asiakas_enimi_idx` ON `vn`.`asiakas` (`etunimi` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `vn`.`mokki`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`mokki` ;

CREATE TABLE IF NOT EXISTS `vn`.`mokki` (
  `mokki_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `alue_id` INT UNSIGNED NOT NULL,
  `postinro` CHAR(5) NOT NULL,
  `mokkinimi` VARCHAR(45) NULL DEFAULT NULL,
  `katuosoite` VARCHAR(45) NULL DEFAULT NULL,
  `hinta` DOUBLE(8,2) NOT NULL,
  `kuvaus` VARCHAR(150) NULL DEFAULT NULL,
  `henkilomaara` INT NULL DEFAULT NULL,
  `varustelu` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`mokki_id`),
  CONSTRAINT `fk_mokki_alue`
    FOREIGN KEY (`alue_id`)
    REFERENCES `vn`.`alue` (`alue_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `fk_mokki_alue_idx` ON `vn`.`mokki` (`alue_id` ASC) VISIBLE;

CREATE INDEX `fk_mokki_posti_idx` ON `vn`.`mokki` (`postinro` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `vn`.`varaus`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`varaus` ;

CREATE TABLE IF NOT EXISTS `vn`.`varaus` (
  `varaus_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `asiakas_id` INT UNSIGNED NOT NULL,
  `mokki_mokki_id` INT UNSIGNED NOT NULL,
  `varattu_pvm` DATETIME NULL DEFAULT NULL,
  `vahvistus_pvm` DATETIME NULL DEFAULT NULL,
  `varattu_alkupvm` DATETIME NULL DEFAULT NULL,
  `varattu_loppupvm` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`varaus_id`),
  CONSTRAINT `fk_varaus_mokki`
    FOREIGN KEY (`mokki_mokki_id`)
    REFERENCES `vn`.`mokki` (`mokki_id`),
  CONSTRAINT `varaus_ibfk`
    FOREIGN KEY (`asiakas_id`)
    REFERENCES `vn`.`asiakas` (`asiakas_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `varaus_as_id_index` ON `vn`.`varaus` (`asiakas_id` ASC) VISIBLE;

CREATE INDEX `fk_var_mok_idx` ON `vn`.`varaus` (`mokki_mokki_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `vn`.`lasku`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`lasku` ;

CREATE TABLE IF NOT EXISTS `vn`.`lasku` (
  `lasku_id` INT NOT NULL AUTO_INCREMENT,
  `varaus_id` INT UNSIGNED NOT NULL,
  `summa` DOUBLE(8,2) NOT NULL,
  `alv` DOUBLE(8,2) NOT NULL,
  `maksettu` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`lasku_id`),
  CONSTRAINT `lasku_ibfk_1`
    FOREIGN KEY (`varaus_id`)
    REFERENCES `vn`.`varaus` (`varaus_id`)
    ON DELETE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `lasku_ibfk_1` ON `vn`.`lasku` (`varaus_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `vn`.`palvelu`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`palvelu` ;

CREATE TABLE IF NOT EXISTS `vn`.`palvelu` (
  `palvelu_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `alue_id` INT UNSIGNED NOT NULL,
  `nimi` VARCHAR(40) NULL DEFAULT NULL,
  `kuvaus` VARCHAR(255) NULL DEFAULT NULL,
  `hinta` DOUBLE(8,2) NOT NULL,
  PRIMARY KEY (`palvelu_id`),
  CONSTRAINT `palvelu_ibfk_1`
    FOREIGN KEY (`alue_id`)
    REFERENCES `vn`.`alue` (`alue_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `Palvelu_nimi_index` ON `vn`.`palvelu` (`nimi` ASC) VISIBLE;

CREATE INDEX `palv_toimip_id_ind` ON `vn`.`palvelu` (`alue_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `vn`.`varauksen_palvelut`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vn`.`varauksen_palvelut` ;

CREATE TABLE IF NOT EXISTS `vn`.`varauksen_palvelut` (
  `varaus_id` INT UNSIGNED NOT NULL,
  `palvelu_id` INT UNSIGNED NOT NULL,
  `lkm` INT NOT NULL,
  PRIMARY KEY (`palvelu_id`, `varaus_id`),
  CONSTRAINT `fk_palvelu`
    FOREIGN KEY (`palvelu_id`)
    REFERENCES `vn`.`palvelu` (`palvelu_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_varaus`
    FOREIGN KEY (`varaus_id`)
    REFERENCES `vn`.`varaus` (`varaus_id`)
    ON DELETE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `vp_varaus_id_index` ON `vn`.`varauksen_palvelut` (`varaus_id` ASC) VISIBLE;

CREATE INDEX `vp_palvelu_id_index` ON `vn`.`varauksen_palvelut` (`palvelu_id` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `vn`.`alue`
-- -----------------------------------------------------
START TRANSACTION;
USE `vn`;
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (1, 'Tahko');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (2, 'Levi');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (3, 'Ruka');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (4, 'Himos');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (5, 'Kuopio');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (6, 'Mikkeli');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (7, 'Parainen');
INSERT INTO `vn`.`alue` (`alue_id`, `nimi`) VALUES (8, 'Lohja');

COMMIT;

-- -----------------------------------------------------
-- Data for table `vn`.`asiakas`
-- -----------------------------------------------------
START TRANSACTION;
USE `vn`;
INSERT INTO `vn`.`asiakas` (`postinro`, `etunimi`, `sukunimi`, `lahiosoite`, `paikkakunta`, `email`, `puhelinnro`) VALUES ("12345", 'Matti', 'Muikku', 'Kotitie 5', "Hyvink????", 'matti.muiikku@gmail.com', '0441122333');

COMMIT;

-- -----------------------------------------------------
-- Data for table `vn`.`mokki`
-- -----------------------------------------------------
START TRANSACTION;
USE `vn`;
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Huvila', 'Aamuruskontie 1', 500, 'Rantam??kki ', 6, 's??hk??, juokseva vesi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Honka', 'Honkatie 5', 1200, 'Hirsim??kki terassilla', 12, 's??hk??, juokseva vesi, takka, sis??-wc, rantasauna, ');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Presidentti', 'Golftie 2', 3000, 'Honkam??kki golfkent??n v??litt??m??ss?? l??heisyydess??', 20, 's??hk??, takka, grilli, juokseva vesi, valokuitu, palju');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Havu', 'Honkatie 10', 1000, 'Hirsim??kki terassilla', 8, 's??hk??, juokseva vesi, sis??-wc, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'M??nty', 'Aamuruskontie 2', 400, 'Puum??kki 200m:n p????ss?? rannasta', 6, 's??hk??, juokseva vesi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Pet??j??', 'J??k??l??tie 22', 700, 'Moderni vapaa-ajanasunto laskettelurinteiden kupeessa', 8, 's??hk??, juokseva vesi, sis??-wc, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Piltti', 'Katajakuja 7', 600, 'Lapsiperheille suunnattu hirsim??kki, sis??lt???? sy??tt??tuolin ja h??kkis??ngyn', 6, 's??hk??, juokseva vesi, grilli ');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'N??re', 'Klubitietie 1', 2000, 'Bilehileille suunnattu, palveluiden v??litt??m??ss?? l??heisyydess?? sijaitseva bilem??kki', 16, 's??hk??, juokseva vesi, sauna, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Hovi', 'Kultapallo 4', 1300, 'Pariskunnille tarkoitettu, romanttiseen viikonloppuun varattu, palveluiden l??heisyydess?? sijaitseva m??kki ', 6, 's??hk??, juokseva vesi, takka, grilli, sauna, palju, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (1, '73310', 'Kelo', 'Honkatie 7', 300, 'Budjettiratkaisu pienryhmille', 4, 's??hk??, juokseva vesi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Poro', 'Poropolku 9', 600, 'M??kki golfkent??n ja laskettelurinteiden l??hell??', 7, 's??hk??, vesi, sauna, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Helmi', 'Pikkurakka 15', 700, 'Hirsim??kki', 5, 's??hk??, vesi, wifi, takka');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Sompio', 'Sompiontie 2', 300, 'Pieni m??kki rauhallisella paikalla', 4, 's??hk??, vesi, sauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Kaarna', 'Immeltie 7', 1000, 'M??kki j??rven rannalla', 8, 's??hk??, vesi, wifi, takka, sauna, grillikatos');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Tunturihuvila', 'Tolvatie 10', 1400, 'Valoisa m??kki rauhallisella paikalla', 6, 's??hk??, vesi, takka, sauna, poreamme');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (2, '99130', 'Mets??l??', 'Koutaniementie 1', 1500, 'Tilava m??kki palvelujen l??hell??', 14, 's??hk??, vesi, wifi, takka, ulkosauna, oma ranta, vene');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Rukankarhu A', 'Saukkotie 10A', 596, 'N??ytt??v?? ja moderni hirsiparitalohuoneisto', 8, 's??hk??, vesi, lapsivarustus, sauna, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Rukankarhu B', 'Saukkotie 10B', 536, 'Tyylikk????sti sisustettu ja avara huoneisto', 8, 's??hk??, vesi, lapsivarustus, sauna, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Otsontupa', 'Ukkoherrantie 10 A3', 328, 'Tunnelmallinen ja uusittu kelotalon p????tyhuoneisto.', 8, 's??hk??, vesi, lapsivarustus, sauna, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Rukantrio A', 'Pyrypolku 3 A', 328, 'Uudehko moderni huoneisto.', 7, 's??hk?? vesi, lapsivarustus, ei lemmikkej??');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Hippu', 'Hiiht??j??ntie 3B', 510, 'Huippupaikalla sijaitseva paritalon p????tyhuoneisto.', 6, 's??hk??, vesi, lapsivarustus, lemmikit sallittu');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (3, '93830', 'Hippu', 'Hiiht??j??ntie 3B', 510, 'Huippupaikalla sijaitseva paritalon p????tyhuoneisto.', 6, 's??hk??, vesi, lapsivarustus, lemmikit sallittu');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (4, '42100', 'Ameno 3', 'Haapanentie 14 B', 643, 'Puurakenteinen kaksikerroksinen paritalohuoneisto', 7, 's??hk??sauna, 2xWC, induktioliesi, astianpesukone');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (4, '42100', 'Villa Nummenranta 1', 'Joutsenentie 5 F', 617, 'Tasokas kaksikerroksinen huvila', 8, 'ilmal??mp??pumppu, s??hk??sauna, induktioliesi, yksitt??inen m??kki');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (4, '42100', 'Himosputti 1', 'Hiiht??j??ntie 24 A', 477, 'Lapsiperheille suunnattu huvila', 4+2, 's??hk??sauna, 1xWC, kamiina, sy??tt??tuoli');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (4, '42100', 'Peltsinranta', 'Jyv??skyl??ntie 808', 281, 'Pieni omarantainen m??kki Himoksen alueella', 1+5, 'puusauna, 1xWC, s??hk??l??mmitys, kaasugrilli, palju vuokrattavissa');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (4, '42100', 'Akavilla 2', 'Himosrannantie 12', 453, 'Hyvin varusteltu kodikas m??kki', 2+6, 's??hk??sauna, 2xWC, 4 l??mmityspaikkaa, wifi, varaava takka');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (5, '73300', 'Koski', 'Tervaniementie 12', 1000, 'Hirsim??kki kosken rannalla', 8, 's??hk??, juokseva vesi, savusauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (5, '73300', 'Kuusisto', 'Tervaniementie 24', 800, 'Mummonm??kki kuusikossa, katseilta piilossa', 6, 's??hk??, vesi, sauna, wc');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (5, '73300', 'P??kkel??', 'Kuutamokuja 7', 500, 'Pikkum??kki sopuhintaan sopivalla et??isyydell?? palveluista', 4, 's??hk??, kaivo, savusauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (5, '73300', 'Kataja', 'K??rritie 2', 1200, 'Tilava, yritysten virkistysk??ytt????n tarkoitettu hirsim??kki', 18, 's??hk??, vesi, wifi, sauna, wc, palju, pingisp??yt??');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (5, '73300', 'Lepp??', 'M??kkitie 5', 900, 'Peruskorjattu hirsim??kki j??rven rannalla', 8, 's??hk??, vesi, rantasauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (6, '50350', 'Maria', 'H??rk??niementie 22', 800, 'T??ysin varusteltu huvila kaikilla mukavuuksilla j??rven rannalla', 10, 's??hk??, vesi, rantasauna, wc, vene, palju, wifi');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (6, '50350', 'Jussi', 'M??kkitie 9', 2000, 'Tilava hirsim??kki j??rven rannalla', 20, 's??hk??, vesi, sauna, wc, wifi, palju, satelliitti-tv');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (6, '50350', 'Anna', 'M??kkitie 17', 1200, 'Lemmikeillekin sopiva m??kki rauhallisella paikalla', 8, 's??hk??, vesi, sauna, palju');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (6, '50350', 'Risto', 'Visulahdentie 12', 1000, 'Visulahden l??hell?? sijaiitseva hirsim??kki j??rven rannalla ', 10, 's??hk??, vesi, wifi, wc, rantasauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (6, '50350', 'Emilia', 'Angervonkuja 6', 1300, 'Palveluiden l??heisyydess?? sijaitseva hirsim??kki', 8, 's??hk??, vesi, wc, sauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (7, '21600', 'Rantala', 'Rompontie 4', 700, 'M??kki vesist??n l??hell?? omalla rannalla', 6, 's??hk??, vesi, wifi, sauna, takka, grilli, vene');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (7, '21600', 'Kalasatama', 'Kalastajankuja 16', 400, 'Lapsiperheelle sopiva ratkaisu palvelujen l??hell??', 4, 's??hk??, vesi, wifi, lemmikit sallittu');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (7, '21600', 'Haukela', 'Kalastajankuja 3', 800, 'Tilava m??kki runsaalla varustelulla palvelujen l??hell??', 12, 's??hk??, vesi, wifi, sauna, takka, tv, parvi, poreamme, terassi, oma ranta, vene, grilli');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (7, '21600', 'S??rkel??', 'Saaristotie 1500', 1000, 'Moderni m??kki liikuntapaikkojen l??hell??', 6, 's??hk??, vesi, wifi, sauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (7, '21600', 'Lohela', 'Varustajantie 1', 500, 'M??kki uimarannan l??hell??', 8, 's??hk??, vesi, grilli, sauna, lemmikit sallittu');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (8, '08100', 'Kallio', 'Haikarinkatu 30', 500, 'M??kki Liessaaren luontopolkujen vieress??', 6, 's??hk??, vesi, sauna');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (8, '08100', 'Pikku-Tupa', 'Lehmij??rventie 2', 400, 'Frisbeegolfradan yhteydess?? pariskunnalle sopiva hirsim??kki', 3, 's??hk??, vesi, sauna, parveke');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (8, '08100', 'Iso-Tupa', 'Hietakuja 1', 800, 'M??kki k??velymatkan p????ss?? uimarannalta', 10, 's??hk??, vesi, wifi, tv, lemmikit sallittu');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (8, '08100', 'Lohjan Pirtti', 'B??cksv??gen 5', 600, 'M??kki golfkent??n l??hell??', 6, 's??hk??, vesi, wifi, tv, grilli');
INSERT INTO `vn`.`mokki` (`alue_id`, `postinro`, `mokkinimi`, `katuosoite`, `hinta`, `kuvaus`, `henkilomaara`, `varustelu`) VALUES (8, '08100', 'Lohjan Torppa', 'Huvilatie 9', 1200, 'M??kki rauhallisella alueella', 8, 's??hk??, vesi, wifi, sauna, tv, lemmikit sallittu');
COMMIT;

-- -----------------------------------------------------
-- Data for table `vn`.`palvelu`
-- -----------------------------------------------------
START TRANSACTION;
USE `vn`;
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (1, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (2, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (3, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (4, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (5, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (6, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (7, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'moottorikelkka', 'Moottorikelkan vuokraus', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'paintball', 'Paintball-varusteiden vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'polkupy??r??', 'Polkupy??r??n vuokraus', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'porosafari', 'Porosafari', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'koiravaljakkoajo', 'Koiravaljakkoajelu', 50);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'airsoft', 'Airsoftausta', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'hevosajelu', 'Hevosajelu', 40);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'vesiskootteri', 'Vesiskootterit vuokrattavissa', 60);
INSERT INTO `vn`.`palvelu` (`alue_id`, `nimi`, `kuvaus`, `hinta`) VALUES (8, 'siivous', 'M??kin l??ht??siivous asiakkaan puolesta', 40);
COMMIT;