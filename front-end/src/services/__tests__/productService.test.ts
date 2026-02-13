import { productService } from '../productService';

describe('productService', () => {
  beforeEach(() => {
    // Reset mock data
    jest.clearAllMocks();
  });

  it('should get products paginated', async () => {
    const response = await productService.getAllPaginated({ page: 0, size: 20, sort: 'code' });
    expect(response.success).toBe(true);
    expect(response.data).toHaveProperty('content');
    expect(Array.isArray(response.data.content)).toBe(true);
    expect(response.data).toHaveProperty('page', 0);
    expect(response.data).toHaveProperty('size', 20);
    expect(response.data).toHaveProperty('totalElements');
    expect(response.data).toHaveProperty('totalPages');
  });

  it('should create a product', async () => {
    const newProduct = {
      name: 'Test Product',
      value: 50.0,
    };
    const response = await productService.create(newProduct);
    expect(response.success).toBe(true);
    expect(response.data.name).toBe(newProduct.name);
    expect(response.data.value).toBe(newProduct.value);
    expect(response.data.code).toBeDefined();
  });

  it('should get product by code', async () => {
    // First create a product
    const createResponse = await productService.create({
      name: 'Test Product',
      value: 50.0,
    });
    const code = createResponse.data.code;

    // Then get it
    const response = await productService.getByCode(code);
    expect(response.success).toBe(true);
    expect(response.data.code).toBe(code);
  });

  it('should update a product', async () => {
    // First create a product
    const createResponse = await productService.create({
      name: 'Test Product',
      value: 50.0,
    });
    const code = createResponse.data.code;

    // Then update it
    const updateResponse = await productService.update(code, {
      name: 'Updated Product',
      value: 75.0,
    });
    expect(updateResponse.success).toBe(true);
    expect(updateResponse.data.name).toBe('Updated Product');
    expect(updateResponse.data.value).toBe(75.0);
  });

  it('should delete a product', async () => {
    // First create a product
    const createResponse = await productService.create({
      name: 'Test Product',
      value: 50.0,
    });
    const code = createResponse.data.code;

    // Then delete it
    const deleteResponse = await productService.delete(code);
    expect(deleteResponse.success).toBe(true);

    // Verify it's deleted
    await expect(productService.getByCode(code)).rejects.toThrow();
  });
});
