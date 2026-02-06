import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useI18n } from '@/i18n';
import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';
import { ProductForm } from './ProductForm';

interface ProductDialogProps {
  open: boolean;
  product?: Product;
  onClose: () => void;
  onSubmit: (data: CreateProductRequest | UpdateProductRequest) => void;
  loading?: boolean;
}

export const ProductDialog = ({
  open,
  product,
  onClose,
  onSubmit,
  loading = false,
}: ProductDialogProps) => {
  const { t } = useI18n();
  const handleSubmit = (data: CreateProductRequest | UpdateProductRequest) => {
    onSubmit(data);
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: { xs: 2, sm: 3 },
          m: { xs: 2, sm: 3 },
          maxHeight: { xs: '90vh', sm: 'auto' },
        },
      }}
    >
      <DialogTitle
        sx={{
          pb: { xs: 1.5, sm: 2 },
          px: { xs: 2, sm: 3 },
          pt: { xs: 2, sm: 3 },
          borderBottom: '1px solid',
          borderColor: 'divider',
          fontWeight: 600,
          fontSize: { xs: '1.1rem', sm: '1.25rem' },
        }}
      >
        {product ? t.products.editProduct : t.products.createProduct}
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 16,
            top: 16,
            color: 'text.secondary',
            '&:hover': {
              backgroundColor: 'action.hover',
            },
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent
        sx={{
          pt: { xs: 2, sm: 3 },
          px: { xs: 2, sm: 3 },
          pb: { xs: 1, sm: 2 },
        }}
      >
        <ProductForm
          product={product}
          onSubmit={handleSubmit}
          onCancel={onClose}
          loading={loading}
        />
      </DialogContent>
      <DialogActions
        sx={{
          p: { xs: 2, sm: 3 },
          pt: { xs: 1.5, sm: 2 },
          px: { xs: 2, sm: 3 },
          borderTop: '1px solid',
          borderColor: 'divider',
          flexDirection: { xs: 'column-reverse', sm: 'row' },
          gap: { xs: 1, sm: 0 },
        }}
      >
        <Button
          onClick={onClose}
          fullWidth={false}
          sx={{
            color: 'text.secondary',
            borderColor: '#e0e0e0',
            width: { xs: '100%', sm: 'auto' },
            '&:hover': {
              borderColor: '#bdbdbd',
              backgroundColor: '#f5f5f5',
            },
          }}
          variant="outlined"
        >
          {t.products.cancel}
        </Button>
        <Button
          type="submit"
          form="product-form"
          variant="contained"
          fullWidth={false}
          sx={{
            backgroundColor: '#1192d4',
            width: { xs: '100%', sm: 'auto' },
            '&:hover': {
              backgroundColor: '#0d7bb8',
            },
          }}
          disabled={loading}
        >
          {product ? t.products.update : t.products.createButton}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
