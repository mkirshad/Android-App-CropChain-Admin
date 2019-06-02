	/*
	
Users:
	Id			PK auto increment
	Name		Not Null, NOT EMPTY
	Email		Unique	Not Null, NOT EMPTY
	Password	Not Null, NOT EMPTY
	IsActive	0 0r 1 default 1 not null

Products:
	Id			PK autoincrement
	Name		NOT NULL, NOT EMPTY, UNIQUE
	Rate
	RateUpdatedAt
	RateUpdatedByUserId
	IsActive	0 0r 1 default 1 not null
	
ProductRates:
	Id			PK autoincrement
	ProductId	indexed
	UserId		indexed
	Rate		Not Null
	CreatedAt
	IsActive	0 0r 1 default 1 not null

	
	
	*/
	
	
	
	
	
DROP TABLE IF EXISTS `Requests`;
CREATE TABLE `Requests` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Request` text,
  `Response` text NOT NULL,
   PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;	

	
	
	
	
DROP TABLE IF EXISTS `Users`;
CREATE TABLE `Users` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(500) NOT NULL,
  `Email` varchar(500) NOT NULL UNIQUE,
  `Password` varchar(255) NOT NULL,
  `PasswordHint` varchar(255),
  `IsActive` int(11) DEFAULT 1 NOT NULL,
  PRIMARY KEY (`Id`),
  CONSTRAINT CHK_Users CHECK (`Name` <> '' AND `Email` <> '' AND `Password` <> '' AND `IsActive` IN (0,1))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


INSERT INTO `Users` (PasswordHint, `Password`, Name, Email) VALUES
('pass312', '1514a9958960b9e8a560e05345835d4bbac8048468b798bc5a4f3c377aa59bdd48ee2abc353a8c41950aeedaf7a935f3863b707c80320c713c8c4047cbde04ad', 'user1','user1@users.com'),
('pass749', 'd383015a48a694496a3c62577fd6a3aeade9fcc4dfbc3cb058d77ede33b81e8f5957fd552aa7516a765521362aa75201e451f8bc9d7c36461f20a934a0c35947', 'user2','user2@users.com'),
('admin987', '7e770dc7aa7353c6fb2dbc563e394e564b3f482ccb21af4d47690a2b3b6856089ab680307908bcc374c1ffcd499237f6f635ce18c3ef4abbdf477d1530876feb', 'admin','admin@users.com');


DROP TABLE IF EXISTS `Products`;
CREATE TABLE `Products` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(500) NOT NULL UNIQUE,
  `Rate` INT(11) NOT NULL,
  `RateUpdatedAt` DATETIME NOT NULL,
  `RateUpdatedByUserId` INT(11) NOT NULL,
  `IsActive` int(11) DEFAULT 1 NOT NULL,
  `SortOrder` int default 0 not null,
  PRIMARY KEY (`Id`),
  CONSTRAINT CHK_Products CHECK (`Name` <> '' AND `IsActive` IN (0,1))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `ProductRates`;
CREATE TABLE `ProductRates` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ProductId` INT(11) NOT NULL,
  `UserId` INT(11) NOT NULL,
  `Rate` INT(11) NOT NULL,
  `CreatedAt` DATETIME NOT NULL,
  `IsActive` int(11) DEFAULT 1 NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE INDEX IDXProdctRateProductId ON `ProductRates`(`ProductId`);
CREATE INDEX IDXProdctRateUserId ON `ProductRates`(`UserId`);
CREATE INDEX IDXProdctRateIsActive ON `ProductRates`(`IsActive`);

INSERT INTO `Products`(`Name`, `Rate`, `RateUpdatedAt`, `RateUpdatedByUserId`) VALUES ('KINNOW', 34, now(), 1), ('WHITE CHONSA', 38,now(),  1), ('ONION PK', 11, now(), 1), ('ONION IN', 09, now(), 1), 
('POTATO PK', 13, now(), 1), ('MOSMI CHONSA', 40, now(), 1);

