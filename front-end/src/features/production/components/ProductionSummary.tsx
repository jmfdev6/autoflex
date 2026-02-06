import React from 'react';
import { Paper, Typography, Box, Grid } from '@mui/material';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import FactoryIcon from '@mui/icons-material/Factory';
import InfoIcon from '@mui/icons-material/Info';
import { useI18n } from '@/i18n';
import { ProductionSummary as ProductionSummaryType } from '@/types/production';

interface ProductionSummaryProps {
  summary: ProductionSummaryType;
}

export const ProductionSummary = ({ summary }: ProductionSummaryProps) => {
  const { t } = useI18n();
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  return (
    <Grid container spacing={{ xs: 2, sm: 3 }} sx={{ mb: { xs: 2, sm: 3 } }}>
      {/* Total Production Value Card */}
      <Grid item xs={12} sm={6}>
        <Paper
          elevation={0}
          sx={{
            p: { xs: 2.5, sm: 3.5 },
            background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
            borderRadius: { xs: 2, sm: 3 },
            border: '1px solid rgba(0, 0, 0, 0.06)',
            position: 'relative',
            overflow: 'hidden',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: '3px',
              background: 'linear-gradient(90deg, #0c0f3d 0%, #1192d4 100%)',
            },
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
            <Box
              sx={{
                p: 1,
                borderRadius: 2,
                backgroundColor: '#f1f5f9',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <TrendingUpIcon sx={{ fontSize: { xs: 20, sm: 24 }, color: '#1192d4' }} />
            </Box>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 600,
                color: '#475569',
                fontSize: { xs: '0.95rem', sm: '1.1rem' },
                textTransform: 'uppercase',
                letterSpacing: '0.5px',
              }}
            >
              {t.production.maximumRevenue}
            </Typography>
          </Box>
          <Typography
            variant="h3"
            sx={{
              fontWeight: 700,
              mb: 1.5,
              fontSize: { xs: '2rem', sm: '2.75rem' },
              color: '#0c0f3d',
              lineHeight: 1.1,
            }}
          >
            {formatCurrency(summary.totalValue)}
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <InfoIcon sx={{ fontSize: 14, color: '#94a3b8' }} />
            <Typography
              variant="caption"
              sx={{
                color: '#64748b',
                fontSize: { xs: '0.7rem', sm: '0.75rem' },
              }}
            >
              {t.production.maximumRevenueDescription}
            </Typography>
          </Box>
        </Paper>
      </Grid>

      {/* Production Efficiency Card */}
      <Grid item xs={12} sm={6}>
        <Paper
          elevation={0}
          sx={{
            p: { xs: 2.5, sm: 3.5 },
            background: 'linear-gradient(135deg, #1192d4 0%, #0d7bb8 100%)',
            color: 'white',
            borderRadius: { xs: 2, sm: 3 },
            position: 'relative',
            overflow: 'hidden',
            '&::after': {
              content: '""',
              position: 'absolute',
              bottom: -20,
              right: -20,
              width: 120,
              height: 120,
              borderRadius: '50%',
              background: 'rgba(255, 255, 255, 0.1)',
              opacity: 0.5,
            },
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
            <Box
              sx={{
                p: 1,
                borderRadius: 2,
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <FactoryIcon sx={{ fontSize: { xs: 20, sm: 24 }, color: 'white' }} />
            </Box>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 600,
                color: 'white',
                fontSize: { xs: '0.95rem', sm: '1.1rem' },
                textTransform: 'uppercase',
                letterSpacing: '0.5px',
              }}
            >
              {t.production.productionEfficiency}
            </Typography>
          </Box>
          <Typography
            variant="h3"
            sx={{
              fontWeight: 700,
              mb: 1.5,
              fontSize: { xs: '2rem', sm: '2.75rem' },
              color: 'white',
              lineHeight: 1.1,
            }}
          >
            {summary.suggestions.length}
          </Typography>
          <Typography
            variant="body2"
            sx={{
              color: 'rgba(255, 255, 255, 0.9)',
              fontSize: { xs: '0.8rem', sm: '0.9rem' },
            }}
          >
            {summary.suggestions.length === 1
              ? t.production.productionEfficiencyDescription
              : t.production.productionEfficiencyDescriptionPlural}
          </Typography>
        </Paper>
      </Grid>
    </Grid>
  );
};
