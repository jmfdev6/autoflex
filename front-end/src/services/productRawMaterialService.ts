import {
  ProductRawMaterial,
  CreateProductRawMaterialRequest,
  UpdateProductRawMaterialRequest,
} from '@/types/productRawMaterial';
import { ApiResponse } from '@/types/api';
import { apiClient } from './api';

export const productRawMaterialService = {
  async getByProductCode(productCode: string): Promise<ApiResponse<ProductRawMaterial[]>> {
    return apiClient.get<ProductRawMaterial[]>(
      `/products/${encodeURIComponent(productCode)}/raw-materials`
    );
  },

  async create(
    productCode: string,
    request: CreateProductRawMaterialRequest
  ): Promise<ApiResponse<ProductRawMaterial>> {
    return apiClient.post<ProductRawMaterial>(
      `/products/${encodeURIComponent(productCode)}/raw-materials`,
      request
    );
  },

  async update(
    productCode: string,
    rawMaterialCode: string,
    request: UpdateProductRawMaterialRequest
  ): Promise<ApiResponse<ProductRawMaterial>> {
    return apiClient.put<ProductRawMaterial>(
      `/products/${encodeURIComponent(productCode)}/raw-materials/${encodeURIComponent(rawMaterialCode)}`,
      request
    );
  },

  async delete(productCode: string, rawMaterialCode: string): Promise<ApiResponse<void>> {
    return apiClient.delete<void>(
      `/products/${encodeURIComponent(productCode)}/raw-materials/${encodeURIComponent(rawMaterialCode)}`
    );
  },
};
