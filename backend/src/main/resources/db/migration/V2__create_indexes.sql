-- Create indexes for better query performance
-- These indexes optimize common query patterns

-- Index on product code (unique constraint already exists, but explicit index helps)
CREATE INDEX IF NOT EXISTS idx_products_code ON products(code);

-- Index on product name for search operations
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Index on raw material code
CREATE INDEX IF NOT EXISTS idx_raw_materials_code ON raw_materials(code);

-- Index on raw material name for search operations
CREATE INDEX IF NOT EXISTS idx_raw_materials_name ON raw_materials(name);

-- Index on product_raw_materials for faster joins
CREATE INDEX IF NOT EXISTS idx_product_raw_materials_product_id ON product_raw_materials(product_id);
CREATE INDEX IF NOT EXISTS idx_product_raw_materials_raw_material_id ON product_raw_materials(raw_material_id);

-- Composite index for common query patterns
CREATE INDEX IF NOT EXISTS idx_products_name_value ON products(name, value);

-- Full-text search indexes (PostgreSQL GIN indexes for text search)
-- These enable fast full-text search on product and raw material names
CREATE INDEX IF NOT EXISTS idx_products_name_fts ON products USING gin(to_tsvector('portuguese', name));
CREATE INDEX IF NOT EXISTS idx_raw_materials_name_fts ON raw_materials USING gin(to_tsvector('portuguese', name));

-- Index on version field for optimistic locking
CREATE INDEX IF NOT EXISTS idx_products_version ON products(version);
CREATE INDEX IF NOT EXISTS idx_raw_materials_version ON raw_materials(version);
