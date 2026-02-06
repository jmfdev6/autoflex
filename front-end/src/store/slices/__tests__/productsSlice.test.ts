import productsReducer, { fetchProducts, createProduct, updateProduct, deleteProduct } from '../productsSlice';
import { ProductsState } from '../../types';
import { Product } from '@/types/product';

describe('productsSlice', () => {
  const initialState: ProductsState = {
    items: [],
    loading: false,
    error: null,
  };

  it('should return the initial state', () => {
    expect(productsReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle fetchProducts.pending', () => {
    const action = { type: fetchProducts.pending.type };
    const state = productsReducer(initialState, action);
    expect(state.loading).toBe(true);
    expect(state.error).toBe(null);
  });

  it('should handle fetchProducts.fulfilled', () => {
    const products: Product[] = [
      { code: 'P001', name: 'Product A', value: 100 },
    ];
    const action = { type: fetchProducts.fulfilled.type, payload: products };
    const state = productsReducer(initialState, action);
    expect(state.loading).toBe(false);
    expect(state.items).toEqual(products);
  });

  it('should handle createProduct.fulfilled', () => {
    const newProduct: Product = { code: 'P001', name: 'Product A', value: 100 };
    const action = { type: createProduct.fulfilled.type, payload: newProduct };
    const state = productsReducer(initialState, action);
    expect(state.loading).toBe(false);
    expect(state.items).toContainEqual(newProduct);
  });

  it('should handle updateProduct.fulfilled', () => {
    const existingState: ProductsState = {
      items: [{ code: 'P001', name: 'Product A', value: 100 }],
      loading: false,
      error: null,
    };
    const updatedProduct: Product = { code: 'P001', name: 'Product A Updated', value: 150 };
    const action = { type: updateProduct.fulfilled.type, payload: updatedProduct };
    const state = productsReducer(existingState, action);
    expect(state.items[0]).toEqual(updatedProduct);
  });

  it('should handle deleteProduct.fulfilled', () => {
    const existingState: ProductsState = {
      items: [{ code: 'P001', name: 'Product A', value: 100 }],
      loading: false,
      error: null,
    };
    const action = { type: deleteProduct.fulfilled.type, payload: 'P001' };
    const state = productsReducer(existingState, action);
    expect(state.items).toHaveLength(0);
  });
});
