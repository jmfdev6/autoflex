import { Box, Fab, Menu, MenuItem } from '@mui/material';
import { ReactNode, useState } from 'react';
import LanguageIcon from '@mui/icons-material/Language';
import { Sidebar } from './Sidebar';
import { BottomNavigation } from './BottomNavigation';
import { useI18n } from '@/i18n';

interface LayoutProps {
  children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => {
  const { locale, setLocale } = useI18n();
  const [languageMenuAnchor, setLanguageMenuAnchor] = useState<null | HTMLElement>(null);

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
        display: 'flex',
        minHeight: '100vh',
        backgroundColor: '#fafbfc',
        touchAction: 'pan-y',
      }}
    >
      <Sidebar />
      <Box
        sx={{
          flex: 1,
          marginLeft: { xs: 0, md: '260px' },
          display: 'flex',
          flexDirection: 'column',
          paddingBottom: { xs: '70px', sm: '80px', md: 0 },
          touchAction: 'pan-y',
          position: 'relative',
        }}
      >
        {/* Language Selector for Mobile */}
        <Fab
          color="primary"
          aria-label="language"
          onClick={handleLanguageMenuOpen}
          sx={{
            position: 'fixed',
            top: { xs: 16, sm: 24 },
            right: { xs: 16, sm: 24 },
            zIndex: 1000,
            display: { xs: 'flex', md: 'none' },
            backgroundColor: '#0c0f3d',
            '&:hover': {
              backgroundColor: '#151840',
            },
            touchAction: 'manipulation',
            WebkitTapHighlightColor: 'transparent',
            minWidth: 48,
            minHeight: 48,
            width: 48,
            height: 48,
            color: 'white',
          }}
        >
          <LanguageIcon />
        </Fab>
        <Menu
          anchorEl={languageMenuAnchor}
          open={Boolean(languageMenuAnchor)}
          onClose={handleLanguageMenuClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'right',
          }}
          transformOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
          PaperProps={{
            sx: {
              mt: 1,
              minWidth: 150,
              borderRadius: 2,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
            },
          }}
        >
          <MenuItem
            onClick={() => handleLanguageChange('pt-BR')}
            selected={locale === 'pt-BR'}
            sx={{
              fontSize: '0.875rem',
              py: 1.5,
              '&.Mui-selected': {
                backgroundColor: 'rgba(17, 146, 212, 0.08)',
                '&:hover': {
                  backgroundColor: 'rgba(17, 146, 212, 0.12)',
                },
              },
            }}
          >
            Português
          </MenuItem>
          <MenuItem
            onClick={() => handleLanguageChange('en')}
            selected={locale === 'en'}
            sx={{
              fontSize: '0.875rem',
              py: 1.5,
              '&.Mui-selected': {
                backgroundColor: 'rgba(17, 146, 212, 0.08)',
                '&:hover': {
                  backgroundColor: 'rgba(17, 146, 212, 0.12)',
                },
              },
            }}
          >
            English
          </MenuItem>
        </Menu>
        <Box
          sx={{
            flex: 1,
            p: { xs: 2, sm: 4 },
            width: '100%',
            maxWidth: { xs: '100%', lg: '1400px' },
            mx: 'auto',
            overflowX: 'hidden',
            touchAction: 'pan-y',
          }}
        >
          {children}
        </Box>
      </Box>
      <BottomNavigation />
    </Box>
  );
};
