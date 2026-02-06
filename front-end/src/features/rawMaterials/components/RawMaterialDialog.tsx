import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useI18n } from '@/i18n';
import {
  RawMaterial,
  CreateRawMaterialRequest,
  UpdateRawMaterialRequest,
} from '@/types/rawMaterial';
import { RawMaterialForm } from './RawMaterialForm';

interface RawMaterialDialogProps {
  open: boolean;
  rawMaterial?: RawMaterial;
  onClose: () => void;
  onSubmit: (data: CreateRawMaterialRequest | UpdateRawMaterialRequest) => void;
  loading?: boolean;
}

export const RawMaterialDialog = ({
  open,
  rawMaterial,
  onClose,
  onSubmit,
  loading = false,
}: RawMaterialDialogProps) => {
  const { t } = useI18n();
  const handleSubmit = (data: CreateRawMaterialRequest | UpdateRawMaterialRequest) => {
    onSubmit(data);
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: { xs: 2, sm: 3 },
          m: { xs: 2, sm: 3 },
          maxHeight: { xs: '90vh', sm: 'auto' },
        },
      }}
    >
      <DialogTitle
        sx={{
          pb: { xs: 1.5, sm: 2 },
          px: { xs: 2, sm: 3 },
          pt: { xs: 2, sm: 3 },
          borderBottom: '1px solid',
          borderColor: 'divider',
          fontWeight: 600,
          fontSize: { xs: '1.1rem', sm: '1.25rem' },
        }}
      >
        {rawMaterial ? t.rawMaterials.editRawMaterial : t.rawMaterials.createRawMaterial}
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 16,
            top: 16,
            color: 'text.secondary',
            '&:hover': {
              backgroundColor: 'action.hover',
            },
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent
        sx={{
          pt: { xs: 2, sm: 3 },
          px: { xs: 2, sm: 3 },
          pb: { xs: 1, sm: 2 },
        }}
      >
        <RawMaterialForm
          rawMaterial={rawMaterial}
          onSubmit={handleSubmit}
          onCancel={onClose}
          loading={loading}
        />
      </DialogContent>
      <DialogActions
        sx={{
          p: { xs: 2, sm: 3 },
          pt: { xs: 1.5, sm: 2 },
          px: { xs: 2, sm: 3 },
          borderTop: '1px solid',
          borderColor: 'divider',
          flexDirection: { xs: 'column-reverse', sm: 'row' },
          gap: { xs: 1, sm: 0 },
        }}
      >
        <Button
          onClick={onClose}
          fullWidth={false}
          sx={{
            color: 'text.secondary',
            borderColor: '#e0e0e0',
            width: { xs: '100%', sm: 'auto' },
            '&:hover': {
              borderColor: '#bdbdbd',
              backgroundColor: '#f5f5f5',
            },
          }}
          variant="outlined"
        >
          {t.rawMaterials.cancel}
        </Button>
        <Button
          type="submit"
          form="raw-material-form"
          variant="contained"
          fullWidth={false}
          sx={{
            backgroundColor: '#1192d4',
            width: { xs: '100%', sm: 'auto' },
            '&:hover': {
              backgroundColor: '#0d7bb8',
            },
          }}
          disabled={loading}
        >
          {rawMaterial ? t.rawMaterials.update : t.rawMaterials.createButton}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
