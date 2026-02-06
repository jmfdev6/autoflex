import { useRef, useEffect } from 'react';
import { Card, CardContent, Box, Typography, IconButton, Chip } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SettingsIcon from '@mui/icons-material/Settings';
import { Product } from '@/types/product';

interface ProductCardProps {
  product: Product;
  onEdit: (product: Product) => void;
  onDelete: (product: Product) => void;
  onManageRawMaterials: (product: Product) => void;
}

export const ProductCard = ({
  product,
  onEdit,
  onDelete,
  onManageRawMaterials,
}: ProductCardProps) => {
  const manageRef = useRef<HTMLButtonElement>(null);
  const editRef = useRef<HTMLButtonElement>(null);
  const deleteRef = useRef<HTMLButtonElement>(null);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  // Track touch start position to detect if it's a tap or scroll
  useEffect(() => {
    const manageBtn = manageRef.current;
    let touchStartX = 0;
    let touchStartY = 0;

    const handleTouchStart = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        touchStartX = e.touches[0].clientX;
        touchStartY = e.touches[0].clientY;
      }
    };

    const handleTouchEnd = (e: TouchEvent, action: () => void, button: HTMLButtonElement) => {
      if (e.changedTouches.length > 0) {
        const touchEndX = e.changedTouches[0].clientX;
        const touchEndY = e.changedTouches[0].clientY;
        const deltaX = Math.abs(touchEndX - touchStartX);
        const deltaY = Math.abs(touchEndY - touchStartY);
        
        // Only trigger if it's a tap (not a scroll) - threshold of 10px
        if (deltaX < 10 && deltaY < 10) {
          if (e.cancelable) {
            e.preventDefault();
          }
          e.stopPropagation();
          action();
          // Remove focus after action
          setTimeout(() => {
            button.blur();
          }, 100);
        }
      }
    };

    if (manageBtn) {
      const startHandler = handleTouchStart;
      const endHandler = (e: TouchEvent) => handleTouchEnd(e, () => onManageRawMaterials(product), manageBtn);
      manageBtn.addEventListener('touchstart', startHandler, { passive: true });
      manageBtn.addEventListener('touchend', endHandler, { passive: false });
      return () => {
        manageBtn.removeEventListener('touchstart', startHandler);
        manageBtn.removeEventListener('touchend', endHandler);
      };
    }
  }, [product, onManageRawMaterials]);

  useEffect(() => {
    const editBtn = editRef.current;
    let touchStartX = 0;
    let touchStartY = 0;

    const handleTouchStart = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        touchStartX = e.touches[0].clientX;
        touchStartY = e.touches[0].clientY;
      }
    };

    const handleTouchEnd = (e: TouchEvent, action: () => void, button: HTMLButtonElement) => {
      if (e.changedTouches.length > 0) {
        const touchEndX = e.changedTouches[0].clientX;
        const touchEndY = e.changedTouches[0].clientY;
        const deltaX = Math.abs(touchEndX - touchStartX);
        const deltaY = Math.abs(touchEndY - touchStartY);
        
        // Only trigger if it's a tap (not a scroll) - threshold of 10px
        if (deltaX < 10 && deltaY < 10) {
          if (e.cancelable) {
            e.preventDefault();
          }
          e.stopPropagation();
          action();
          // Remove focus after action
          setTimeout(() => {
            button.blur();
          }, 100);
        }
      }
    };

    if (editBtn) {
      const startHandler = handleTouchStart;
      const endHandler = (e: TouchEvent) => handleTouchEnd(e, () => onEdit(product), editBtn);
      editBtn.addEventListener('touchstart', startHandler, { passive: true });
      editBtn.addEventListener('touchend', endHandler, { passive: false });
      return () => {
        editBtn.removeEventListener('touchstart', startHandler);
        editBtn.removeEventListener('touchend', endHandler);
      };
    }
  }, [product, onEdit]);

  useEffect(() => {
    const deleteBtn = deleteRef.current;
    let touchStartX = 0;
    let touchStartY = 0;

    const handleTouchStart = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        touchStartX = e.touches[0].clientX;
        touchStartY = e.touches[0].clientY;
      }
    };

    const handleTouchEnd = (e: TouchEvent, action: () => void, button: HTMLButtonElement) => {
      if (e.changedTouches.length > 0) {
        const touchEndX = e.changedTouches[0].clientX;
        const touchEndY = e.changedTouches[0].clientY;
        const deltaX = Math.abs(touchEndX - touchStartX);
        const deltaY = Math.abs(touchEndY - touchStartY);
        
        // Only trigger if it's a tap (not a scroll) - threshold of 10px
        if (deltaX < 10 && deltaY < 10) {
          if (e.cancelable) {
            e.preventDefault();
          }
          e.stopPropagation();
          action();
          // Remove focus after action
          setTimeout(() => {
            button.blur();
          }, 100);
        }
      }
    };

    if (deleteBtn) {
      const startHandler = handleTouchStart;
      const endHandler = (e: TouchEvent) => handleTouchEnd(e, () => onDelete(product), deleteBtn);
      deleteBtn.addEventListener('touchstart', startHandler, { passive: true });
      deleteBtn.addEventListener('touchend', endHandler, { passive: false });
      return () => {
        deleteBtn.removeEventListener('touchstart', startHandler);
        deleteBtn.removeEventListener('touchend', endHandler);
      };
    }
  }, [product, onDelete]);

  const handleAction = (
    e: React.MouseEvent<HTMLButtonElement>,
    action: () => void
  ) => {
    e.preventDefault();
    e.stopPropagation();
    action();
    // Remove focus after action
    setTimeout(() => {
      e.currentTarget.blur();
    }, 100);
  };

  return (
    <Card
      elevation={0}
      sx={{
        border: '1px solid rgba(0, 0, 0, 0.06)',
        borderRadius: 3,
        mb: 2,
        transition: 'all 0.2s ease-in-out',
        position: 'relative',
        touchAction: 'pan-y',
        '&:hover': {
          boxShadow: '0 4px 12px 0 rgba(0, 0, 0, 0.08)',
          transform: 'translateY(-2px)',
        },
      }}
    >
      <CardContent 
        sx={{ 
          p: 3,
          '& > *': {
            pointerEvents: 'auto',
          },
        }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box sx={{ flex: 1 }}>
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
              variant="h6"
              sx={{
                fontWeight: 600,
                color: '#0c0f3d',
                fontSize: '1.1rem',
                mb: 1.5,
              }}
            >
              {product.name}
            </Typography>
            <Typography
              variant="h5"
              sx={{
                fontWeight: 700,
                color: '#22c55e',
                fontSize: '1.5rem',
              }}
            >
              {formatCurrency(product.value)}
            </Typography>
          </Box>
          <Box
            sx={{
              display: 'flex',
              gap: 0.5,
              position: 'relative',
              zIndex: 10,
              pointerEvents: 'auto',
            }}
          >
            <IconButton
              ref={manageRef}
              size="small"
              onClick={(e) => handleAction(e, () => onManageRawMaterials(product))}
              sx={{
                color: '#1192d4',
                touchAction: 'manipulation',
                WebkitTapHighlightColor: 'transparent',
                minWidth: 44,
                minHeight: 44,
                padding: '8px',
                pointerEvents: 'auto',
                position: 'relative',
                zIndex: 11,
                userSelect: 'none',
                '&:focus': {
                  outline: 'none',
                },
                '&:focus-visible': {
                  outline: 'none',
                },
                '&.Mui-focusVisible': {
                  outline: 'none',
                },
              }}
            >
              <SettingsIcon fontSize="small" />
            </IconButton>
            <IconButton
              ref={editRef}
              size="small"
              onClick={(e) => handleAction(e, () => onEdit(product))}
              sx={{
                color: '#64748b',
                touchAction: 'manipulation',
                WebkitTapHighlightColor: 'transparent',
                minWidth: 44,
                minHeight: 44,
                padding: '8px',
                pointerEvents: 'auto',
                position: 'relative',
                zIndex: 11,
                userSelect: 'none',
                '&:focus': {
                  outline: 'none',
                },
                '&:focus-visible': {
                  outline: 'none',
                },
                '&.Mui-focusVisible': {
                  outline: 'none',
                },
              }}
            >
              <EditIcon fontSize="small" />
            </IconButton>
            <IconButton
              ref={deleteRef}
              size="small"
              onClick={(e) => handleAction(e, () => onDelete(product))}
              sx={{
                color: '#ef4444',
                touchAction: 'manipulation',
                WebkitTapHighlightColor: 'transparent',
                minWidth: 44,
                minHeight: 44,
                padding: '8px',
                pointerEvents: 'auto',
                position: 'relative',
                zIndex: 11,
                userSelect: 'none',
                '&:focus': {
                  outline: 'none',
                },
                '&:focus-visible': {
                  outline: 'none',
                },
                '&.Mui-focusVisible': {
                  outline: 'none',
                },
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};
