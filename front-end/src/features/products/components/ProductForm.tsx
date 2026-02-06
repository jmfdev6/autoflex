import { useState, useEffect } from 'react';
import { TextField, Box } from '@mui/material';
import { useI18n } from '@/i18n';
import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';

interface ProductFormProps {
  product?: Product;
  onSubmit: (data: CreateProductRequest | UpdateProductRequest) => void;
  onCancel?: () => void;
  loading?: boolean;
}

export const ProductForm = ({ product, onSubmit, loading = false }: ProductFormProps) => {
  const { t } = useI18n();
  const [name, setName] = useState('');
  const [value, setValue] = useState('');
  const [errors, setErrors] = useState<{ name?: string; value?: string }>({});

  useEffect(() => {
    if (product) {
      setName(product.name);
      setValue(product.value.toString());
    }
  }, [product]);

  const validate = (): boolean => {
    const newErrors: { name?: string; value?: string } = {};

    if (!name.trim()) {
      newErrors.name = t.products.validation.nameRequired;
    }

    const numValue = parseFloat(value);
    if (!value.trim()) {
      newErrors.value = t.products.validation.valueRequired;
    } else if (isNaN(numValue) || numValue <= 0) {
      newErrors.value = t.products.validation.valuePositive;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      onSubmit({
        name: name.trim(),
        value: parseFloat(value),
      });
    }
  };

  return (
    <form id="product-form" onSubmit={handleSubmit}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: { xs: 2, sm: 3 },
        }}
      >
        <TextField
          label={t.products.productName}
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={!!errors.name}
          helperText={errors.name}
          fullWidth
          required
          disabled={loading}
          size="medium"
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
            '& .MuiInputLabel-root': {
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
          }}
        />
        <TextField
          label={t.products.productValue}
          type="number"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          error={!!errors.value}
          helperText={errors.value}
          fullWidth
          required
          inputProps={{ min: 0, step: 0.01 }}
          disabled={loading}
          size="medium"
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
            '& .MuiInputLabel-root': {
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
          }}
        />
      </Box>
    </form>
  );
};
