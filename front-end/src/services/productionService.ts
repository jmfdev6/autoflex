import { ProductionSummary } from '@/types/production';
import { ApiResponse } from '@/types/api';
import { apiClient } from './api';

export const productionService = {
  async getProductionSuggestions(): Promise<ApiResponse<ProductionSummary>> {
    return apiClient.get<ProductionSummary>('/production/suggestions');
  },
};
