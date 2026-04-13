describe('Page Not Found', () => {
  it('affiche la page 404 pour une URL inconnue', () => {
    cy.visit('/this-page-does-not-exist');
    cy.contains('Page not found !').should('be.visible');
  });
});
