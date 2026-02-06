import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import {
  RawMaterial,
  CreateRawMaterialRequest,
  UpdateRawMaterialRequest,
} from '@/types/rawMaterial';
import { PageRequest, PageResponse } from '@/types/api';
import { rawMaterialService } from '@/services/rawMaterialService';
import { RawMaterialsState } from '../types';

const initialState: RawMaterialsState = {
  items: [],
  loading: false,
  error: null,
  pagination: null,
};

export const fetchRawMaterials = createAsyncThunk(
  'rawMaterials/fetchAll',
  async () => {
    const response = await rawMaterialService.getAll();
    return response.data;
  }
);

export const fetchRawMaterialsPaginated = createAsyncThunk(
  'rawMaterials/fetchPaginated',
  async (pageRequest?: PageRequest) => {
    const response = await rawMaterialService.getAllPaginated(pageRequest);
    return response.data;
  }
);

export const fetchRawMaterialByCode = createAsyncThunk(
  'rawMaterials/fetchByCode',
  async (code: string) => {
    const response = await rawMaterialService.getByCode(code);
    return response.data;
  }
);

export const createRawMaterial = createAsyncThunk(
  'rawMaterials/create',
  async (request: CreateRawMaterialRequest) => {
    const response = await rawMaterialService.create(request);
    return response.data;
  }
);

export const updateRawMaterial = createAsyncThunk(
  'rawMaterials/update',
  async ({ code, request }: { code: string; request: UpdateRawMaterialRequest }) => {
    const response = await rawMaterialService.update(code, request);
    return response.data;
  }
);

export const deleteRawMaterial = createAsyncThunk(
  'rawMaterials/delete',
  async (code: string) => {
    await rawMaterialService.delete(code);
    return code;
  }
);

const rawMaterialsSlice = createSlice({
  name: 'rawMaterials',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all
      .addCase(fetchRawMaterials.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRawMaterials.fulfilled, (state, action: PayloadAction<RawMaterial[]>) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchRawMaterials.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch raw materials';
      })
      // Fetch paginated
      .addCase(fetchRawMaterialsPaginated.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRawMaterialsPaginated.fulfilled, (state, action: PayloadAction<PageResponse<RawMaterial>>) => {
        state.loading = false;
        state.items = action.payload.content;
        state.pagination = {
          page: action.payload.page,
          size: action.payload.size,
          totalElements: action.payload.totalElements,
          totalPages: action.payload.totalPages,
          first: action.payload.first,
          last: action.payload.last,
        };
      })
      .addCase(fetchRawMaterialsPaginated.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch raw materials';
      })
      // Create
      .addCase(createRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createRawMaterial.fulfilled, (state, action: PayloadAction<RawMaterial>) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(createRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create raw material';
      })
      // Update
      .addCase(updateRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateRawMaterial.fulfilled, (state, action: PayloadAction<RawMaterial>) => {
        state.loading = false;
        const index = state.items.findIndex(rm => rm.code === action.payload.code);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(updateRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update raw material';
      })
      // Delete
      .addCase(deleteRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteRawMaterial.fulfilled, (state, action: PayloadAction<string>) => {
        state.loading = false;
        state.items = state.items.filter(rm => rm.code !== action.payload);
      })
      .addCase(deleteRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete raw material';
      });
  },
});

export const { clearError } = rawMaterialsSlice.actions;
export default rawMaterialsSlice.reducer;
