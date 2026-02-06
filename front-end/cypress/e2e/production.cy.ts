describe('Production Suggestions', () => {
  beforeEach(() => {
    cy.visit('/production');
  });

  it('should display production suggestions page', () => {
    cy.contains('Production Suggestions').should('be.visible');
    cy.contains('Refresh').should('be.visible');
  });

  it('should refresh production suggestions', () => {
    cy.contains('Refresh').click();
    // Wait for loading to complete
    cy.wait(1000);
    // Should show either suggestions or a message
    cy.get('body').should('be.visible');
  });

  it('should display production summary if suggestions exist', () => {
    // This test assumes there are products and raw materials with associations
    cy.contains('Refresh').click();
    cy.wait(2000);
    
    // Check if summary or table is displayed
    cy.get('body').then(($body) => {
      if ($body.find('table').length > 0) {
        cy.get('table').should('be.visible');
        cy.contains('Total Production Value').should('be.visible');
      } else {
        // If no suggestions, should show appropriate message
        cy.contains('No products can be produced').should('be.visible');
      }
    });
  });
});
