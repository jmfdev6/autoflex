import { ProductionSummary } from '@/types/production';
import { ApiResponse } from '@/types/api';
import { apiClient } from './api';

export interface ProductionItemRequest {
  productCode: string;
  quantity: number;
}

export interface CreateProductionRequest {
  items: ProductionItemRequest[];
}

export interface ProductionResponse {
  id: number;
  status: string;
  items: { productCode: string; quantity: number }[];
  createdAt: string;
}

export const productionService = {
  async getProductionSuggestions(): Promise<ApiResponse<ProductionSummary>> {
    return apiClient.get<ProductionSummary>('/production-suggestions');
  },

  async createProduction(request: CreateProductionRequest): Promise<ApiResponse<ProductionResponse>> {
    return apiClient.post<ProductionResponse>('/productions', request);
  },

  async confirmProduction(id: number): Promise<ApiResponse<{ items: unknown[]; successCount: number; failureCount: number }>> {
    return apiClient.post(`/productions/${id}/confirm`, {});
  },
};
