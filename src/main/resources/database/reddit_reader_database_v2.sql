-- MySQL Script generated by MySQL Workbench
-- Fri Mar 13 12:34:45 2020
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema reddit_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `reddit_db` ;

-- -----------------------------------------------------
-- Schema reddit_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reddit_db` DEFAULT CHARACTER SET utf8mb4 ;
USE `reddit_db` ;

-- -----------------------------------------------------
-- Table `reddit_db`.`url`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`url` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`url` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `complete` TEXT NOT NULL,
  `protocol` VARCHAR(254) NULL,
  `subdomain` VARCHAR(254) NULL,
  `domain` VARCHAR(254) NULL,
  `port` INT NULL,
  `path` VARCHAR(1000) NULL,
  `parameters` VARCHAR(2500) NULL,
  `fragment` VARCHAR(254) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`subreddit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`subreddit` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`subreddit` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `external_id` VARCHAR(254) NOT NULL COMMENT '\'fullName\'',
  `name` VARCHAR(254) NOT NULL,
  `type` VARCHAR(5) NULL,
  `title` TEXT NULL,
  `description` TEXT NULL,
  `sidebar_description` TEXT NULL,
  `created` DATETIME NULL,
  `permalink` TEXT NULL COMMENT '\'url\'',
  `subscribed_users` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `external_id_UNIQUE` (`external_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`user` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(254) NOT NULL,
  `created` DATETIME NULL,
  `is_moderator` TINYINT NULL,
  `has_verified_email` TINYINT NULL,
  `comment_karma` INT NULL,
  `link_karma` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`username` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`submission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`submission` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`submission` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `subreddit_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `external_id` VARCHAR(254) NOT NULL COMMENT '\'fullName\'',
  `type` VARCHAR(5) NULL,
  `title` TEXT NULL,
  `text` TEXT NULL,
  `created` DATETIME NULL,
  `edited` DATETIME NULL,
  `permalink` TEXT NULL,
  `score` INT NULL,
  `comment_count` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `external_id_UNIQUE` (`external_id` ASC),
  INDEX `fk_submission_subreddit_idx` (`subreddit_id` ASC),
  INDEX `fk_submission_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_submission_subreddit`
    FOREIGN KEY (`subreddit_id`)
    REFERENCES `reddit_db`.`subreddit` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_submission_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reddit_db`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`comment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`comment` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `submission_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `comment_id` INT NULL COMMENT 'replies',
  `external_id` VARCHAR(254) NOT NULL COMMENT '\'fullName\'',
  `type` VARCHAR(5) NULL,
  `text` TEXT NULL COMMENT '\'body\'',
  `created` DATETIME NULL,
  `edited` DATETIME NULL,
  `depth` INT NULL,
  `score` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `external_id_UNIQUE` (`external_id` ASC),
  INDEX `fk_comment_submission1_idx` (`submission_id` ASC),
  INDEX `fk_comment_user1_idx` (`user_id` ASC),
  INDEX `fk_comment_comment1_idx` (`comment_id` ASC),
  CONSTRAINT `fk_comment_submission1`
    FOREIGN KEY (`submission_id`)
    REFERENCES `reddit_db`.`submission` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reddit_db`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_comment1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `reddit_db`.`comment` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`subreddit_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`subreddit_user` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`subreddit_user` (
  `subreddit_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`subreddit_id`, `user_id`),
  INDEX `fk_subreddit_user_user1_idx` (`user_id` ASC),
  INDEX `fk_subreddit_user_subreddit1_idx` (`subreddit_id` ASC),
  CONSTRAINT `fk_subreddit_user_subreddit1`
    FOREIGN KEY (`subreddit_id`)
    REFERENCES `reddit_db`.`subreddit` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_subreddit_user_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reddit_db`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`submission_url`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`submission_url` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`submission_url` (
  `submission_id` INT NOT NULL,
  `url_id` INT NOT NULL,
  PRIMARY KEY (`submission_id`, `url_id`),
  INDEX `fk_submission_url_url1_idx` (`url_id` ASC),
  INDEX `fk_submission_url_submission1_idx` (`submission_id` ASC),
  CONSTRAINT `fk_submission_url_submission1`
    FOREIGN KEY (`submission_id`)
    REFERENCES `reddit_db`.`submission` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_submission_url_url1`
    FOREIGN KEY (`url_id`)
    REFERENCES `reddit_db`.`url` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`query`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`query` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`query` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(665) NOT NULL COMMENT 'Value of the query: diabetes',
  `type` ENUM('UPDATE', 'QUERY', 'PSCOMMENT', 'PSSUBMISSION') NOT NULL COMMENT 'UPDATE, QUERY, PSCOMMENT, PSSUBMISSION',
  `suspended` TINYINT NULL DEFAULT 0 COMMENT '1: is suspended',
  `created` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last time executed',
  `times_executed` INT NULL DEFAULT 0,
  `message` TEXT NULL COMMENT '\'Information message about the last retrieval\'',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `value_name_UNIQUE` (`value` ASC, `type` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reddit_db`.`query_subreddit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reddit_db`.`query_subreddit` ;

CREATE TABLE IF NOT EXISTS `reddit_db`.`query_subreddit` (
  `query_id` INT NOT NULL,
  `subreddit_id` INT NOT NULL,
  PRIMARY KEY (`query_id`, `subreddit_id`),
  INDEX `fk_query_subreddit_subreddit1_idx` (`subreddit_id` ASC),
  INDEX `fk_query_subreddit_query1_idx` (`query_id` ASC),
  CONSTRAINT `fk_query_subreddit_query1`
    FOREIGN KEY (`query_id`)
    REFERENCES `reddit_db`.`query` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_query_subreddit_subreddit1`
    FOREIGN KEY (`subreddit_id`)
    REFERENCES `reddit_db`.`subreddit` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;