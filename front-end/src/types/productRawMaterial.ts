export interface ProductRawMaterial {
  productCode: string;
  rawMaterialCode: string;
  quantity: number;
}

export interface CreateProductRawMaterialRequest {
  rawMaterialCode: string;
  quantity: number;
}

export interface UpdateProductRawMaterialRequest {
  quantity: number;
}

export interface ProductRawMaterialWithDetails extends ProductRawMaterial {
  rawMaterialName?: string;
  productName?: string;
}
