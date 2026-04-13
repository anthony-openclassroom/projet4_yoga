declare namespace Cypress {
  interface Chainable {
    login(admin?: boolean): Chainable<void>;
  }
}

Cypress.Commands.add('login', (admin = false) => {
  cy.intercept('POST', '/api/auth/login', {
    body: {
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'Admin',
      lastName: 'User',
      admin,
    },
  }).as('loginRequest');

  cy.intercept('GET', '/api/session', []).as('sessions');

  cy.visit('/login');
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234{enter}');
  cy.url().should('include', '/sessions');
});
