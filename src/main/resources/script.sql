CREATE TABLE IF NOT EXISTS `items` (
  ID          INTEGER PRIMARY KEY AUTOINCREMENT,
  seller_uuid VARCHAR(45) NOT NULL,
  seller_name VARCHAR(45) NOT NULL,
  price       VARCHAR(16) NOT NULL,
  item        TEXT,
  time_expire BIGINT
);
