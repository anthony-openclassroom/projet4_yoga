describe('AuthGuard — redirection si non connecté', () => {
  it('visite /sessions sans être connecté — redirige vers /login', () => {
    cy.visit('/sessions');
    cy.url().should('include', '/login');
  });

  it('visite /me sans être connecté — redirige vers /login', () => {
    cy.visit('/me');
    cy.url().should('include', '/login');
  });

  it('visite /sessions/create sans être connecté — redirige vers /login', () => {
    cy.visit('/sessions/create');
    cy.url().should('include', '/login');
  });
});
