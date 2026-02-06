import { Box, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography, IconButton, Menu, MenuItem } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import CategoryIcon from '@mui/icons-material/Category';
import ScienceIcon from '@mui/icons-material/Science';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import InventoryIcon from '@mui/icons-material/Inventory';
import LanguageIcon from '@mui/icons-material/Language';
import { useI18n } from '@/i18n';
import { useState } from 'react';

export const Sidebar = () => {
  const location = useLocation();
  const { t, locale, setLocale } = useI18n();
  const [languageMenuAnchor, setLanguageMenuAnchor] = useState<null | HTMLElement>(null);

  const navItems = [
    { label: t.nav.products, path: '/products', icon: CategoryIcon },
    { label: t.nav.rawMaterials, path: '/raw-materials', icon: ScienceIcon },
    { label: t.nav.production, path: '/production', icon: TrendingUpIcon },
  ];

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setLanguageMenuAnchor(event.currentTarget);
  };

  const handleLanguageMenuClose = () => {
    setLanguageMenuAnchor(null);
  };

  const handleLanguageChange = (newLocale: 'en' | 'pt-BR') => {
    setLocale(newLocale);
    handleLanguageMenuClose();
  };

  return (
    <Box
      sx={{
        width: 260,
        height: '100vh',
        backgroundColor: '#0c0f3d',
        display: { xs: 'none', md: 'flex' },
        flexDirection: 'column',
        position: 'fixed',
        left: 0,
        top: 0,
        zIndex: 1000,
        boxShadow: '2px 0 8px rgba(0, 0, 0, 0.1)',
      }}
    >
      {/* Logo/Branding */}
      <Box
        sx={{
          p: 3,
          display: 'flex',
          alignItems: 'center',
          gap: 2,
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
        }}
      >
        <Box
          sx={{
            width: 44,
            height: 44,
            borderRadius: 2.5,
            backgroundColor: '#1192d4',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            flexShrink: 0,
          }}
        >
          <InventoryIcon sx={{ fontSize: 26 }} />
        </Box>
        <Box sx={{ minWidth: 0 }}>
          <Typography
            variant="h6"
            sx={{
              color: 'white',
              fontWeight: 700,
              fontSize: '1.2rem',
              lineHeight: 1.2,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              letterSpacing: '-0.01em',
            }}
          >
            Autoflex
          </Typography>
          <Typography
            variant="caption"
            sx={{
              color: 'rgba(255, 255, 255, 0.65)',
              fontSize: '0.75rem',
              fontWeight: 500,
            }}
          >
            Inventory Control
          </Typography>
        </Box>
      </Box>

      {/* Navigation Items */}
      <Box sx={{ flex: 1, overflow: 'auto', py: 3 }}>
        {/* Language Selector */}
        <Box sx={{ px: 2, mb: 2 }}>
          <IconButton
            onClick={handleLanguageMenuOpen}
            sx={{
              width: '100%',
              justifyContent: 'flex-start',
              color: 'rgba(255, 255, 255, 0.75)',
              borderRadius: 2,
              py: 1.5,
              px: 2,
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 0.08)',
                color: 'white',
              },
            }}
          >
            <LanguageIcon sx={{ mr: 1.5, fontSize: 20 }} />
            <Typography sx={{ fontSize: '0.875rem', fontWeight: 500, flex: 1, textAlign: 'left' }}>
              {locale === 'en' ? 'English' : 'Português'}
            </Typography>
          </IconButton>
          <Menu
            anchorEl={languageMenuAnchor}
            open={Boolean(languageMenuAnchor)}
            onClose={handleLanguageMenuClose}
            PaperProps={{
              sx: {
                mt: 1,
                minWidth: 180,
                borderRadius: 2,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
              },
            }}
            transformOrigin={{ horizontal: 'left', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'left', vertical: 'bottom' }}
          >
            <MenuItem
              onClick={() => handleLanguageChange('pt-BR')}
              selected={locale === 'pt-BR'}
              sx={{
                py: 1.5,
                px: 2,
                '&.Mui-selected': {
                  backgroundColor: 'rgba(17, 146, 212, 0.1)',
                  '&:hover': {
                    backgroundColor: 'rgba(17, 146, 212, 0.15)',
                  },
                },
              }}
            >
              Português (BR)
            </MenuItem>
            <MenuItem
              onClick={() => handleLanguageChange('en')}
              selected={locale === 'en'}
              sx={{
                py: 1.5,
                px: 2,
                '&.Mui-selected': {
                  backgroundColor: 'rgba(17, 146, 212, 0.1)',
                  '&:hover': {
                    backgroundColor: 'rgba(17, 146, 212, 0.15)',
                  },
                },
              }}
            >
              English
            </MenuItem>
          </Menu>
        </Box>
        <List sx={{ px: 2 }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.path} disablePadding sx={{ mb: 1 }}>
                <ListItemButton
                  component={Link}
                  to={item.path}
                  sx={{
                    borderRadius: 2.5,
                    py: 1.75,
                    px: 2.5,
                    position: 'relative',
                    backgroundColor: 'transparent',
                    color: isActive ? '#1192d4' : 'rgba(255, 255, 255, 0.75)',
                    '&::before': isActive
                      ? {
                          content: '""',
                          position: 'absolute',
                          left: 0,
                          top: '50%',
                          transform: 'translateY(-50%)',
                          width: 3,
                          height: '60%',
                          backgroundColor: '#1192d4',
                          borderRadius: '0 2px 2px 0',
                        }
                      : {},
                    '&:hover': {
                      backgroundColor: 'rgba(255, 255, 255, 0.08)',
                      color: isActive ? '#1192d4' : 'white',
                    },
                    transition: 'all 0.2s ease-in-out',
                  }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: 44,
                      color: isActive ? '#1192d4' : 'rgba(255, 255, 255, 0.65)',
                    }}
                  >
                    <Icon sx={{ fontSize: 26 }} />
                  </ListItemIcon>
                  <ListItemText
                    primary={item.label}
                    primaryTypographyProps={{
                      fontWeight: isActive ? 600 : 500,
                      fontSize: '0.95rem',
                      letterSpacing: '-0.01em',
                    }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Box>
    </Box>
  );
};
