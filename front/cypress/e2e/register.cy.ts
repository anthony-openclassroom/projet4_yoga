describe('Register', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('affiche le formulaire d\'inscription', () => {
    cy.get('input[formControlName=firstName]').should('exist');
    cy.get('input[formControlName=lastName]').should('exist');
    cy.get('input[formControlName=email]').should('exist');
    cy.get('input[formControlName=password]').should('exist');
    cy.get('button[type=submit]').should('exist');
  });

  it('inscription réussie — redirige vers /login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {},
    }).as('register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@test.com');
    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/login');
  });

  it('échec d\'inscription — affiche un message d\'erreur', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' },
    }).as('registerFailed');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('existing@test.com');
    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').click();

    cy.get('.error').should('be.visible');
  });
});
