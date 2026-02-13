import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';
import { PageRequest, PageResponse } from '@/types/api';
import { productService } from '@/services/productService';
import { ResponseError } from '@/services/api';
import { ProductsState } from '../types';

const initialState: ProductsState = {
  items: [],
  loading: false,
  error: null,
  pagination: null,
};

/** Fetches products with pagination. Backend always returns PageResponse. */
export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async (pageRequest?: PageRequest, { rejectWithValue } = {} as any) => {
    try {
      const response = await productService.getAllPaginated(pageRequest ?? { page: 0, size: 20, sort: 'code' });
      return response.data;
    } catch (error) {
      if (error instanceof ResponseError) {
        return rejectWithValue(error.apiError);
      }
      throw error;
    }
  }
);

export const fetchProductByCode = createAsyncThunk(
  'products/fetchByCode',
  async (code: string, { rejectWithValue }) => {
    try {
      const response = await productService.getByCode(code);
      return response.data;
    } catch (error) {
      if (error instanceof ResponseError) {
        return rejectWithValue(error.apiError);
      }
      throw error;
    }
  }
);

export const createProduct = createAsyncThunk(
  'products/create',
  async (request: CreateProductRequest, { rejectWithValue }) => {
    try {
      const response = await productService.create(request);
      return response.data;
    } catch (error) {
      if (error instanceof ResponseError) {
        return rejectWithValue(error.apiError);
      }
      throw error;
    }
  }
);

export const updateProduct = createAsyncThunk(
  'products/update',
  async ({ code, request }: { code: string; request: UpdateProductRequest }, { rejectWithValue }) => {
    try {
      const response = await productService.update(code, request);
      return response.data;
    } catch (error) {
      if (error instanceof ResponseError) {
        return rejectWithValue(error.apiError);
      }
      throw error;
    }
  }
);

export const deleteProduct = createAsyncThunk(
  'products/delete',
  async (code: string, { rejectWithValue }) => {
    try {
      await productService.delete(code);
      return code;
    } catch (error) {
      if (error instanceof ResponseError) {
        return rejectWithValue(error.apiError);
      }
      throw error;
    }
  }
);

const productsSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action: PayloadAction<PageResponse<Product>>) => {
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
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch products';
      })
      // Create
      .addCase(createProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createProduct.fulfilled, (state, action: PayloadAction<Product>) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(createProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create product';
      })
      // Update
      .addCase(updateProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateProduct.fulfilled, (state, action: PayloadAction<Product>) => {
        state.loading = false;
        const index = state.items.findIndex(p => p.code === action.payload.code);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(updateProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update product';
      })
      // Delete
      .addCase(deleteProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteProduct.fulfilled, (state, action: PayloadAction<string>) => {
        state.loading = false;
        state.items = state.items.filter(p => p.code !== action.payload);
      })
      .addCase(deleteProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete product';
      });
  },
});

export const { clearError } = productsSlice.actions;
export default productsSlice.reducer;
