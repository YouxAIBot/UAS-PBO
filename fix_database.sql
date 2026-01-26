-- Fix transaction table to use correct column name
ALTER TABLE IF EXISTS transactions RENAME COLUMN film_nama TO film_name;

-- Create reviews table if not exists
CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL,
    film_name VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_reviews_film_name ON reviews(film_name);
CREATE INDEX IF NOT EXISTS idx_reviews_transaction_id ON reviews(transaction_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at);
