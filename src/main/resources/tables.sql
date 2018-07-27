DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS transfer;

CREATE TABLE account (
  id BIGINT AUTO_INCREMENT primary key,
  name VARCHAR(255),
  balance DECIMAL(20,2) default 0,
  currency VARCHAR(3) default 'GBP'
);

CREATE table transfer (
  id BIGINT AUTO_INCREMENT primary key,
  sourceAccountId BIGINT,
  destinationAccountId BIGINT,
  foreign key (sourceAccountId) references account(id),
  foreign key (destinationAccountId) references account(id),
  amount DECIMAL(20,2),
  currency VARCHAR(3) default 'GBP',
  reference VARCHAR(255) default null,
  status VARCHAR(255) default null,
  timestamp TIMESTAMP default null
);
