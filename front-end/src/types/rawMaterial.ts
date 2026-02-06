export interface RawMaterial {
  code: string;
  name: string;
  stockQuantity: number;
}

export interface CreateRawMaterialRequest {
  name: string;
  stockQuantity: number;
}

export interface UpdateRawMaterialRequest {
  name?: string;
  stockQuantity?: number;
}
