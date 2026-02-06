import { Product } from './product';

export interface ProductionSuggestion {
  product: Product;
  producibleQuantity: number;
  totalValue: number;
}

export interface ProductionSummary {
  suggestions: ProductionSuggestion[];
  totalValue: number;
}
