import { Product } from '@/types/product';
import { RawMaterial } from '@/types/rawMaterial';
import { ProductRawMaterial } from '@/types/productRawMaterial';
import { ProductionSummary } from '@/types/production';

export interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ProductsState {
  items: Product[];
  loading: boolean;
  error: string | null;
  pagination: PaginationInfo | null;
}

export interface RawMaterialsState {
  items: RawMaterial[];
  loading: boolean;
  error: string | null;
  pagination: PaginationInfo | null;
}

export interface ProductRawMaterialsState {
  items: ProductRawMaterial[];
  loading: boolean;
  error: string | null;
}

export interface ProductionState {
  summary: ProductionSummary | null;
  loading: boolean;
  error: string | null;
}

export interface RootState {
  products: ProductsState;
  rawMaterials: RawMaterialsState;
  productRawMaterials: ProductRawMaterialsState;
  production: ProductionState;
}
