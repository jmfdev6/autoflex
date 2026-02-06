import { useEffect } from 'react';
import { Snackbar, Alert, AlertColor } from '@mui/material';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { removeToast, ToastSeverity } from '@/store/slices/toastSlice';

const severityMap: Record<ToastSeverity, AlertColor> = {
  success: 'success',
  error: 'error',
  warning: 'warning',
  info: 'info',
};

export function Toast() {
  const dispatch = useAppDispatch();
  const toasts = useAppSelector(state => state.toast.toasts);
  const currentToast = toasts[0];

  useEffect(() => {
    if (currentToast) {
      const timer = setTimeout(() => {
        dispatch(removeToast(currentToast.id));
      }, currentToast.duration);

      return () => clearTimeout(timer);
    }
  }, [currentToast, dispatch]);

  const handleClose = () => {
    if (currentToast) {
      dispatch(removeToast(currentToast.id));
    }
  };

  if (!currentToast) {
    return null;
  }

  return (
    <Snackbar
      open={true}
      autoHideDuration={currentToast.duration}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      sx={{ bottom: { xs: 90, sm: 24 } }}
    >
      <Alert
        onClose={handleClose}
        severity={severityMap[currentToast.severity]}
        variant="filled"
        sx={{ width: '100%' }}
      >
        {currentToast.message}
      </Alert>
    </Snackbar>
  );
}
