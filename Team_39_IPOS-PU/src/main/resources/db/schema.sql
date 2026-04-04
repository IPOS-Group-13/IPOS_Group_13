CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login_alias VARCHAR(64) NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    member_type VARCHAR(32) NOT NULL,
    account_status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    first_login_required BOOLEAN NOT NULL DEFAULT FALSE,
    completed_order_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS commercial_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_registration_number VARCHAR(100) NOT NULL,
    director_name VARCHAR(255) NOT NULL,
    business_type VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    email VARCHAR(255) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_status VARCHAR(64) NOT NULL DEFAULT 'SUBMITTED_TO_SA'
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    retail_price DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS campaigns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'UPCOMING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS campaign_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_campaign_items_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_campaign_items_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE,
    CONSTRAINT uq_campaign_product UNIQUE (campaign_id, product_id)
);

CREATE TABLE IF NOT EXISTS campaign_metrics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    campaign_hits INT NOT NULL DEFAULT 0,
    item_added_count INT NOT NULL DEFAULT 0,
    item_purchased_count INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_campaign_metrics_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_campaign_metrics_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE,
    CONSTRAINT uq_campaign_metrics UNIQUE (campaign_id, product_id)
);

CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(64) PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    tracking_code VARCHAR(128),
    delivery_address VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    line_total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64),
    payee_details VARCHAR(255) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    card_type VARCHAR(32),
    first4 VARCHAR(4),
    last4 VARCHAR(4),
    expiry_date VARCHAR(16),
    status VARCHAR(32) NOT NULL,
    message VARCHAR(255),
    transaction_id VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS email_outbox (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    purpose VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_config (
    config_key VARCHAR(64) PRIMARY KEY,
    config_value VARCHAR(255) NOT NULL
);
