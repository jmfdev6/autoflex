import { useEffect, useState, useMemo } from 'react';
import {
  Box,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  IconButton,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
  InputAdornment,
  List,
  ListItem,
  ListItemText,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SettingsIcon from '@mui/icons-material/Settings';
import SearchIcon from '@mui/icons-material/Search';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useI18n } from '@/i18n';
import {
  fetchProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from '@/store/slices/productsSlice';
import { ProductDialog } from './ProductDialog';
import { ProductRawMaterialManager } from '@/features/productRawMaterials/components/ProductRawMaterialManager';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';

export const ProductList = () => {
  const dispatch = useAppDispatch();
  const { t } = useI18n();
  const { items, loading, error } = useAppSelector((state) => state.products);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | undefined>();
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);
  const [rawMaterialsOpen, setRawMaterialsOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  const filteredItems = useMemo(() => {
    if (!searchQuery.trim()) return items;
    const query = searchQuery.toLowerCase();
    return items.filter(
      (product) =>
        product.name.toLowerCase().includes(query) ||
        product.code.toLowerCase().includes(query)
    );
  }, [items, searchQuery]);

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  const preventZoom = (e: React.TouchEvent | React.MouseEvent) => {
    if (e.type === 'touchstart' && (e as React.TouchEvent).touches.length > 1) {
      e.preventDefault();
    }
  };

  const handleCreate = (e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    setEditingProduct(undefined);
    setDialogOpen(true);
  };

  const handleEdit = (product: Product, e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    setEditingProduct(product);
    setDialogOpen(true);
  };

  const handleDelete = (product: Product, e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    setProductToDelete(product);
    setDeleteConfirmOpen(true);
  };

  const confirmDelete = () => {
    if (productToDelete) {
      dispatch(deleteProduct(productToDelete.code));
      setDeleteConfirmOpen(false);
      setProductToDelete(null);
    }
  };

  const handleSubmit = async (data: CreateProductRequest | UpdateProductRequest) => {
    if (editingProduct) {
      await dispatch(updateProduct({ code: editingProduct.code, request: data as UpdateProductRequest }));
    } else {
      await dispatch(createProduct(data as CreateProductRequest));
    }
    setDialogOpen(false);
    setEditingProduct(undefined);
  };

  const handleManageRawMaterials = (product: Product, e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    setSelectedProduct(product);
    setRawMaterialsOpen(true);
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  return (
    <Box>
      <Box
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          justifyContent: 'space-between',
          alignItems: { xs: 'flex-start', sm: 'center' },
          mb: 4,
          gap: { xs: 2, sm: 0 },
        }}
      >
        <Box>
          <Typography
            variant="h4"
            sx={{
              fontWeight: 700,
              color: '#0c0f3d',
              fontSize: { xs: '1.75rem', sm: '2.25rem' },
              mb: 0.5,
              letterSpacing: '-0.02em',
            }}
          >
            {t.products.title}
          </Typography>
          <Typography
            variant="body2"
            sx={{
              color: '#64748b',
              fontSize: { xs: '0.875rem', sm: '0.95rem' },
              fontWeight: 400,
            }}
          >
            {t.products.subtitle}
          </Typography>
        </Box>
        <Box
          sx={{
            display: 'flex',
            flexDirection: { xs: 'column', sm: 'row' },
            gap: 2,
            alignItems: 'stretch',
            width: { xs: '100%', sm: 'auto' },
          }}
        >
          <TextField
            placeholder={t.products.search}
            size="medium"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onTouchStart={(e) => e.stopPropagation()}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon sx={{ color: '#94a3b8', fontSize: 20 }} />
                </InputAdornment>
              ),
            }}
            sx={{
              width: { xs: '100%', sm: 350 },
              touchAction: 'manipulation',
              '& .MuiOutlinedInput-root': {
                borderRadius: '25px',
                backgroundColor: 'white',
                border: '1px solid rgba(0, 0, 0, 0.08)',
                transition: 'all 0.2s ease-in-out',
                touchAction: 'manipulation',
                boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.03)',
                '&:hover': {
                  borderColor: '#1192d4',
                  boxShadow: '0 2px 4px 0 rgba(0, 0, 0, 0.05)',
                },
                '&.Mui-focused': {
                  borderColor: '#1192d4',
                  boxShadow: '0 2px 8px 0 rgba(17, 146, 212, 0.15)',
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#1192d4',
                  },
                },
                '& .MuiOutlinedInput-notchedOutline': {
                  borderColor: 'transparent',
                },
              },
              '& .MuiInputBase-input': {
                padding: '10px 16px 10px 8px',
                fontSize: '0.95rem',
                color: '#1e293b',
                '&::placeholder': {
                  color: '#94a3b8',
                  opacity: 1,
                },
              },
            }}
          />
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreate}
            onTouchStart={preventZoom}
            size="large"
            fullWidth={false}
            sx={{
              width: { xs: '100%', sm: 'auto' },
              whiteSpace: 'nowrap',
              touchAction: 'manipulation',
            }}
          >
            {t.products.create}
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading && filteredItems.length === 0 ? (
        <LoadingSpinner />
      ) : filteredItems.length === 0 ? (
        <Paper
          sx={{
            p: { xs: 4, sm: 6 },
            textAlign: 'center',
            background: 'linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)',
          }}
        >
          <Typography
            variant="h6"
            color="text.secondary"
            gutterBottom
            sx={{
              fontSize: { xs: '1rem', sm: '1.25rem' },
            }}
          >
            {t.products.noProducts}
          </Typography>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              mb: { xs: 2, sm: 3 },
              fontSize: { xs: '0.875rem', sm: '1rem' },
            }}
          >
            {t.products.noProductsDescription}
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreate}
            onTouchStart={preventZoom}
            sx={{
              width: { xs: '100%', sm: 'auto' },
              touchAction: 'manipulation',
            }}
          >
            {t.products.createProduct}
          </Button>
        </Paper>
      ) : (
        <>
          {/* List for Mobile */}
          <Box sx={{ display: { xs: 'block', md: 'none' } }}>
            <List sx={{ p: 0 }}>
              {filteredItems.map((product) => (
                <ListItem
                  key={product.code}
                  sx={{
                    border: '1px solid rgba(0, 0, 0, 0.06)',
                    borderRadius: 2,
                    mb: 1.5,
                    bgcolor: 'white',
                    '&:last-child': {
                      mb: 0,
                    },
                  }}
                  secondaryAction={
                    <Box sx={{ display: 'flex', gap: 0.5 }}>
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={(e) => handleManageRawMaterials(product, e)}
                        sx={{
                          color: '#1192d4',
                          minWidth: 44,
                          minHeight: 44,
                        }}
                      >
                        <SettingsIcon />
                      </IconButton>
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={(e) => handleEdit(product, e)}
                        sx={{
                          color: '#64748b',
                          minWidth: 44,
                          minHeight: 44,
                        }}
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={(e) => handleDelete(product, e)}
                        sx={{
                          color: '#ef4444',
                          minWidth: 44,
                          minHeight: 44,
                        }}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Box>
                  }
                >
                  <ListItemText
                    primary={
                      <Box>
                        <Typography
                          variant="caption"
                          sx={{
                            fontFamily: 'monospace',
                            fontWeight: 600,
                            color: '#1192d4',
                            fontSize: '0.75rem',
                            display: 'block',
                            mb: 0.5,
                          }}
                        >
                          {product.code}
                        </Typography>
                        <Typography
                          variant="body1"
                          sx={{
                            fontWeight: 600,
                            color: '#0c0f3d',
                            fontSize: '1rem',
                            mb: 0.5,
                          }}
                        >
                          {product.name}
                        </Typography>
                        <Typography
                          variant="h6"
                          sx={{
                            fontWeight: 700,
                            color: '#22c55e',
                            fontSize: '1.25rem',
                          }}
                        >
                          {formatCurrency(product.value)}
                        </Typography>
                      </Box>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </Box>

          {/* Table for Desktop */}
          <TableContainer
            component={Paper}
            elevation={0}
            sx={{
              display: { xs: 'none', md: 'block' },
              overflowX: 'auto',
              touchAction: 'pan-x pan-y',
              '&::-webkit-scrollbar': {
                height: '8px',
              },
              '&::-webkit-scrollbar-thumb': {
                backgroundColor: '#cbd5e1',
                borderRadius: '4px',
              },
            }}
          >
            <Table sx={{ minWidth: 600 }}>
            <TableHead>
              <TableRow>
                <TableCell>{t.products.code}</TableCell>
                <TableCell>{t.products.name}</TableCell>
                <TableCell>{t.products.value}</TableCell>
                <TableCell align="right">{t.products.actions}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredItems.map((product) => (
                <TableRow key={product.code}>
                  <TableCell
                    sx={{
                      py: { xs: 1.5, sm: 2 },
                      px: { xs: 2, sm: 3 },
                    }}
                  >
                    <Typography
                      variant="body2"
                      sx={{
                        fontFamily: 'monospace',
                        fontWeight: 600,
                        color: 'primary.main',
                        fontSize: { xs: '0.75rem', sm: '0.875rem' },
                      }}
                    >
                      {product.code}
                    </Typography>
                  </TableCell>
                  <TableCell
                    sx={{
                      py: { xs: 1.5, sm: 2 },
                      px: { xs: 2, sm: 3 },
                    }}
                  >
                    <Typography
                      variant="body1"
                      sx={{
                        fontWeight: 500,
                        fontSize: { xs: '0.875rem', sm: '1rem' },
                      }}
                    >
                      {product.name}
                    </Typography>
                  </TableCell>
                  <TableCell
                    sx={{
                      py: { xs: 1.5, sm: 2 },
                      px: { xs: 2, sm: 3 },
                    }}
                  >
                    <Typography
                      variant="body1"
                      sx={{
                        fontWeight: 600,
                        color: 'success.main',
                        fontSize: { xs: '0.875rem', sm: '1rem' },
                      }}
                    >
                      {formatCurrency(product.value)}
                    </Typography>
                  </TableCell>
                  <TableCell
                    align="right"
                    sx={{
                      py: { xs: 0.5, sm: 1.5 },
                      px: { xs: 0.5, sm: 2 },
                    }}
                  >
                    <Box
                      sx={{
                        display: 'flex',
                        gap: { xs: 0.25, sm: 0.5 },
                        justifyContent: 'flex-end',
                        flexWrap: 'nowrap',
                      }}
                    >
                      <IconButton
                        size="small"
                        onClick={(e) => handleManageRawMaterials(product, e)}
                        onTouchStart={preventZoom}
                        title={t.products.manageRawMaterials}
                        aria-label={t.products.manageRawMaterials}
                        sx={{
                          color: 'primary.main',
                          padding: { xs: '4px', sm: '8px' },
                          touchAction: 'manipulation',
                          '&:hover': {
                            backgroundColor: 'primary.light',
                            color: 'white',
                          },
                          '& .MuiSvgIcon-root': {
                            fontSize: { xs: '16px', sm: '20px' },
                          },
                        }}
                      >
                        <SettingsIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={(e) => handleEdit(product, e)}
                        onTouchStart={preventZoom}
                        title={t.products.edit}
                        sx={{
                          color: 'text.secondary',
                          padding: { xs: '4px', sm: '8px' },
                          touchAction: 'manipulation',
                          '&:hover': {
                            backgroundColor: 'primary.light',
                            color: 'white',
                          },
                          '& .MuiSvgIcon-root': {
                            fontSize: { xs: '16px', sm: '20px' },
                          },
                        }}
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={(e) => handleDelete(product, e)}
                        onTouchStart={preventZoom}
                        title={t.products.delete}
                        sx={{
                          color: 'error.main',
                          padding: { xs: '4px', sm: '8px' },
                          touchAction: 'manipulation',
                          '&:hover': {
                            backgroundColor: 'error.light',
                            color: 'white',
                          },
                          '& .MuiSvgIcon-root': {
                            fontSize: { xs: '16px', sm: '20px' },
                          },
                        }}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        </>
      )}

      <ProductDialog
        open={dialogOpen}
        product={editingProduct}
        onClose={() => {
          setDialogOpen(false);
          setEditingProduct(undefined);
        }}
        onSubmit={handleSubmit}
        loading={loading}
      />

      <Dialog
        open={deleteConfirmOpen}
        onClose={() => setDeleteConfirmOpen(false)}
        PaperProps={{
          sx: {
            borderRadius: { xs: 2, sm: 3 },
            m: { xs: 2, sm: 3 },
          },
        }}
      >
        <DialogTitle
          sx={{
            fontSize: { xs: '1.1rem', sm: '1.25rem' },
            px: { xs: 2, sm: 3 },
            pt: { xs: 2, sm: 3 },
          }}
        >
          {t.products.confirm}
        </DialogTitle>
        <DialogContent sx={{ px: { xs: 2, sm: 3 } }}>
          <DialogContentText
            sx={{
              fontSize: { xs: '0.9rem', sm: '1rem' },
            }}
          >
            {t.products.deleteConfirm} "{productToDelete?.name}"? {t.products.deleteConfirmDescription}
          </DialogContentText>
        </DialogContent>
        <DialogActions
          sx={{
            p: { xs: 2, sm: 3 },
            flexDirection: { xs: 'column-reverse', sm: 'row' },
            gap: { xs: 1, sm: 0 },
          }}
        >
          <Button
            onClick={() => setDeleteConfirmOpen(false)}
            fullWidth={false}
            sx={{
              width: { xs: '100%', sm: 'auto' },
            }}
          >
            {t.products.cancel}
          </Button>
          <Button
            onClick={confirmDelete}
            color="error"
            variant="contained"
            fullWidth={false}
            sx={{
              width: { xs: '100%', sm: 'auto' },
            }}
          >
            {t.products.delete}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={rawMaterialsOpen}
        onClose={() => {
          setRawMaterialsOpen(false);
          setSelectedProduct(null);
        }}
        maxWidth="md"
        fullWidth
        PaperProps={{
          sx: {
            borderRadius: { xs: 2, sm: 3 },
            m: { xs: 1, sm: 3 },
            maxHeight: { xs: '95vh', sm: '90vh' },
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
          {t.productRawMaterials.title} - {selectedProduct?.name}
        </DialogTitle>
        <DialogContent
          sx={{
            pt: { xs: 2, sm: 3 },
            px: { xs: 2, sm: 3 },
            pb: { xs: 1, sm: 2 },
            overflowY: 'auto',
          }}
        >
          {selectedProduct && (
            <ProductRawMaterialManager
              productCode={selectedProduct.code}
              onClose={() => {
                setRawMaterialsOpen(false);
                setSelectedProduct(null);
              }}
            />
          )}
        </DialogContent>
      </Dialog>
    </Box>
  );
};
