import rawMaterialsReducer, {
  fetchRawMaterials,
  createRawMaterial,
  updateRawMaterial,
  deleteRawMaterial,
} from '../rawMaterialsSlice';
import { RawMaterialsState } from '../../types';
import { RawMaterial } from '@/types/rawMaterial';

describe('rawMaterialsSlice', () => {
  const initialState: RawMaterialsState = {
    items: [],
    loading: false,
    error: null,
    pagination: null,
  };

  it('should return the initial state', () => {
    expect(rawMaterialsReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle fetchRawMaterials.pending', () => {
    const action = { type: fetchRawMaterials.pending.type };
    const state = rawMaterialsReducer(initialState, action);
    expect(state.loading).toBe(true);
    expect(state.error).toBe(null);
  });

  it('should handle fetchRawMaterials.fulfilled', () => {
    const rawMaterials: RawMaterial[] = [
      { code: 'RM001', name: 'Raw Material A', stockQuantity: 100 },
    ];
    const action = { type: fetchRawMaterials.fulfilled.type, payload: rawMaterials };
    const state = rawMaterialsReducer(initialState, action);
    expect(state.loading).toBe(false);
    expect(state.items).toEqual(rawMaterials);
  });

  it('should handle createRawMaterial.fulfilled', () => {
    const newRawMaterial: RawMaterial = {
      code: 'RM001',
      name: 'Raw Material A',
      stockQuantity: 100,
    };
    const action = { type: createRawMaterial.fulfilled.type, payload: newRawMaterial };
    const state = rawMaterialsReducer(initialState, action);
    expect(state.loading).toBe(false);
    expect(state.items).toContainEqual(newRawMaterial);
  });

  it('should handle updateRawMaterial.fulfilled', () => {
    const existingState: RawMaterialsState = {
      items: [{ code: 'RM001', name: 'Raw Material A', stockQuantity: 100 }],
      loading: false,
      error: null,
      pagination: null,
    };
    const updatedRawMaterial: RawMaterial = {
      code: 'RM001',
      name: 'Raw Material A Updated',
      stockQuantity: 150,
    };
    const action = { type: updateRawMaterial.fulfilled.type, payload: updatedRawMaterial };
    const state = rawMaterialsReducer(existingState, action);
    expect(state.items[0]).toEqual(updatedRawMaterial);
  });

  it('should handle deleteRawMaterial.fulfilled', () => {
    const existingState: RawMaterialsState = {
      items: [{ code: 'RM001', name: 'Raw Material A', stockQuantity: 100 }],
      loading: false,
      error: null,
      pagination: null,
    };
    const action = { type: deleteRawMaterial.fulfilled.type, payload: 'RM001' };
    const state = rawMaterialsReducer(existingState, action);
    expect(state.items).toHaveLength(0);
  });
});
