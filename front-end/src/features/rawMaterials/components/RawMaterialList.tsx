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
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SearchIcon from '@mui/icons-material/Search';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useI18n } from '@/i18n';
import {
  fetchRawMaterials,
  createRawMaterial,
  updateRawMaterial,
  deleteRawMaterial,
} from '@/store/slices/rawMaterialsSlice';
import { RawMaterialDialog } from './RawMaterialDialog';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import {
  RawMaterial,
  CreateRawMaterialRequest,
  UpdateRawMaterialRequest,
} from '@/types/rawMaterial';

export const RawMaterialList = () => {
  const dispatch = useAppDispatch();
  const { t } = useI18n();
  const { items, loading, error } = useAppSelector((state) => state.rawMaterials);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingRawMaterial, setEditingRawMaterial] = useState<RawMaterial | undefined>();
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [rawMaterialToDelete, setRawMaterialToDelete] = useState<RawMaterial | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  const filteredItems = useMemo(() => {
    if (!searchQuery.trim()) return items;
    const query = searchQuery.toLowerCase();
    return items.filter(
      (rawMaterial) =>
        rawMaterial.name.toLowerCase().includes(query) ||
        rawMaterial.code.toLowerCase().includes(query)
    );
  }, [items, searchQuery]);

  useEffect(() => {
    dispatch(fetchRawMaterials());
  }, [dispatch]);

  const preventZoom = (e: React.TouchEvent | React.MouseEvent) => {
    if (e.type === 'touchstart' && (e as React.TouchEvent).touches.length > 1) {
      e.preventDefault();
    }
  };

  const handleCreate = (e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    setEditingRawMaterial(undefined);
    setDialogOpen(true);
  };

  const handleEdit = (rawMaterial: RawMaterial, e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    setEditingRawMaterial(rawMaterial);
    setDialogOpen(true);
  };

  const handleDelete = (rawMaterial: RawMaterial, e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    setRawMaterialToDelete(rawMaterial);
    setDeleteConfirmOpen(true);
  };

  const confirmDelete = () => {
    if (rawMaterialToDelete) {
      dispatch(deleteRawMaterial(rawMaterialToDelete.code));
      setDeleteConfirmOpen(false);
      setRawMaterialToDelete(null);
    }
  };

  const handleSubmit = async (
    data: CreateRawMaterialRequest | UpdateRawMaterialRequest
  ) => {
    if (editingRawMaterial) {
      await dispatch(
        updateRawMaterial({ code: editingRawMaterial.code, request: data as UpdateRawMaterialRequest })
      );
    } else {
      await dispatch(createRawMaterial(data as CreateRawMaterialRequest));
    }
    setDialogOpen(false);
    setEditingRawMaterial(undefined);
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
            {t.rawMaterials.title}
          </Typography>
          <Typography
            variant="body2"
            sx={{
              color: '#64748b',
              fontSize: { xs: '0.875rem', sm: '0.95rem' },
              fontWeight: 400,
            }}
          >
            {t.rawMaterials.subtitle}
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
            placeholder={t.rawMaterials.search}
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
            size="large"
            fullWidth={false}
            sx={{
              width: { xs: '100%', sm: 'auto' },
              whiteSpace: 'nowrap',
            }}
          >
            {t.rawMaterials.create}
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
            p: 6,
            textAlign: 'center',
            background: 'linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)',
          }}
        >
          <Typography variant="h6" color="text.secondary" gutterBottom>
            {t.rawMaterials.noRawMaterials}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            {t.rawMaterials.noRawMaterialsDescription}
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreate}
            onTouchStart={preventZoom}
            sx={{
              touchAction: 'manipulation',
            }}
          >
            {t.rawMaterials.createRawMaterial}
          </Button>
        </Paper>
      ) : (
        <>
          {/* List for Mobile */}
          <Box sx={{ display: { xs: 'block', md: 'none' } }}>
            <List sx={{ p: 0 }}>
              {filteredItems.map((rawMaterial) => {
                const getStatus = () => {
                  if (rawMaterial.stockQuantity > 50) {
                    return {
                      label: t.rawMaterials.status.ok,
                      color: '#22c55e',
                      bgColor: '#dcfce7',
                    };
                  } else if (rawMaterial.stockQuantity > 20) {
                    return {
                      label: t.rawMaterials.status.low,
                      color: '#eab308',
                      bgColor: '#fef9c3',
                    };
                  } else {
                    return {
                      label: t.rawMaterials.status.critical,
                      color: '#ef4444',
                      bgColor: '#fee2e2',
                    };
                  }
                };
                const status = getStatus();
                return (
                  <ListItem
                    key={rawMaterial.code}
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
                          onClick={(e) => handleEdit(rawMaterial, e)}
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
                          onClick={(e) => handleDelete(rawMaterial, e)}
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
                            {rawMaterial.code}
                          </Typography>
                          <Typography
                            variant="body1"
                            sx={{
                              fontWeight: 600,
                              color: '#0c0f3d',
                              fontSize: '1rem',
                              mb: 1,
                            }}
                          >
                            {rawMaterial.name}
                          </Typography>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                            <Typography
                              variant="h6"
                              sx={{
                                fontWeight: 700,
                                color: '#0c0f3d',
                                fontSize: '1.25rem',
                              }}
                            >
                              {rawMaterial.stockQuantity}
                            </Typography>
                            <Chip
                              label={status.label}
                              size="small"
                              sx={{
                                backgroundColor: status.bgColor,
                                color: status.color,
                                fontWeight: 600,
                                fontSize: '0.75rem',
                                height: 24,
                              }}
                            />
                          </Box>
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
                <TableCell>{t.rawMaterials.code}</TableCell>
                <TableCell>{t.rawMaterials.name}</TableCell>
                <TableCell>{t.rawMaterials.stockQuantity}</TableCell>
                <TableCell align="right">{t.rawMaterials.actions}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredItems.map((rawMaterial) => (
                <TableRow key={rawMaterial.code}>
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
                      {rawMaterial.code}
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
                      {rawMaterial.name}
                    </Typography>
                  </TableCell>
                  <TableCell
                    sx={{
                      py: { xs: 1.5, sm: 2 },
                      px: { xs: 2, sm: 3 },
                    }}
                  >
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <Typography
                        variant="body1"
                        sx={{
                          fontWeight: 700,
                          color: '#1e293b',
                          fontSize: { xs: '1rem', sm: '1.25rem' },
                        }}
                      >
                        {rawMaterial.stockQuantity}
                      </Typography>
                      <Chip
                        icon={
                          rawMaterial.stockQuantity > 50 ? (
                            <CheckCircleIcon sx={{ fontSize: 14 }} />
                          ) : rawMaterial.stockQuantity > 20 ? (
                            <WarningIcon sx={{ fontSize: 14 }} />
                          ) : (
                            <ErrorIcon sx={{ fontSize: 14 }} />
                          )
                        }
                        label={
                          rawMaterial.stockQuantity > 50
                            ? t.rawMaterials.status.ok
                            : rawMaterial.stockQuantity > 20
                            ? t.rawMaterials.status.low
                            : t.rawMaterials.status.critical
                        }
                        size="small"
                        sx={{
                          backgroundColor:
                            rawMaterial.stockQuantity > 50
                              ? '#dcfce7'
                              : rawMaterial.stockQuantity > 20
                              ? '#fef9c3'
                              : '#fee2e2',
                          color:
                            rawMaterial.stockQuantity > 50
                              ? '#166534'
                              : rawMaterial.stockQuantity > 20
                              ? '#854d0e'
                              : '#991b1b',
                          fontWeight: 600,
                          fontSize: { xs: '0.7rem', sm: '0.75rem' },
                          height: { xs: 22, sm: 26 },
                          '& .MuiChip-icon': {
                            marginLeft: '8px',
                            color: 'inherit',
                          },
                        }}
                      />
                    </Box>
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
                        onClick={(e) => handleEdit(rawMaterial, e)}
                        onTouchStart={preventZoom}
                        title={t.rawMaterials.edit}
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
                        onClick={(e) => handleDelete(rawMaterial, e)}
                        onTouchStart={preventZoom}
                        title={t.rawMaterials.delete}
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

      <RawMaterialDialog
        open={dialogOpen}
        rawMaterial={editingRawMaterial}
        onClose={() => {
          setDialogOpen(false);
          setEditingRawMaterial(undefined);
        }}
        onSubmit={handleSubmit}
        loading={loading}
      />

      <Dialog open={deleteConfirmOpen} onClose={() => setDeleteConfirmOpen(false)}>
        <DialogTitle>{t.rawMaterials.confirm}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t.rawMaterials.deleteConfirm} "{rawMaterialToDelete?.name}"? {t.rawMaterials.deleteConfirmDescription}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteConfirmOpen(false)}>{t.rawMaterials.cancel}</Button>
          <Button onClick={confirmDelete} color="error" variant="contained">
            {t.rawMaterials.delete}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
