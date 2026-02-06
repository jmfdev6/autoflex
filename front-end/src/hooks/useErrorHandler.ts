import { useCallback } from 'react';
import { useAppDispatch } from '@/store/hooks';
import { addToast } from '@/store/slices/toastSlice';
import { parseErrorResponse } from '@/utils/errorHandler';
import { ApiError } from '@/types/api';

export function useErrorHandler() {
  const dispatch = useAppDispatch();

  const handleError = useCallback(
    async (error: unknown, defaultMessage?: string): Promise<ApiError> => {
      let apiError: ApiError;

      if (error instanceof Response) {
        apiError = await parseErrorResponse(error);
      } else if (error instanceof Error) {
        apiError = {
          message: error.message || defaultMessage || 'An unexpected error occurred',
          code: 'UNKNOWN',
        };
      } else {
        apiError = {
          message: defaultMessage || 'An unexpected error occurred',
          code: 'UNKNOWN',
        };
      }

      // Show toast notification
      dispatch(
        addToast({
          message: apiError.message,
          severity: 'error',
          duration: 6000,
        })
      );

      return apiError;
    },
    [dispatch]
  );

  const showSuccess = useCallback(
    (message: string) => {
      dispatch(
        addToast({
          message,
          severity: 'success',
          duration: 4000,
        })
      );
    },
    [dispatch]
  );

  const showWarning = useCallback(
    (message: string) => {
      dispatch(
        addToast({
          message,
          severity: 'warning',
          duration: 5000,
        })
      );
    },
    [dispatch]
  );

  const showInfo = useCallback(
    (message: string) => {
      dispatch(
        addToast({
          message,
          severity: 'info',
          duration: 4000,
        })
      );
    },
    [dispatch]
  );

  return {
    handleError,
    showSuccess,
    showWarning,
    showInfo,
  };
}
