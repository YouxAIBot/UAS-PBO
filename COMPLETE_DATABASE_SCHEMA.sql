-- ============================================
-- BIOSKOP DATABASE COMPLETE SCHEMA
-- PostgreSQL Script
-- ============================================

-- Step 1: Create database (jika belum ada)
-- CREATE DATABASE bioskop_db;

-- Step 2: Connect ke database
-- \c bioskop_db

-- ============================================
-- TABLE: FILMS
-- ============================================
CREATE TABLE IF NOT EXISTS films (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price INT NOT NULL CHECK (price > 0),
    stock INT NOT NULL CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index untuk performance
CREATE INDEX IF NOT EXISTS idx_films_name ON films(name);

-- Sample data
INSERT INTO films (name, price, stock) VALUES
    ('Avengers', 50000, 20),
    ('Interstellar', 60000, 15),
    ('Spiderman', 55000, 10)
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- TABLE: CONSUMPTIONS
-- ============================================
CREATE TABLE IF NOT EXISTS consumptions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price INT NOT NULL CHECK (price > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_consumptions_name ON consumptions(name);

-- Sample data
INSERT INTO consumptions (name, price) VALUES
    ('Popcorn', 20000),
    ('Cola', 10000)
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- TABLE: TRANSACTIONS (existing)
-- ============================================
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    film_name VARCHAR(255) NOT NULL,
    ticket_qty INT NOT NULL CHECK (ticket_qty > 0),
    snack_detail TEXT,
    total INT NOT NULL CHECK (total > 0),
    payment_method VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transactions_film_name ON transactions(film_name);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_payment_method ON transactions(payment_method);

-- ============================================
-- TABLE: REVIEWS (NEW)
-- ============================================
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

-- Optional: Foreign key constraint
-- ALTER TABLE reviews 
-- ADD CONSTRAINT fk_reviews_transaction 
-- FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE;

-- ============================================
-- VIEWS (Aggregation)
-- ============================================

-- View: Film ratings summary
CREATE OR REPLACE VIEW vw_film_ratings AS
SELECT 
    r.film_name,
    COUNT(r.id) as total_reviews,
    ROUND(AVG(r.rating)::numeric, 2) as average_rating,
    MAX(r.rating) as max_rating,
    MIN(r.rating) as min_rating,
    COUNT(CASE WHEN r.rating = 5 THEN 1 END) as five_star_count,
    COUNT(CASE WHEN r.rating = 4 THEN 1 END) as four_star_count,
    COUNT(CASE WHEN r.rating = 3 THEN 1 END) as three_star_count,
    COUNT(CASE WHEN r.rating = 2 THEN 1 END) as two_star_count,
    COUNT(CASE WHEN r.rating = 1 THEN 1 END) as one_star_count
FROM reviews r
GROUP BY r.film_name;

-- View: Transaction summary
CREATE OR REPLACE VIEW vw_transaction_summary AS
SELECT 
    COUNT(id) as total_transactions,
    SUM(total) as total_revenue,
    AVG(total) as average_transaction_value,
    MAX(total) as max_transaction,
    MIN(total) as min_transaction,
    (SELECT COUNT(DISTINCT payment_method) FROM transactions) as payment_methods_used
FROM transactions;

-- View: Recent transactions with reviews
CREATE OR REPLACE VIEW vw_transactions_with_reviews AS
SELECT 
    t.id as transaction_id,
    t.film_name,
    t.ticket_qty,
    t.snack_detail,
    t.total,
    t.payment_method,
    t.created_at as transaction_date,
    r.id as review_id,
    r.rating,
    r.comment,
    r.created_at as review_date,
    CASE WHEN r.id IS NOT NULL THEN 'Reviewed' ELSE 'Not Reviewed' END as status
FROM transactions t
LEFT JOIN reviews r ON t.id = r.transaction_id
ORDER BY t.created_at DESC;

-- ============================================
-- FUNCTIONS
-- ============================================

-- Function: Get film average rating
CREATE OR REPLACE FUNCTION get_film_average_rating(p_film_name VARCHAR)
RETURNS NUMERIC AS $$
BEGIN
    RETURN (SELECT COALESCE(ROUND(AVG(rating)::numeric, 2), 0)
            FROM reviews 
            WHERE film_name = p_film_name);
END;
$$ LANGUAGE plpgsql;

-- Function: Get film review count
CREATE OR REPLACE FUNCTION get_film_review_count(p_film_name VARCHAR)
RETURNS INT AS $$
BEGIN
    RETURN (SELECT COUNT(*)
            FROM reviews 
            WHERE film_name = p_film_name);
END;
$$ LANGUAGE plpgsql;

-- Function: Get total revenue
CREATE OR REPLACE FUNCTION get_total_revenue()
RETURNS INT AS $$
BEGIN
    RETURN (SELECT COALESCE(SUM(total), 0)
            FROM transactions);
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- PROCEDURES (Optional - untuk future use)
-- ============================================

-- Procedure: Archive old transactions
CREATE OR REPLACE PROCEDURE archive_old_transactions(p_months INT DEFAULT 12)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM transactions 
    WHERE created_at < NOW() - (p_months || ' months')::INTERVAL
    AND id NOT IN (SELECT transaction_id FROM reviews WHERE transaction_id IS NOT NULL);
    
    RAISE NOTICE 'Old transactions archived successfully';
END;
$$;

-- ============================================
-- VALIDATION & VERIFICATION QUERIES
-- ============================================

-- Verify all tables exist
SELECT 'Tables' as object_type, table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Verify all indexes
SELECT 'Indexes' as object_type, indexname FROM pg_indexes 
WHERE schemaname = 'public'
ORDER BY indexname;

-- Verify all views
SELECT 'Views' as object_type, table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_type = 'VIEW'
ORDER BY table_name;

-- ============================================
-- DATA INTEGRITY CHECKS
-- ============================================

-- Check orphaned reviews (tidak ada transaction ID)
SELECT 'Orphaned Reviews' as check_type, COUNT(*) as count
FROM reviews 
WHERE transaction_id = 0 OR transaction_id IS NULL;

-- Check transactions without film
SELECT 'Empty Film Names' as check_type, COUNT(*) as count
FROM transactions 
WHERE film_name IS NULL OR film_name = '';

-- Check invalid ratings
SELECT 'Invalid Ratings' as check_type, COUNT(*) as count
FROM reviews 
WHERE rating < 1 OR rating > 5;

-- ============================================
-- SAMPLE QUERIES
-- ============================================

-- Get top rated films
SELECT film_name, average_rating, total_reviews
FROM vw_film_ratings
ORDER BY average_rating DESC
LIMIT 10;

-- Get most reviewed films
SELECT film_name, total_reviews, average_rating
FROM vw_film_ratings
ORDER BY total_reviews DESC
LIMIT 10;

-- Get transaction summary
SELECT * FROM vw_transaction_summary;

-- Get transactions yang sudah di-review
SELECT * FROM vw_transactions_with_reviews 
WHERE status = 'Reviewed'
LIMIT 10;

-- Get recent reviews (last 7 days)
SELECT 
    r.id,
    r.film_name,
    r.rating,
    r.comment,
    r.created_at
FROM reviews r
WHERE r.created_at >= NOW() - INTERVAL '7 days'
ORDER BY r.created_at DESC;

-- Get average rating by payment method
SELECT 
    t.payment_method,
    COUNT(r.id) as reviews_count,
    ROUND(AVG(r.rating)::numeric, 2) as avg_rating
FROM transactions t
LEFT JOIN reviews r ON t.id = r.transaction_id
GROUP BY t.payment_method
ORDER BY avg_rating DESC;

-- ============================================
-- MAINTENANCE QUERIES
-- ============================================

-- Update updated_at timestamp (trigger substitute)
UPDATE transactions SET updated_at = CURRENT_TIMESTAMP WHERE id > 0;
UPDATE reviews SET updated_at = CURRENT_TIMESTAMP WHERE id > 0;

-- Get database size
SELECT 
    'bioskop_db' as database_name,
    pg_size_pretty(pg_database_size('bioskop_db')) as size;

-- Get table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Get row counts
SELECT 
    'films' as table_name, COUNT(*) as row_count FROM films
UNION ALL
SELECT 'consumptions', COUNT(*) FROM consumptions
UNION ALL
SELECT 'transactions', COUNT(*) FROM transactions
UNION ALL
SELECT 'reviews', COUNT(*) FROM reviews;

-- ============================================
-- BACKUP & RECOVERY NOTES
-- ============================================

-- Backup (dari terminal):
-- pg_dump -U postgres bioskop_db > bioskop_backup.sql

-- Restore (dari terminal):
-- psql -U postgres -d bioskop_db < bioskop_backup.sql

-- Export data to CSV:
-- \COPY (SELECT * FROM transactions) TO 'transactions.csv' CSV HEADER;
-- \COPY (SELECT * FROM reviews) TO 'reviews.csv' CSV HEADER;

-- ============================================
-- END OF SCHEMA SCRIPT
-- ============================================

-- Verification - Run this to check everything is ready:
-- SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';
-- Should return: 4 (films, consumptions, transactions, reviews)

COMMIT;
