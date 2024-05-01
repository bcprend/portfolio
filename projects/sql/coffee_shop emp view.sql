CREATE VIEW employee_view AS
	SELECT 
		employee_id,
        CONCAT(first_name, " ", last_name) employee_full_name,
        hire_date,
        job_title,
        shop_id
	FROM employee;
