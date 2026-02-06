import { useState } from 'react';
import {
  Box,
  Button,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Alert,
} from '@mui/material';
import { useAppSelector } from '@/store/hooks';
import { useI18n } from '@/i18n';
import { CreateProductRawMaterialRequest } from '@/types/productRawMaterial';

interface RawMaterialSelectorProps {
  onAdd: (request: CreateProductRawMaterialRequest) => void;
  existingRawMaterialCodes: string[];
  loading?: boolean;
}

export const RawMaterialSelector = ({
  onAdd,
  existingRawMaterialCodes,
  loading = false,
}: RawMaterialSelectorProps) => {
  const { t } = useI18n();
  const { items: rawMaterials } = useAppSelector((state) => state.rawMaterials);
  const [selectedCode, setSelectedCode] = useState('');
  const [quantity, setQuantity] = useState('');
  const [error, setError] = useState('');

  const availableRawMaterials = rawMaterials.filter(
    (rm) => !existingRawMaterialCodes.includes(rm.code)
  );

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!selectedCode) {
      setError(t.productRawMaterials.rawMaterial + ' ' + t.common.error.toLowerCase());
      return;
    }

    const numQuantity = parseFloat(quantity);
    if (!quantity.trim() || isNaN(numQuantity) || numQuantity <= 0) {
      setError(t.productRawMaterials.quantity + ' ' + t.products.validation.valuePositive.toLowerCase());
      return;
    }

    onAdd({
      rawMaterialCode: selectedCode,
      quantity: numQuantity,
    });

    // Reset form
    setSelectedCode('');
    setQuantity('');
  };

  if (availableRawMaterials.length === 0 && existingRawMaterialCodes.length > 0) {
    return (
      <Alert severity="info">
        {t.productRawMaterials.noRawMaterialsDescription}
      </Alert>
    );
  }

  return (
    <Box>
      <Box
        component="form"
        onSubmit={handleSubmit}
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          gap: { xs: 2, sm: 2 },
          alignItems: { xs: 'stretch', sm: 'flex-start' },
        }}
      >
        <FormControl fullWidth sx={{ minWidth: { xs: '100%', sm: 200 } }}>
          <InputLabel
            sx={{
              fontSize: { xs: '0.95rem', sm: '1rem' },
            }}
          >
            {t.productRawMaterials.rawMaterial}
          </InputLabel>
          <Select
            value={selectedCode}
            onChange={(e) => setSelectedCode(e.target.value)}
            label={t.productRawMaterials.rawMaterial}
            disabled={loading || availableRawMaterials.length === 0}
            sx={{
              fontSize: { xs: '0.95rem', sm: '1rem' },
            }}
          >
            {availableRawMaterials.map((rm) => (
              <MenuItem key={rm.code} value={rm.code}>
                {rm.name} ({rm.code}) - {t.rawMaterials.stockQuantity}: {rm.stockQuantity}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <TextField
          label={t.productRawMaterials.quantity}
          type="number"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          inputProps={{ min: 0, step: 0.01 }}
          sx={{
            width: { xs: '100%', sm: 150 },
            '& .MuiInputLabel-root': {
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
            '& .MuiOutlinedInput-input': {
              fontSize: { xs: '0.95rem', sm: '1rem' },
            },
          }}
          disabled={loading}
          required
        />
        <Button
          type="submit"
          variant="contained"
          disabled={loading || availableRawMaterials.length === 0}
          sx={{
            width: { xs: '100%', sm: 'auto' },
            whiteSpace: 'nowrap',
            minWidth: { xs: '100%', sm: 100 },
          }}
        >
          {t.productRawMaterials.addRawMaterial}
        </Button>
      </Box>
      {error && (
        <Alert severity="error" sx={{ mt: 2, width: '100%' }}>
          {error}
        </Alert>
      )}
    </Box>
  );
};
