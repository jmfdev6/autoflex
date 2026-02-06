import { useState, useEffect } from 'react';
import { TextField, Box, Button } from '@mui/material';
import { useI18n } from '@/i18n';
import {
  RawMaterial,
  CreateRawMaterialRequest,
  UpdateRawMaterialRequest,
} from '@/types/rawMaterial';

interface RawMaterialFormProps {
  rawMaterial?: RawMaterial;
  onSubmit: (data: CreateRawMaterialRequest | UpdateRawMaterialRequest) => void;
  onCancel: () => void;
  loading?: boolean;
}

export const RawMaterialForm = ({
  rawMaterial,
  onSubmit,
  onCancel,
  loading = false,
}: RawMaterialFormProps) => {
  const { t } = useI18n();
  const [name, setName] = useState('');
  const [stockQuantity, setStockQuantity] = useState('');
  const [errors, setErrors] = useState<{ name?: string; stockQuantity?: string }>({});

  useEffect(() => {
    if (rawMaterial) {
      setName(rawMaterial.name);
      setStockQuantity(rawMaterial.stockQuantity.toString());
    }
  }, [rawMaterial]);

  const validate = (): boolean => {
    const newErrors: { name?: string; stockQuantity?: string } = {};

    if (!name.trim()) {
      newErrors.name = t.rawMaterials.validation.nameRequired;
    }

    const numQuantity = parseFloat(stockQuantity);
    if (!stockQuantity.trim()) {
      newErrors.stockQuantity = t.rawMaterials.validation.stockQuantityRequired;
    } else if (isNaN(numQuantity) || numQuantity < 0) {
      newErrors.stockQuantity = t.rawMaterials.validation.stockQuantityNonNegative;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      onSubmit({
        name: name.trim(),
        stockQuantity: parseFloat(stockQuantity),
      });
    }
  };

  return (
    <form id="raw-material-form" onSubmit={handleSubmit}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: { xs: 2, sm: 3 },
        }}
      >
        <TextField
          label={t.rawMaterials.rawMaterialName}
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
          label={t.rawMaterials.stockQuantityLabel}
          type="number"
          value={stockQuantity}
          onChange={(e) => setStockQuantity(e.target.value)}
          error={!!errors.stockQuantity}
          helperText={errors.stockQuantity}
          fullWidth
          required
          inputProps={{ min: 0, step: 1 }}
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
