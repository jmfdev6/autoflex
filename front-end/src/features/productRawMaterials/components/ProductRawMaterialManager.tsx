import { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  TextField,
  Button,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useI18n } from '@/i18n';
import {
  fetchProductRawMaterials,
  createProductRawMaterial,
  updateProductRawMaterial,
  deleteProductRawMaterial,
} from '@/store/slices/productRawMaterialsSlice';
import { fetchRawMaterials } from '@/store/slices/rawMaterialsSlice';
import { RawMaterialSelector } from './RawMaterialSelector';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ProductRawMaterial } from '@/types/productRawMaterial';

interface ProductRawMaterialManagerProps {
  productCode: string;
  onClose: () => void;
}

export const ProductRawMaterialManager = ({
  productCode,
  onClose,
}: ProductRawMaterialManagerProps) => {
  const dispatch = useAppDispatch();
  const { t } = useI18n();
  const { items, loading, error } = useAppSelector((state) => state.productRawMaterials);
  const { items: rawMaterials } = useAppSelector((state) => state.rawMaterials);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editQuantity, setEditQuantity] = useState('');

  useEffect(() => {
    dispatch(fetchProductRawMaterials(productCode));
    dispatch(fetchRawMaterials());
  }, [dispatch, productCode]);

  const getRawMaterialName = (code: string) => {
    return rawMaterials.find((rm) => rm.code === code)?.name || code;
  };

  const handleAdd = async (request: { rawMaterialCode: string; quantity: number }) => {
    await dispatch(createProductRawMaterial({ productCode, request }));
  };

  const handleEdit = (item: ProductRawMaterial) => {
    setEditingId(`${item.productCode}-${item.rawMaterialCode}`);
    setEditQuantity(item.quantity.toString());
  };

  const handleSave = async (item: ProductRawMaterial) => {
    const quantity = parseFloat(editQuantity);
    if (!isNaN(quantity) && quantity > 0) {
      await dispatch(
        updateProductRawMaterial({
          productCode: item.productCode,
          rawMaterialCode: item.rawMaterialCode,
          request: { quantity },
        })
      );
      setEditingId(null);
      setEditQuantity('');
    }
  };

  const handleCancel = () => {
    setEditingId(null);
    setEditQuantity('');
  };

  const handleDelete = async (item: ProductRawMaterial) => {
    await dispatch(
      deleteProductRawMaterial({
        productCode: item.productCode,
        rawMaterialCode: item.rawMaterialCode,
      })
    );
  };

  const existingCodes = items.map((item) => item.rawMaterialCode);

  return (
    <Box>
      <Typography
        variant="h6"
        gutterBottom
        sx={{
          fontSize: { xs: '1rem', sm: '1.25rem' },
          mb: { xs: 2, sm: 3 },
        }}
      >
        {t.productRawMaterials.title} - {productCode}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ mb: { xs: 2, sm: 3 } }}>
        <RawMaterialSelector
          onAdd={handleAdd}
          existingRawMaterialCodes={existingCodes}
          loading={loading}
        />
      </Box>

      {loading && items.length === 0 ? (
        <LoadingSpinner />
      ) : items.length === 0 ? (
        <Alert severity="info">{t.productRawMaterials.noRawMaterials}</Alert>
      ) : (
        <>
          {/* List for Mobile */}
          <Box sx={{ display: { xs: 'block', md: 'none' } }}>
            <List sx={{ p: 0 }}>
              {items.map((item) => {
                const isEditing =
                  editingId === `${item.productCode}-${item.rawMaterialCode}`;
                return (
                  <ListItem
                    key={`${item.productCode}-${item.rawMaterialCode}`}
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
                        {isEditing ? (
                          <>
                            <IconButton
                              edge="end"
                              size="small"
                              onClick={() => handleSave(item)}
                              color="primary"
                              sx={{
                                minWidth: 44,
                                minHeight: 44,
                              }}
                            >
                              <SaveIcon />
                            </IconButton>
                            <IconButton
                              edge="end"
                              size="small"
                              onClick={handleCancel}
                              sx={{
                                minWidth: 44,
                                minHeight: 44,
                              }}
                            >
                              <CancelIcon />
                            </IconButton>
                          </>
                        ) : (
                          <>
                            <IconButton
                              edge="end"
                              size="small"
                              onClick={() => handleEdit(item)}
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
                              onClick={() => handleDelete(item)}
                              color="error"
                              sx={{
                                minWidth: 44,
                                minHeight: 44,
                              }}
                            >
                              <DeleteIcon />
                            </IconButton>
                          </>
                        )}
                      </Box>
                    }
                  >
                    <ListItemText
                      primary={
                        <Box>
                          <Typography
                            variant="body1"
                            sx={{
                              fontSize: '1rem',
                              fontWeight: 600,
                              color: '#0c0f3d',
                              mb: 0.5,
                            }}
                          >
                            {getRawMaterialName(item.rawMaterialCode)}
                          </Typography>
                          <Typography
                            variant="caption"
                            sx={{
                              fontFamily: 'monospace',
                              fontSize: '0.75rem',
                              fontWeight: 600,
                              color: '#1192d4',
                              display: 'block',
                              mb: 1,
                            }}
                          >
                            {item.rawMaterialCode}
                          </Typography>
                          {isEditing ? (
                            <TextField
                              type="number"
                              value={editQuantity}
                              onChange={(e) => setEditQuantity(e.target.value)}
                              size="small"
                              inputProps={{ min: 0, step: 0.01 }}
                              sx={{
                                width: 120,
                                '& .MuiInputBase-input': {
                                  fontSize: '0.875rem',
                                },
                              }}
                            />
                          ) : (
                            <Typography
                              variant="body2"
                              sx={{
                                fontSize: '0.875rem',
                                fontWeight: 500,
                                color: '#64748b',
                              }}
                            >
                              {t.productRawMaterials.quantity}: {item.quantity}
                            </Typography>
                          )}
                        </Box>
                      }
                    />
                  </ListItem>
                );
              })}
            </List>
          </Box>

          {/* Table for Desktop */}
          <TableContainer
            component={Paper}
            elevation={0}
            sx={{
              display: { xs: 'none', md: 'block' },
              overflowX: 'auto',
              '&::-webkit-scrollbar': {
                height: '8px',
              },
              '&::-webkit-scrollbar-thumb': {
                backgroundColor: '#cbd5e1',
                borderRadius: '4px',
              },
            }}
          >
            <Table sx={{ minWidth: 500 }}>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 600, fontSize: { xs: '0.875rem', sm: '1rem' } }}>
                    {t.productRawMaterials.rawMaterial}
                  </TableCell>
                  <TableCell sx={{ fontWeight: 600, fontSize: { xs: '0.875rem', sm: '1rem' } }}>
                    {t.products.code}
                  </TableCell>
                  <TableCell sx={{ fontWeight: 600, fontSize: { xs: '0.875rem', sm: '1rem' } }}>
                    {t.productRawMaterials.quantity}
                  </TableCell>
                  <TableCell
                    align="right"
                    sx={{ fontWeight: 600, fontSize: { xs: '0.875rem', sm: '1rem' } }}
                  >
                    {t.products.actions}
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {items.map((item) => {
                  const isEditing =
                    editingId === `${item.productCode}-${item.rawMaterialCode}`;
                  return (
                    <TableRow
                      key={`${item.productCode}-${item.rawMaterialCode}`}
                      sx={{
                        '&:hover': {
                          backgroundColor: '#f8fafc',
                        },
                        transition: 'background-color 0.2s ease-in-out',
                      }}
                    >
                      <TableCell
                        sx={{
                          py: { xs: 1, sm: 1.5 },
                          px: { xs: 1, sm: 2 },
                        }}
                      >
                        <Typography
                          sx={{
                            fontSize: { xs: '0.875rem', sm: '1rem' },
                            fontWeight: 500,
                          }}
                        >
                          {getRawMaterialName(item.rawMaterialCode)}
                        </Typography>
                      </TableCell>
                      <TableCell
                        sx={{
                          py: { xs: 1, sm: 1.5 },
                          px: { xs: 1, sm: 2 },
                        }}
                      >
                        <Typography
                          sx={{
                            fontFamily: 'monospace',
                            fontSize: { xs: '0.75rem', sm: '0.875rem' },
                            fontWeight: 600,
                            color: 'primary.main',
                          }}
                        >
                          {item.rawMaterialCode}
                        </Typography>
                      </TableCell>
                      <TableCell
                        sx={{
                          py: { xs: 1, sm: 1.5 },
                          px: { xs: 1, sm: 2 },
                        }}
                      >
                        {isEditing ? (
                          <TextField
                            type="number"
                            value={editQuantity}
                            onChange={(e) => setEditQuantity(e.target.value)}
                            size="small"
                            inputProps={{ min: 0, step: 0.01 }}
                            sx={{
                              width: { xs: 80, sm: 100 },
                              '& .MuiInputBase-input': {
                                fontSize: { xs: '0.875rem', sm: '1rem' },
                              },
                            }}
                          />
                        ) : (
                          <Typography
                            sx={{
                              fontSize: { xs: '0.875rem', sm: '1rem' },
                              fontWeight: 500,
                            }}
                          >
                            {item.quantity}
                          </Typography>
                        )}
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
                          {isEditing ? (
                            <>
                              <IconButton
                                size="small"
                                onClick={() => handleSave(item)}
                                color="primary"
                                sx={{
                                  padding: { xs: '4px', sm: '8px' },
                                  '& .MuiSvgIcon-root': {
                                    fontSize: { xs: '16px', sm: '20px' },
                                  },
                                }}
                              >
                                <SaveIcon />
                              </IconButton>
                              <IconButton
                                size="small"
                                onClick={handleCancel}
                                sx={{
                                  padding: { xs: '4px', sm: '8px' },
                                  '& .MuiSvgIcon-root': {
                                    fontSize: { xs: '16px', sm: '20px' },
                                  },
                                }}
                              >
                                <CancelIcon />
                              </IconButton>
                            </>
                          ) : (
                            <>
                              <IconButton
                                size="small"
                                onClick={() => handleEdit(item)}
                                title={t.products.edit}
                                sx={{
                                  padding: { xs: '4px', sm: '8px' },
                                  '& .MuiSvgIcon-root': {
                                    fontSize: { xs: '16px', sm: '20px' },
                                  },
                                }}
                              >
                                <EditIcon />
                              </IconButton>
                              <IconButton
                                size="small"
                                onClick={() => handleDelete(item)}
                                title={t.products.delete}
                                color="error"
                                sx={{
                                  padding: { xs: '4px', sm: '8px' },
                                  '& .MuiSvgIcon-root': {
                                    fontSize: { xs: '16px', sm: '20px' },
                                  },
                                }}
                              >
                                <DeleteIcon />
                              </IconButton>
                            </>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}
    </Box>
  );
};
