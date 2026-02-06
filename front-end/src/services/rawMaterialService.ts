import { RawMaterial, CreateRawMaterialRequest, UpdateRawMaterialRequest } from '@/types/rawMaterial';
import { ApiResponse, PageRequest, PageResponse } from '@/types/api';
import { apiClient } from './api';

export const rawMaterialService = {
  async getAll(): Promise<ApiResponse<RawMaterial[]>> {
    return apiClient.get<RawMaterial[]>('/raw-materials');
  },

  async getAllPaginated(pageRequest?: PageRequest): Promise<ApiResponse<PageResponse<RawMaterial>>> {
    const params = new URLSearchParams();
    if (pageRequest?.page !== undefined) params.append('page', pageRequest.page.toString());
    if (pageRequest?.size !== undefined) params.append('size', pageRequest.size.toString());
    if (pageRequest?.sort) params.append('sort', pageRequest.sort);
    
    const queryString = params.toString();
    const endpoint = queryString ? `/raw-materials?${queryString}` : '/raw-materials';
    return apiClient.get<PageResponse<RawMaterial>>(endpoint);
  },

  async getByCode(code: string): Promise<ApiResponse<RawMaterial>> {
    return apiClient.get<RawMaterial>(`/raw-materials/${encodeURIComponent(code)}`);
  },

  async create(request: CreateRawMaterialRequest): Promise<ApiResponse<RawMaterial>> {
    return apiClient.post<RawMaterial>('/raw-materials', request);
  },

  async update(code: string, request: UpdateRawMaterialRequest): Promise<ApiResponse<RawMaterial>> {
    return apiClient.put<RawMaterial>(`/raw-materials/${encodeURIComponent(code)}`, request);
  },

  async delete(code: string): Promise<ApiResponse<void>> {
    return apiClient.delete<void>(`/raw-materials/${encodeURIComponent(code)}`);
  },
};
