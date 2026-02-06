import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Box,
} from '@mui/material';
import { useI18n } from '@/i18n';
import { ProductionSuggestion } from '@/types/production';

interface ProductionTableProps {
  suggestions: ProductionSuggestion[];
}

export const ProductionTable = ({ suggestions }: ProductionTableProps) => {
  const { t } = useI18n();
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  if (suggestions.length === 0) {
    return (
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
          {t.production.noProductsCanBeProduced}
        </Typography>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            fontSize: { xs: '0.875rem', sm: '1rem' },
            mt: 1,
          }}
        >
          {t.production.noProductsCanBeProducedDescription}
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer
      component={Paper}
      elevation={0}
      sx={{
        overflowX: 'auto',
        overflowY: 'hidden',
        WebkitOverflowScrolling: 'touch',
        border: '1px solid rgba(0, 0, 0, 0.06)',
        borderRadius: 3,
        '&::-webkit-scrollbar': {
          height: '8px',
        },
        '&::-webkit-scrollbar-thumb': {
          backgroundColor: '#cbd5e1',
          borderRadius: '4px',
          '&:hover': {
            backgroundColor: '#94a3b8',
          },
        },
        '&::-webkit-scrollbar-track': {
          backgroundColor: '#f1f5f9',
        },
      }}
    >
      <Table
        sx={{
          minWidth: { xs: 650, sm: 750 },
          width: '100%',
        }}
      >
        <TableHead>
          <TableRow>
            <TableCell sx={{ minWidth: 90 }}>{t.production.order}</TableCell>
            <TableCell sx={{ minWidth: 120 }}>{t.production.productCode}</TableCell>
            <TableCell sx={{ minWidth: 220 }}>{t.production.product}</TableCell>
            <TableCell align="right" sx={{ minWidth: 130 }}>{t.production.quantity}</TableCell>
            <TableCell align="right" sx={{ minWidth: 150 }}>{t.production.subtotal}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {suggestions.map((suggestion, index) => (
            <TableRow key={`${suggestion.product.code}-${index}`}>
              <TableCell
                sx={{
                  py: { xs: 1.5, sm: 2 },
                  px: { xs: 2, sm: 3 },
                  whiteSpace: 'nowrap',
                }}
              >
                <Typography
                  variant="body2"
                  sx={{
                    fontFamily: 'monospace',
                    fontWeight: 600,
                    color: '#1192d4',
                    fontSize: { xs: '0.875rem', sm: '1rem' },
                  }}
                >
                  #{index + 1}
                </Typography>
              </TableCell>
              <TableCell
                sx={{
                  py: { xs: 1.5, sm: 2 },
                  px: { xs: 2, sm: 3 },
                  whiteSpace: 'nowrap',
                }}
              >
                <Typography
                  variant="body2"
                  sx={{
                    fontFamily: 'monospace',
                    fontWeight: 600,
                    color: 'primary.main',
                    fontSize: { xs: '0.875rem', sm: '1rem' },
                  }}
                >
                  {suggestion.product.code}
                </Typography>
              </TableCell>
              <TableCell
                sx={{
                  py: { xs: 1.5, sm: 2 },
                  px: { xs: 2, sm: 3 },
                }}
              >
                <Box>
                  <Typography
                    variant="body1"
                    sx={{
                      fontWeight: 600,
                      color: '#0c0f3d',
                      fontSize: { xs: '0.875rem', sm: '1rem' },
                      mb: 0.5,
                    }}
                  >
                    {suggestion.product.name}
                  </Typography>
                  <Typography
                    variant="caption"
                    sx={{
                      color: '#94a3b8',
                      fontSize: { xs: '0.75rem', sm: '0.875rem' },
                    }}
                  >
                    {formatCurrency(suggestion.product.value)}/un
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                align="right"
                sx={{
                  py: { xs: 1.5, sm: 2 },
                  px: { xs: 2, sm: 3 },
                  whiteSpace: 'nowrap',
                }}
              >
                <Typography
                  variant="body1"
                  sx={{
                    fontWeight: 600,
                    color: '#1192d4',
                    fontSize: { xs: '0.875rem', sm: '1rem' },
                  }}
                >
                  {suggestion.producibleQuantity} UN
                </Typography>
              </TableCell>
              <TableCell
                align="right"
                sx={{
                  py: { xs: 1.5, sm: 2 },
                  px: { xs: 2, sm: 3 },
                  whiteSpace: 'nowrap',
                }}
              >
                <Typography
                  variant="body1"
                  sx={{
                    fontWeight: 700,
                    color: '#22c55e',
                    fontSize: { xs: '0.95rem', sm: '1.25rem' },
                  }}
                >
                  {formatCurrency(suggestion.totalValue)}
                </Typography>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
