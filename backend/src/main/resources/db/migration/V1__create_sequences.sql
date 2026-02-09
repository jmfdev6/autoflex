-- Create sequences for thread-safe code generation
-- This eliminates race conditions when generating product and raw material codes

-- Sequence for product codes (P001, P002, etc.)
CREATE SEQUENCE IF NOT EXISTS product_code_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Sequence for raw material codes (RM001, RM002, etc.)
CREATE SEQUENCE IF NOT EXISTS raw_material_code_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Set the sequences to start from the current max code value if data already exists
-- This ensures continuity when migrating from the old count() + 1 approach

-- For products: extract max number from existing codes
DO $$
DECLARE
    max_product_num INTEGER;
BEGIN
    SELECT COALESCE(MAX(CAST(SUBSTRING(code FROM 2) AS INTEGER)), 0)
    INTO max_product_num
    FROM products
    WHERE code ~ '^P[0-9]+$';
    
    IF max_product_num > 0 THEN
        PERFORM setval('product_code_sequence', max_product_num, true);
    END IF;
END $$;

-- For raw materials: extract max number from existing codes
DO $$
DECLARE
    max_rm_num INTEGER;
BEGIN
    SELECT COALESCE(MAX(CAST(SUBSTRING(code FROM 3) AS INTEGER)), 0)
    INTO max_rm_num
    FROM raw_materials
    WHERE code ~ '^RM[0-9]+$';
    
    IF max_rm_num > 0 THEN
        PERFORM setval('raw_material_code_sequence', max_rm_num, true);
    END IF;
END $$;
