-- Create Product Service Database
CREATE DATABASE IF NOT EXISTS "product-service";

-- Connect to the database
\c "product-service"

-- Create Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    sku VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    stock_quantity BIGINT NOT NULL,
    reserved_quantity BIGINT DEFAULT 0 NOT NULL,
    is_active BOOLEAN DEFAULT true NOT NULL,
    image_url VARCHAR(500),
    rating DOUBLE PRECISION DEFAULT 0,
    review_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create Indexes for Performance
CREATE INDEX idx_category ON products(category);
CREATE INDEX idx_sku ON products(sku);
CREATE INDEX idx_is_active ON products(is_active);

-- Create GIN index for full-text search on product name
CREATE INDEX idx_product_name_search ON products USING GIN(to_tsvector('english', name));
