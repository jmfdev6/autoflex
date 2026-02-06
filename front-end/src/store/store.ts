import { configureStore } from '@reduxjs/toolkit';
import productsReducer from './slices/productsSlice';
import rawMaterialsReducer from './slices/rawMaterialsSlice';
import productRawMaterialsReducer from './slices/productRawMaterialsSlice';
import productionReducer from './slices/productionSlice';
import toastReducer from './slices/toastSlice';

export const store = configureStore({
  reducer: {
    products: productsReducer,
    rawMaterials: rawMaterialsReducer,
    productRawMaterials: productRawMaterialsReducer,
    production: productionReducer,
    toast: toastReducer,
  },
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
