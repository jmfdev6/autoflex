import { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ThemeProvider, createTheme, CssBaseline, CircularProgress, Box } from '@mui/material';
import { store } from './store/store';
import { I18nProvider } from './i18n';
import { Layout } from './components/common/Layout';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { Toast } from './components/common/Toast';

// Lazy load routes for code splitting
// Using dynamic imports with proper named export handling
const ProductList = lazy(() => 
  import('./features/products/components/ProductList').then(module => ({ 
    default: module.ProductList 
  }))
);
const RawMaterialList = lazy(() => 
  import('./features/rawMaterials/components/RawMaterialList').then(module => ({ 
    default: module.RawMaterialList 
  }))
);
const ProductionSuggestions = lazy(() => 
  import('./features/production/components/ProductionSuggestions').then(module => ({ 
    default: module.ProductionSuggestions 
  }))
);

// Loading component for Suspense fallback
const LoadingFallback = () => (
  <Box
    display="flex"
    justifyContent="center"
    alignItems="center"
    minHeight="400px"
  >
    <CircularProgress />
  </Box>
);

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#0c0f3d',
      light: '#1a1f5a',
      dark: '#080a2a',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#1192d4',
      light: '#3ba8e0',
      dark: '#0d7bb8',
      contrastText: '#ffffff',
    },
    background: {
      default: '#fafbfc',
      paper: '#ffffff',
    },
    text: {
      primary: '#1e293b',
      secondary: '#64748b',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 700,
      letterSpacing: '-0.02em',
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 24px',
          boxShadow: 'none',
          touchAction: 'manipulation',
          WebkitTapHighlightColor: 'transparent',
          '&:hover': {
            boxShadow: '0 4px 12px rgba(17, 146, 212, 0.3)',
          },
        },
        contained: {
          backgroundColor: '#1192d4',
          '&:hover': {
            backgroundColor: '#0d7bb8',
          },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
          WebkitTapHighlightColor: 'transparent',
          WebkitTouchCallout: 'none',
          userSelect: 'none',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
        },
      },
    },
    MuiMenuItem: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
          WebkitTapHighlightColor: 'transparent',
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.03)',
          transition: 'all 0.2s ease-in-out',
          touchAction: 'manipulation',
          border: '1px solid rgba(0, 0, 0, 0.04)',
          '&:hover': {
            boxShadow: '0 4px 12px 0 rgba(0, 0, 0, 0.08), 0 2px 4px 0 rgba(0, 0, 0, 0.04)',
            transform: 'translateY(-1px)',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.04), 0 1px 1px 0 rgba(0, 0, 0, 0.02)',
          touchAction: 'manipulation',
          border: '1px solid rgba(0, 0, 0, 0.04)',
        },
        elevation1: {
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.03)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          touchAction: 'manipulation',
          WebkitTapHighlightColor: 'transparent',
          userSelect: 'none',
        },
      },
    },
    MuiTableContainer: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          overflow: 'hidden',
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          backgroundColor: 'transparent',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: '1px solid rgba(0, 0, 0, 0.04)',
        },
        head: {
          fontWeight: 600,
          color: '#64748b',
          fontSize: '0.875rem',
          textTransform: 'uppercase',
          letterSpacing: '0.5px',
          paddingTop: 2,
          paddingBottom: 2,
          backgroundColor: 'transparent',
        },
        body: {
          borderBottom: '1px solid rgba(0, 0, 0, 0.04)',
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:last-child td': {
            borderBottom: 'none',
          },
          '&:hover': {
            backgroundColor: 'rgba(17, 146, 212, 0.02)',
          },
        },
      },
    },
  },
});

function App() {
  return (
    <Provider store={store}>
      <I18nProvider>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <ErrorBoundary>
            <BrowserRouter
              future={{
                v7_startTransition: true,
                v7_relativeSplatPath: true,
              }}
            >
              <Layout>
                <Suspense fallback={<LoadingFallback />}>
                  <Routes>
                    <Route path="/" element={<Navigate to="/products" replace />} />
                    <Route path="/products" element={<ProductList />} />
                    <Route path="/raw-materials" element={<RawMaterialList />} />
                    <Route path="/production" element={<ProductionSuggestions />} />
                  </Routes>
                </Suspense>
                <Toast />
              </Layout>
            </BrowserRouter>
          </ErrorBoundary>
        </ThemeProvider>
      </I18nProvider>
    </Provider>
  );
}

export default App;
