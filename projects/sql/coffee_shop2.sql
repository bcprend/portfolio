CREATE TABLE supplier (
supplier_id			INTEGER,
company_name		VARCHAR(50),
country				VARCHAR(30),
sales_contact_name	VARCHAR(60),
email				VARCHAR(50) NOT NULL,
PRIMARY KEY (supplier_id)
);

CREATE TABLE coffee (
coffee_id		INTEGER,
shop_id			INTEGER,
supplier_id		INTEGER,
coffee_name		VARCHAR(30),
price_per_pound	NUMERIC(5,2),
PRIMARY KEY (coffee_id),
FOREIGN KEY (shop_id) REFERENCES coffee_shop (shop_id),
FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id)
);