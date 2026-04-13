const mockUser = {
  id: 1,
  email: 'yoga@studio.com',
  lastName: 'User',
  firstName: 'Admin',
  admin: false,
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
};

const mockAdminUser = {
  id: 1,
  email: 'yoga@studio.com',
  lastName: 'User',
  firstName: 'Admin',
  admin: true,
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
};

describe('Me — profil utilisateur', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', []).as('sessions');
    cy.login(false);

    cy.intercept('GET', '/api/user/1', mockUser).as('userInfo');

    cy.contains('Account').click();
    cy.url().should('include', '/me');
  });

  it('affiche les informations de l\'utilisateur', () => {
    cy.contains('User information').should('be.visible');
    cy.contains('Admin').should('be.visible');
    cy.contains('yoga@studio.com').should('be.visible');
  });

  it('bouton back revient à la page précédente', () => {
    cy.get('mat-icon').contains('arrow_back').parents('button').click();
    cy.url().should('include', '/sessions');
  });

  it('supprime le compte et redirige vers la page d\'accueil', () => {
    cy.intercept('DELETE', '/api/user/1', {}).as('deleteUser');

    // Le bouton de suppression affiche "Detail" (libellé dans le template)
    cy.get('button[color=warn]').click();

    cy.url().should('include', '/login');
  });
});

describe('Me — profil administrateur', () => {
  it('affiche "You are admin" et pas de bouton de suppression', () => {
    cy.intercept('GET', '/api/session', []).as('sessions');
    cy.login(true);

    cy.intercept('GET', '/api/user/1', mockAdminUser).as('userInfo');

    cy.contains('Account').click();
    cy.url().should('include', '/me');

    cy.contains('You are admin').should('be.visible');
    cy.get('button[color=warn]').should('not.exist');
  });
});
