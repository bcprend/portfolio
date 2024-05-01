SELECT coffee_shop.shop_id, coffee.coffee_name, supplier.sales_contact_name, supplier.email
FROM coffee
JOIN coffee_shop ON coffee.shop_id=coffee_shop.shop_id
JOIN supplier ON coffee.supplier_id=supplier.supplier_id
