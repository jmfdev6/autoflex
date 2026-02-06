import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';
import { productService } from '@/services/productService';
import { ProductsState } from '../types';

const initialState: ProductsState = {
  items: [],
  loading: false,
  error: null,
};

export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async () => {
    const response = await productService.getAll();
    return response.data;
  }
);

export const fetchProductByCode = createAsyncThunk(
  'products/fetchByCode',
  async (code: string) => {
    const response = await productService.getByCode(code);
    return response.data;
  }
);

export const createProduct = createAsyncThunk(
  'products/create',
  async (request: CreateProductRequest) => {
    const response = await productService.create(request);
    return response.data;
  }
);

export const updateProduct = createAsyncThunk(
  'products/update',
  async ({ code, request }: { code: string; request: UpdateProductRequest }) => {
    const response = await productService.update(code, request);
    return response.data;
  }
);

export const deleteProduct = createAsyncThunk(
  'products/delete',
  async (code: string) => {
    await productService.delete(code);
    return code;
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
      .addCase(fetchProducts.fulfilled, (state, action: PayloadAction<Product[]>) => {
        state.loading = false;
        state.items = action.payload;
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
