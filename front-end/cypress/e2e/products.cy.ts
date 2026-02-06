describe('Products CRUD', () => {
  beforeEach(() => {
    cy.visit('/products');
  });

  it('should display products list', () => {
    cy.contains('Products').should('be.visible');
    cy.get('table').should('be.visible');
  });

  it('should create a new product', () => {
    cy.contains('Create Product').click();
    cy.get('input[type="text"]').first().type('Test Product');
    cy.get('input[type="number"]').type('99.99');
    cy.contains('Create').click();
    
    // Wait for the product to appear in the list
    cy.contains('Test Product').should('be.visible');
  });

  it('should edit an existing product', () => {
    // Assuming there's at least one product
    cy.get('table tbody tr').first().within(() => {
      cy.get('button[aria-label="Edit"]').first().click();
    });
    
    // Wait for dialog to open
    cy.contains('Edit Product').should('be.visible');
    cy.get('input[type="text"]').first().clear().type('Updated Product');
    cy.contains('Update').click();
    
    // Verify update
    cy.contains('Updated Product').should('be.visible');
  });

  it('should delete a product', () => {
    // Create a product first
    cy.contains('Create Product').click();
    cy.get('input[type="text"]').first().type('Product to Delete');
    cy.get('input[type="number"]').type('50');
    cy.contains('Create').click();
    
    // Wait for product to be created
    cy.contains('Product to Delete').should('be.visible');
    
    // Delete it
    cy.get('table tbody tr').contains('Product to Delete').parent().within(() => {
      cy.get('button[aria-label="Delete"]').click();
    });
    
    cy.contains('Confirm Delete').should('be.visible');
    cy.contains('Delete').click();
    
    // Verify deletion
    cy.contains('Product to Delete').should('not.exist');
  });
});
