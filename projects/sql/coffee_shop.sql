CREATE TABLE coffee_shop (
shop_id		INT,
shop_name	VARCHAR(50),
city		VARCHAR(50),
state		CHAR(2),
PRIMARY KEY (shop_id)
);

CREATE TABLE employee (
employee_id INT,
first_name	VARCHAR(30),
last_name	VARCHAR(30),
hire_date	DATE,
job_title	VARCHAR(30),
shop_id		INT,
PRIMARY KEY (employee_id),
FOREIGN KEY (shop_id) REFERENCES coffee_shop (shop_id)
);
