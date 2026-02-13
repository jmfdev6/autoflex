import { Product, CreateProductRequest, UpdateProductRequest } from '@/types/product';
import { ApiResponse, PageRequest, PageResponse } from '@/types/api';
import { apiClient } from './api';

export const productService = {
  /**
   * GET /products always returns paginated response. Use page, size, sort (e.g. ?page=0&size=20&sort=code).
   */
  async getAllPaginated(pageRequest?: PageRequest): Promise<ApiResponse<PageResponse<Product>>> {
    const params = new URLSearchParams();
    if (pageRequest?.page !== undefined) params.append('page', pageRequest.page.toString());
    if (pageRequest?.size !== undefined) params.append('size', pageRequest.size.toString());
    if (pageRequest?.sort) params.append('sort', pageRequest.sort);
    const queryString = params.toString();
    const endpoint = queryString ? `/products?${queryString}` : '/products';
    return apiClient.get<PageResponse<Product>>(endpoint);
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
