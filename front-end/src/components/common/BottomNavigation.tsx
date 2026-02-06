import { BottomNavigation as MuiBottomNavigation, BottomNavigationAction, Paper } from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';
import { useI18n } from '@/i18n';
import CategoryIcon from '@mui/icons-material/Category';
import ScienceIcon from '@mui/icons-material/Science';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

export const BottomNavigation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { t } = useI18n();

  const navItems = [
    { label: t.nav.products, path: '/products', icon: CategoryIcon },
    { label: t.nav.rawMaterials, path: '/raw-materials', icon: ScienceIcon },
    { label: t.nav.production, path: '/production', icon: TrendingUpIcon },
  ];

  const currentValue = navItems.findIndex((item) => location.pathname === item.path);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    event.preventDefault();
    if (newValue !== -1) {
      navigate(navItems[newValue].path);
    }
  };

  return (
    <Paper
      component="nav"
      sx={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 1000,
        display: { xs: 'block', md: 'none' },
        borderTop: 'none',
        boxShadow: '0 -2px 8px rgba(0, 0, 0, 0.2)',
        touchAction: 'manipulation',
        WebkitTapHighlightColor: 'transparent',
        backgroundColor: '#0c0f3d',
      }}
      elevation={3}
    >
      <MuiBottomNavigation
        value={currentValue >= 0 ? currentValue : 0}
        onChange={handleChange}
        showLabels
        sx={{
          backgroundColor: '#0c0f3d',
          height: { xs: 70, sm: 80 },
          touchAction: 'manipulation',
          '& .MuiBottomNavigationAction-root': {
            color: 'rgba(255, 255, 255, 0.6)',
            minWidth: { xs: 60, sm: 80 },
            padding: { xs: '6px 8px', sm: '8px 12px' },
            touchAction: 'manipulation',
            WebkitTapHighlightColor: 'transparent',
            userSelect: 'none',
            WebkitUserSelect: 'none',
            '&:active': {
              transform: 'scale(0.95)',
              transition: 'transform 0.1s ease-in-out',
            },
            '&.Mui-selected': {
              color: '#1192d4',
            },
            '& .MuiSvgIcon-root': {
              fontSize: { xs: '1.5rem', sm: '1.75rem' },
            },
            '& .MuiBottomNavigationAction-label': {
              fontSize: { xs: '0.7rem', sm: '0.75rem' },
              fontWeight: 500,
              marginTop: { xs: '4px', sm: '6px' },
              userSelect: 'none',
              WebkitUserSelect: 'none',
              color: 'rgba(255, 255, 255, 0.6)',
              '&.Mui-selected': {
                fontSize: { xs: '0.7rem', sm: '0.75rem' },
                fontWeight: 600,
                color: '#1192d4',
              },
            },
          },
        }}
      >
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <BottomNavigationAction
              key={item.path}
              label={item.label}
              icon={<Icon />}
            />
          );
        })}
      </MuiBottomNavigation>
    </Paper>
  );
};
