-- SQL Script untuk membuat tabel reviews
-- Jalankan script ini di PostgreSQL bioskop_db

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL,
    film_name VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index untuk performa
CREATE INDEX IF NOT EXISTS idx_reviews_film_name ON reviews(film_name);
CREATE INDEX IF NOT EXISTS idx_reviews_transaction_id ON reviews(transaction_id);

-- Optional: Add foreign key constraint jika ada tabel transactions
-- ALTER TABLE reviews 
-- ADD CONSTRAINT fk_transaction 
-- FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE;

-- Verify tables exist
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
