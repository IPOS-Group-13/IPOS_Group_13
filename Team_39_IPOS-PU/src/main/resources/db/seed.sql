

DELETE ci FROM campaign_items ci INNER JOIN campaigns c ON ci.campaign_id = c.id WHERE c.name = 'Spring Relief Campaign';
DELETE cm FROM campaign_metrics cm INNER JOIN campaigns c ON cm.campaign_id = c.id WHERE c.name = 'Spring Relief Campaign';
DELETE FROM campaigns WHERE name = 'Spring Relief Campaign';


INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count)
SELECT 'sysdba@ipos.local', 'sysdba', 'masterkey', 'ADMIN', 'ACTIVE', FALSE, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'sysdba@ipos.local');

INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count)
SELECT 'manager@ipos.local', 'manager', 'GetPU_it_done', 'ADMIN', 'ACTIVE', FALSE, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'manager@ipos.local');


UPDATE users SET email = 'dimitarprem@gmail.com' WHERE email = 'cool@example.com';
UPDATE users SET email = 'test.ipos.pu@gmail.com' WHERE email = 'cool1@example.com';
UPDATE commercial_applications SET email = 'dimitarprem777711@gmail.com' WHERE email = 'pondPharma@example.com';


INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count)
SELECT 'dimitarprem@gmail.com', 'PU0001', '12ss_56_SS', 'NON_COMMERCIAL', 'ACTIVE', FALSE, 8
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'dimitarprem@gmail.com');

INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count)
SELECT 'test.ipos.pu@gmail.com', 'PU0002', '34pp_78_LL', 'NON_COMMERCIAL', 'ACTIVE', FALSE, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'test.ipos.pu@gmail.com');

INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count)
SELECT 'guest.checkout@ipos.local', NULL, 'guest-only-seed', 'GUEST', 'ACTIVE', FALSE, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'guest.checkout@ipos.local');


INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000001', 'Paracetamol', 'Box, caps 20 (sample Item 100 00001)', 0.20, 10345, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000001');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000002', 'Aspirin', 'Box, caps 20 (sample Item 100 00002)', 1.00, 12453, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000002');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000003', 'Analgin', 'Box, caps 10 (sample Item 100 00003)', 2.40, 4235, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000003');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000004', 'Celebrex, caps 100 mg', 'Box, caps 10 (sample Item 100 00004)', 20.00, 3420, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000004');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000005', 'Celebrex, caps 200 mg', 'Box, caps 10 (sample Item 100 00005)', 37.00, 1450, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000005');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000006', 'Retin-A Tretin, 30 g', 'Box, caps 20 (sample Item 100 00006)', 50.00, 2013, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000006');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000007', 'Lipitor TB, 20 mg', 'Box, caps 30 (sample Item 100 00007)', 31.00, 1562, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000007');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '10000008', 'Claritin CR, 60g', 'Box, caps 20 (sample Item 100 00008)', 39.00, 2540, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '10000008');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '20000004', 'Iodine tincture', 'Bottle, ml 100 (sample Item 200 00004)', 0.60, 22134, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '20000004');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '20000005', 'Rhynol', 'Bottle, ml 200 (sample Item 200 00005)', 5.00, 1908, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '20000005');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '30000001', 'Ospen', 'Box, caps 20 (sample Item 300 00001)', 21.00, 809, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '30000001');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '30000002', 'Amopen', 'Box, caps 30 (sample Item 300 00002)', 30.00, 1340, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '30000002');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '40000001', 'Vitamin C', 'Box, caps 30 (sample Item 400 00001)', 2.40, 3258, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '40000001');
INSERT INTO products (id, name, description, retail_price, stock_quantity, is_active)
SELECT '40000002', 'Vitamin B12', 'Box, caps 30 (sample Item 400 00002)', 2.60, 2673, TRUE
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '40000002');


