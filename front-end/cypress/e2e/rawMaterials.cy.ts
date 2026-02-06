describe('Raw Materials CRUD', () => {
  beforeEach(() => {
    cy.visit('/raw-materials');
  });

  it('should display raw materials list', () => {
    cy.contains('Raw Materials').should('be.visible');
    cy.get('table').should('be.visible');
  });

  it('should create a new raw material', () => {
    cy.contains('Create Raw Material').click();
    cy.get('input[type="text"]').first().type('Test Raw Material');
    cy.get('input[type="number"]').type('100');
    cy.contains('Create').click();
    
    // Wait for the raw material to appear in the list
    cy.contains('Test Raw Material').should('be.visible');
  });

  it('should edit an existing raw material', () => {
    // Assuming there's at least one raw material
    cy.get('table tbody tr').first().within(() => {
      cy.get('button[aria-label="Edit"]').click();
    });
    
    // Wait for dialog to open
    cy.contains('Edit Raw Material').should('be.visible');
    cy.get('input[type="text"]').first().clear().type('Updated Raw Material');
    cy.contains('Update').click();
    
    // Verify update
    cy.contains('Updated Raw Material').should('be.visible');
  });

  it('should delete a raw material', () => {
    // Create a raw material first
    cy.contains('Create Raw Material').click();
    cy.get('input[type="text"]').first().type('Raw Material to Delete');
    cy.get('input[type="number"]').type('50');
    cy.contains('Create').click();
    
    // Wait for raw material to be created
    cy.contains('Raw Material to Delete').should('be.visible');
    
    // Delete it
    cy.get('table tbody tr').contains('Raw Material to Delete').parent().within(() => {
      cy.get('button[aria-label="Delete"]').click();
    });
    
    cy.contains('Confirm Delete').should('be.visible');
    cy.contains('Delete').click();
    
    // Verify deletion
    cy.contains('Raw Material to Delete').should('not.exist');
  });
});
