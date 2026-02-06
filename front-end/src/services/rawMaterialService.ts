import { RawMaterial, CreateRawMaterialRequest, UpdateRawMaterialRequest } from '@/types/rawMaterial';
import { ApiResponse } from '@/types/api';
import { apiClient } from './api';

export const rawMaterialService = {
  async getAll(): Promise<ApiResponse<RawMaterial[]>> {
    return apiClient.get<RawMaterial[]>('/raw-materials');
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
