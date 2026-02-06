describe('Product Raw Materials Association', () => {
  beforeEach(() => {
    // Ensure we have products and raw materials
    cy.visit('/products');
  });

  it('should associate raw materials to a product', () => {
    // Create a product first if needed
    cy.contains('Create Product').click();
    cy.get('input[type="text"]').first().type('Product for Association');
    cy.get('input[type="number"]').type('100');
    cy.contains('Create').click();
    
    // Wait for product to be created
    cy.contains('Product for Association').should('be.visible');
    
    // Click on manage raw materials (first edit icon)
    cy.get('table tbody tr').contains('Product for Association').parent().within(() => {
      cy.get('button[aria-label="Manage Raw Materials"]').first().click();
    });
    
    // Wait for dialog to open
    cy.contains('Manage Raw Materials').should('be.visible');
    
    // Select a raw material and add it
    cy.get('select').first().select(0);
    cy.get('input[type="number"]').last().type('2');
    cy.contains('Add').click();
    
    // Verify association was added
    cy.get('table').should('contain', 'Raw Material');
  });
});