UPDATE products SET retail_price = 0.20, stock_quantity = 10345 WHERE id = '10000001';
UPDATE products SET retail_price = 1.00, stock_quantity = 12453 WHERE id = '10000002';
UPDATE products SET retail_price = 2.40, stock_quantity = 4235 WHERE id = '10000003';
UPDATE products SET retail_price = 20.00, stock_quantity = 3420 WHERE id = '10000004';
UPDATE products SET retail_price = 37.00, stock_quantity = 1450 WHERE id = '10000005';
UPDATE products SET retail_price = 50.00, stock_quantity = 2013 WHERE id = '10000006';
UPDATE products SET retail_price = 31.00, stock_quantity = 1562 WHERE id = '10000007';
UPDATE products SET retail_price = 39.00, stock_quantity = 2540 WHERE id = '10000008';
UPDATE products SET retail_price = 0.60, stock_quantity = 22134 WHERE id = '20000004';
UPDATE products SET retail_price = 5.00, stock_quantity = 1908 WHERE id = '20000005';
UPDATE products SET retail_price = 21.00, stock_quantity = 809 WHERE id = '30000001';
UPDATE products SET retail_price = 30.00, stock_quantity = 1340 WHERE id = '30000002';
UPDATE products SET retail_price = 2.40, stock_quantity = 3258 WHERE id = '40000001';
UPDATE products SET retail_price = 2.60, stock_quantity = 2673 WHERE id = '40000002';


INSERT INTO campaigns (name, start_time, end_time, status)
SELECT 'March Promotion', '2026-03-15 00:00:00', '2026-04-20 23:59:59', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM campaigns WHERE name = 'March Promotion');

INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '10000002', 5 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '10000002');
INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '10000003', 10 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '10000003');
INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '10000004', 10 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '10000004');
INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '10000006', 20 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '10000006');

INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '10000002', 0, 0, 0 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '10000002');
INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '10000003', 0, 0, 0 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '10000003');
INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '10000004', 0, 0, 0 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '10000004');
INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '10000006', 0, 0, 0 FROM campaigns c WHERE c.name = 'March Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '10000006');


INSERT INTO campaigns (name, start_time, end_time, status)
SELECT 'April Promotion', '2026-04-05 00:00:00', '2026-04-10 23:59:59', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM campaigns WHERE name = 'April Promotion');

INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '30000001', 20 FROM campaigns c WHERE c.name = 'April Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '30000001');
INSERT INTO campaign_items (campaign_id, product_id, discount_percent)
SELECT c.id, '40000001', 10 FROM campaigns c WHERE c.name = 'April Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_items ci WHERE ci.campaign_id = c.id AND ci.product_id = '40000001');

INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '30000001', 0, 0, 0 FROM campaigns c WHERE c.name = 'April Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '30000001');
INSERT INTO campaign_metrics (campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count)
SELECT c.id, '40000001', 0, 0, 0 FROM campaigns c WHERE c.name = 'April Promotion'
  AND NOT EXISTS (SELECT 1 FROM campaign_metrics cm WHERE cm.campaign_id = c.id AND cm.product_id = '40000001');


INSERT INTO commercial_applications (company_registration_number, director_name, business_type, address, email, submitted_at, submission_status)
SELECT 'UK10003429CompH', 'Pond Pharmacy Director', 'Community Pharmacy',
       '25 High Street, Chislehurst, BR7 5BN', 'dimitarprem777711@gmail.com', '2026-02-01 12:00:00', 'SUBMITTED_TO_SA'
WHERE NOT EXISTS (SELECT 1 FROM commercial_applications WHERE email = 'dimitarprem777711@gmail.com');

UPDATE commercial_applications SET company_registration_number = 'UK10003429CompH'
WHERE email = 'dimitarprem777711@gmail.com';

INSERT INTO app_config (config_key, config_value)
SELECT 'vat_rate', '0'
WHERE NOT EXISTS (SELECT 1 FROM app_config WHERE config_key = 'vat_rate');

UPDATE app_config SET config_value = '0' WHERE config_key = 'vat_rate';

INSERT INTO app_config (config_key, config_value)
SELECT 'retail_markup_percent', '100'
WHERE NOT EXISTS (SELECT 1 FROM app_config WHERE config_key = 'retail_markup_percent');

UPDATE app_config SET config_value = '100' WHERE config_key = 'retail_markup_percent';


UPDATE users SET login_alias = 'sysdba' WHERE email = 'sysdba@ipos.local' AND login_alias IS NULL;
UPDATE users SET login_alias = 'manager' WHERE email = 'manager@ipos.local' AND login_alias IS NULL;
UPDATE users SET login_alias = 'PU0001' WHERE email = 'dimitarprem@gmail.com' AND login_alias IS NULL;
UPDATE users SET login_alias = 'PU0002' WHERE email = 'test.ipos.pu@gmail.com' AND login_alias IS NULL;


UPDATE users SET password_hash = 'GetPU_it_done' WHERE email = 'manager@ipos.local';
