import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';
import { ApiResponse } from '@/types/api';
import { apiClient } from './api';

export const productService = {
  async getAll(): Promise<ApiResponse<Product[]>> {
    return apiClient.get<Product[]>('/products');
  },

  async getByCode(code: string): Promise<ApiResponse<Product>> {
    return apiClient.get<Product>(`/products/${encodeURIComponent(code)}`);
  },

  async create(request: CreateProductRequest): Promise<ApiResponse<Product>> {
    return apiClient.post<Product>('/products', request);
  },

  async update(code: string, request: UpdateProductRequest): Promise<ApiResponse<Product>> {
    return apiClient.put<Product>(`/products/${encodeURIComponent(code)}`, request);
  },

  async delete(code: string): Promise<ApiResponse<void>> {
    return apiClient.delete<void>(`/products/${encodeURIComponent(code)}`);
  },
};
