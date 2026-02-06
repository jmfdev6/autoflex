import { useEffect } from 'react';
import { Box, Typography, Button, Alert, Paper } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useI18n } from '@/i18n';
import { fetchProductionSuggestions } from '@/store/slices/productionSlice';
import { ProductionTable } from './ProductionTable';
import { ProductionCard } from './ProductionCard';
import { ProductionSummary } from './ProductionSummary';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export const ProductionSuggestions = () => {
  const dispatch = useAppDispatch();
  const { t } = useI18n();
  const { summary, loading, error } = useAppSelector((state) => state.production);

  useEffect(() => {
    dispatch(fetchProductionSuggestions());
  }, [dispatch]);

  const preventZoom = (e: React.TouchEvent | React.MouseEvent) => {
    if (e.type === 'touchstart' && (e as React.TouchEvent).touches.length > 1) {
      e.preventDefault();
    }
  };

  const handleRefresh = (e?: React.MouseEvent | React.TouchEvent) => {
    e?.preventDefault();
    dispatch(fetchProductionSuggestions());
  };

  return (
    <Box sx={{ width: '100%', overflowX: 'hidden' }}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          justifyContent: 'space-between',
          alignItems: { xs: 'flex-start', sm: 'center' },
          mb: { xs: 3, sm: 4 },
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
            {t.production.title}
          </Typography>
          <Typography
            variant="body2"
            sx={{
              color: '#64748b',
              fontSize: { xs: '0.875rem', sm: '0.95rem' },
              fontWeight: 400,
            }}
          >
            {t.production.subtitle}
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={handleRefresh}
          onTouchStart={preventZoom}
          disabled={loading}
          size="large"
          sx={{
            width: { xs: '100%', sm: 'auto' },
            touchAction: 'manipulation',
          }}
        >
          {t.production.refresh}
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading && !summary ? (
        <LoadingSpinner />
      ) : summary ? (
        <Box>
          <ProductionSummary summary={summary} />
          <Box sx={{ mt: { xs: 4, sm: 5 }, width: '100%' }}>
            {/* Cards for Mobile */}
            <Box sx={{ display: { xs: 'block', md: 'none' } }}>
              {summary.suggestions.map((suggestion, index) => (
                <ProductionCard key={`${suggestion.product.code}-${index}`} suggestion={suggestion} index={index} />
              ))}
            </Box>

            {/* Table for Desktop */}
            <Box sx={{ display: { xs: 'none', md: 'block' } }}>
              <ProductionTable suggestions={summary.suggestions} />
            </Box>
          </Box>
        </Box>
      ) : (
        <Paper
          elevation={0}
          sx={{
            p: { xs: 4, sm: 6 },
            textAlign: 'center',
            background: 'linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)',
            border: '1px solid rgba(0, 0, 0, 0.06)',
            borderRadius: 3,
          }}
        >
          <Typography
            variant="h6"
            color="text.secondary"
            gutterBottom
            sx={{
              fontSize: { xs: '1rem', sm: '1.25rem' },
              fontWeight: 600,
            }}
          >
            {t.production.noSuggestions}
          </Typography>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              fontSize: { xs: '0.875rem', sm: '1rem' },
              mt: 1,
            }}
          >
            {t.production.noSuggestionsDescription}
          </Typography>
        </Paper>
      )}
    </Box>
  );
};
