import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import {
  ProductRawMaterial,
  CreateProductRawMaterialRequest,
  UpdateProductRawMaterialRequest,
} from '@/types/productRawMaterial';
import { productRawMaterialService } from '@/services/productRawMaterialService';
import { ProductRawMaterialsState } from '../types';

const initialState: ProductRawMaterialsState = {
  items: [],
  loading: false,
  error: null,
};

export const fetchProductRawMaterials = createAsyncThunk(
  'productRawMaterials/fetchByProduct',
  async (productCode: string) => {
    const response = await productRawMaterialService.getByProductCode(productCode);
    return { productCode, items: response.data };
  }
);

export const createProductRawMaterial = createAsyncThunk(
  'productRawMaterials/create',
  async ({ productCode, request }: { productCode: string; request: CreateProductRawMaterialRequest }) => {
    const response = await productRawMaterialService.create(productCode, request);
    return response.data;
  }
);

export const updateProductRawMaterial = createAsyncThunk(
  'productRawMaterials/update',
  async ({
    productCode,
    rawMaterialCode,
    request,
  }: {
    productCode: string;
    rawMaterialCode: string;
    request: UpdateProductRawMaterialRequest;
  }) => {
    const response = await productRawMaterialService.update(productCode, rawMaterialCode, request);
    return response.data;
  }
);

export const deleteProductRawMaterial = createAsyncThunk(
  'productRawMaterials/delete',
  async ({ productCode, rawMaterialCode }: { productCode: string; rawMaterialCode: string }) => {
    await productRawMaterialService.delete(productCode, rawMaterialCode);
    return { productCode, rawMaterialCode };
  }
);

const productRawMaterialsSlice = createSlice({
  name: 'productRawMaterials',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearItems: (state) => {
      state.items = [];
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch by product
      .addCase(fetchProductRawMaterials.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchProductRawMaterials.fulfilled,
        (state, action: PayloadAction<{ productCode: string; items: ProductRawMaterial[] }>) => {
          state.loading = false;
          state.items = action.payload.items;
        }
      )
      .addCase(fetchProductRawMaterials.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch associations';
      })
      // Create
      .addCase(createProductRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createProductRawMaterial.fulfilled, (state, action: PayloadAction<ProductRawMaterial>) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(createProductRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create association';
      })
      // Update
      .addCase(updateProductRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateProductRawMaterial.fulfilled, (state, action: PayloadAction<ProductRawMaterial>) => {
        state.loading = false;
        const index = state.items.findIndex(
          item =>
            item.productCode === action.payload.productCode &&
            item.rawMaterialCode === action.payload.rawMaterialCode
        );
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(updateProductRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update association';
      })
      // Delete
      .addCase(deleteProductRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        deleteProductRawMaterial.fulfilled,
        (state, action: PayloadAction<{ productCode: string; rawMaterialCode: string }>) => {
          state.loading = false;
          state.items = state.items.filter(
            item =>
              !(
                item.productCode === action.payload.productCode &&
                item.rawMaterialCode === action.payload.rawMaterialCode
              )
          );
        }
      )
      .addCase(deleteProductRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete association';
      });
  },
});

export const { clearError, clearItems } = productRawMaterialsSlice.actions;
export default productRawMaterialsSlice.reducer;
