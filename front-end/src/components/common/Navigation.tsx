import { AppBar, Toolbar, Typography, Button, Box, Chip } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import InventoryIcon from '@mui/icons-material/Inventory';
import CategoryIcon from '@mui/icons-material/Category';
import ScienceIcon from '@mui/icons-material/Science';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

const navItems = [
  { label: 'Products', path: '/products', icon: CategoryIcon },
  { label: 'Raw Materials', path: '/raw-materials', icon: ScienceIcon },
  { label: 'Production', path: '/production', icon: TrendingUpIcon },
];

export const Navigation = () => {
  const location = useLocation();

  return (
    <AppBar
      position="static"
      sx={{
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
      }}
    >
      <Toolbar sx={{ py: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, flexGrow: 1 }}>
          <InventoryIcon sx={{ fontSize: 28 }} />
          <Typography variant="h6" component="div" sx={{ fontWeight: 700, letterSpacing: '-0.02em' }}>
            Autoflex
          </Typography>
          <Chip
            label="Inventory Control"
            size="small"
            sx={{
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              color: 'white',
              fontWeight: 500,
              height: 24,
              fontSize: '0.75rem',
            }}
          />
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Button
                key={item.path}
                component={Link}
                to={item.path}
                startIcon={<Icon />}
                sx={{
                  color: 'white',
                  backgroundColor: isActive ? 'rgba(255, 255, 255, 0.2)' : 'transparent',
                  borderRadius: 2,
                  px: 2,
                  py: 1,
                  fontWeight: isActive ? 600 : 500,
                  transition: 'all 0.2s ease-in-out',
                  '&:hover': {
                    backgroundColor: 'rgba(255, 255, 255, 0.15)',
                    transform: 'translateY(-1px)',
                  },
                }}
              >
                {item.label}
              </Button>
            );
          })}
        </Box>
      </Toolbar>
    </AppBar>
  );
};
