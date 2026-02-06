import { Card, CardContent, Box, Typography, Chip } from '@mui/material';
import { useI18n } from '@/i18n';
import { ProductionSuggestion } from '@/types/production';

interface ProductionCardProps {
  suggestion: ProductionSuggestion;
  index: number;
}

export const ProductionCard = ({ suggestion, index }: ProductionCardProps) => {
  const { t } = useI18n();
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  return (
    <Card
      elevation={0}
      sx={{
        border: '1px solid rgba(0, 0, 0, 0.06)',
        borderRadius: 3,
        mb: 2,
        transition: 'all 0.2s ease-in-out',
        '&:hover': {
          boxShadow: '0 4px 12px 0 rgba(0, 0, 0, 0.08)',
          transform: 'translateY(-2px)',
        },
      }}
    >
      <CardContent sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box sx={{ flex: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1 }}>
              <Chip
                label={`#${index + 1}`}
                size="small"
                sx={{
                  backgroundColor: '#dbeafe',
                  color: '#1192d4',
                  fontWeight: 600,
                  fontSize: '0.75rem',
                  height: 24,
                }}
              />
              <Typography
                variant="caption"
                sx={{
                  fontFamily: 'monospace',
                  fontWeight: 600,
                  color: '#1192d4',
                  fontSize: '0.75rem',
                }}
              >
                {suggestion.product.code}
              </Typography>
            </Box>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 600,
                color: '#0c0f3d',
                fontSize: '1.1rem',
                mb: 0.5,
              }}
            >
              {suggestion.product.name}
            </Typography>
            <Typography
              variant="caption"
              sx={{
                color: '#94a3b8',
                fontSize: '0.75rem',
                display: 'block',
                mb: 2,
              }}
            >
              {formatCurrency(suggestion.product.value)}/un
            </Typography>
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                pt: 2,
                borderTop: '1px solid rgba(0, 0, 0, 0.06)',
              }}
            >
              <Box>
                <Typography
                  variant="caption"
                  sx={{
                    color: '#64748b',
                    fontSize: '0.75rem',
                    display: 'block',
                    mb: 0.5,
                  }}
                >
                  {t.production.quantity}
                </Typography>
                <Typography
                  variant="body1"
                  sx={{
                    fontWeight: 600,
                    color: '#1192d4',
                    fontSize: '1rem',
                  }}
                >
                  {suggestion.producibleQuantity} UN
                </Typography>
              </Box>
              <Box sx={{ textAlign: 'right' }}>
                <Typography
                  variant="caption"
                  sx={{
                    color: '#64748b',
                    fontSize: '0.75rem',
                    display: 'block',
                    mb: 0.5,
                  }}
                >
                  {t.production.subtotal}
                </Typography>
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 700,
                    color: '#22c55e',
                    fontSize: '1.25rem',
                  }}
                >
                  {formatCurrency(suggestion.totalValue)}
                </Typography>
              </Box>
            </Box>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};
