import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { ProductionSummary } from '@/types/production';
import { productionService } from '@/services/productionService';
import { ProductionState } from '../types';

const initialState: ProductionState = {
  summary: null,
  loading: false,
  error: null,
};

export const fetchProductionSuggestions = createAsyncThunk(
  'production/fetchSuggestions',
  async () => {
    const response = await productionService.getProductionSuggestions();
    return response.data;
  }
);

const productionSlice = createSlice({
  name: 'production',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearSummary: (state) => {
      state.summary = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProductionSuggestions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductionSuggestions.fulfilled, (state, action: PayloadAction<ProductionSummary>) => {
        state.loading = false;
        state.summary = action.payload;
      })
      .addCase(fetchProductionSuggestions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch production suggestions';
      });
  },
});

export const { clearError, clearSummary } = productionSlice.actions;
export default productionSlice.reducer;
