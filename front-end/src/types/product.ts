export interface Product {
  code: string;
  name: string;
  value: number;
}

export interface CreateProductRequest {
  name: string;
  value: number;
}

export interface UpdateProductRequest {
  name?: string;
  value?: number;
}
