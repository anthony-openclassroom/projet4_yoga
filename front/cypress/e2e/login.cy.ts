describe('Login', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('affiche le formulaire de connexion', () => {
    cy.get('input[formControlName=email]').should('exist');
    cy.get('input[formControlName=password]').should('exist');
    cy.get('button[type=submit]').should('exist');
  });

  it('connexion réussie — redirige vers /sessions', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
      },
    }).as('loginRequest');

    cy.intercept('GET', '/api/session', []).as('sessions');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').contains('Submit').click();

    cy.url().should('include', '/sessions');
  });

  it('échec de connexion — affiche un message d\'erreur', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Unauthorized' },
    }).as('loginFailed');

    cy.get('input[formControlName=email]').type('wrong@email.com');
    cy.get('input[formControlName=password]').type('wrongpassword');
    cy.get('button[type=submit]').contains('Submit').click();

    cy.get('.error').should('be.visible');
  });

  it('lien Register dans la navbar — redirige vers /register', () => {
    cy.contains('Register').click();
    cy.url().should('include', '/register');
  });
});

describe('Logout', () => {
  it('déconnexion — redirige vers la page d\'accueil', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'User',
        admin: false,
      },
    }).as('loginRequest');

    cy.intercept('GET', '/api/session', []).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').contains('Submit').click();
    cy.url().should('include', '/sessions');

    cy.contains('Logout').click();
    cy.url().should('include', '/login');
  });
});
