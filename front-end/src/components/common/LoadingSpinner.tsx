import { Box, CircularProgress } from '@mui/material';

interface LoadingSpinnerProps {
  size?: number;
  fullScreen?: boolean;
}

export const LoadingSpinner = ({ size = 40, fullScreen = false }: LoadingSpinnerProps) => {
  const content = (
    <CircularProgress
      size={size}
      sx={{
        width: { xs: size * 0.8, sm: size },
        height: { xs: size * 0.8, sm: size },
      }}
    />
  );

  if (fullScreen) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
        p={3}
      >
        {content}
      </Box>
    );
  }

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      p={{ xs: 2, sm: 3 }}
    >
      {content}
    </Box>
  );
};
