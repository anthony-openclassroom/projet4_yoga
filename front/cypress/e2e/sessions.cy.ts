const mockSession = {
  id: 1,
  name: 'Morning Yoga',
  description: 'A relaxing morning session',
  date: '2024-06-15T00:00:00.000Z',
  teacher_id: 1,
  users: [],
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
};

const mockTeacher = {
  id: 1,
  lastName: 'Doe',
  firstName: 'John',
  createdAt: '2024-01-01T00:00:00.000Z',
  updatedAt: '2024-01-01T00:00:00.000Z',
};

// ─── LISTE DES SESSIONS ──────────────────────────────────────────────────────

describe('Sessions — liste', () => {
  it('utilisateur admin — affiche la liste et le bouton Créer', () => {
    cy.login(true);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    cy.contains('Rentals available').should('be.visible');
    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('Create').should('be.visible');
  });

  it('utilisateur non-admin — pas de bouton Créer', () => {
    cy.login(false);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    cy.contains('Rentals available').should('be.visible');
    cy.get('button').contains('Create').should('not.exist');
  });
});

// ─── DÉTAIL D'UNE SESSION ────────────────────────────────────────────────────

describe('Sessions — détail', () => {
  it('admin — affiche le détail et le bouton Delete', () => {
    cy.login(true);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');
    cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');

    cy.contains('Detail').first().click();

    cy.url().should('include', '/sessions/detail/1');
    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('Delete').should('be.visible');
  });

  it('non-admin — affiche Participate, pas de Delete', () => {
    cy.login(false);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');
    cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');

    cy.contains('Detail').first().click();

    cy.url().should('include', '/sessions/detail/1');
    cy.contains('Participate').should('be.visible');
    cy.get('button').contains('Delete').should('not.exist');
  });

  it('admin — supprime la session et revient à la liste', () => {
    cy.login(true);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');
    cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');
    cy.intercept('DELETE', '/api/session/1', {}).as('deleteSession');

    cy.contains('Detail').first().click();
    cy.contains('Delete').click();

    cy.url().should('include', '/sessions');
  });
});

// ─── FORMULAIRE — CRÉATION ───────────────────────────────────────────────────

describe('Sessions — création', () => {
  it('admin — remplit et soumet le formulaire de création', () => {
    cy.login(true);

    cy.intercept('GET', '/api/session', []).as('sessions');
    cy.intercept('GET', '/api/teacher', [mockTeacher]).as('teachers');
    cy.intercept('POST', '/api/session', mockSession).as('createSession');

    cy.contains('Create').click();
    cy.url().should('include', '/sessions/create');

    cy.get('input[formControlName=name]').type('Morning Yoga');
    cy.get('input[formControlName=date]').type('2024-06-15');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').first().click();
    cy.get('textarea[formControlName=description]').type('A relaxing morning session');

    cy.intercept('GET', '/api/session', [mockSession]).as('sessionsAfterCreate');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');
  });
});

// ─── FORMULAIRE — MODIFICATION ───────────────────────────────────────────────

describe('Sessions — modification', () => {
  it('admin — modifie une session existante', () => {
    cy.login(true);

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');
    cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
    cy.intercept('GET', '/api/teacher', [mockTeacher]).as('teachers');
    cy.intercept('PUT', '/api/session/1', { ...mockSession, name: 'Updated Yoga' }).as('updateSession');

    cy.get('button').contains('Edit').first().click();
    cy.url().should('include', '/sessions/update/1');

    cy.get('input[formControlName=name]').clear().type('Updated Yoga');

    cy.intercept('GET', '/api/session', [{ ...mockSession, name: 'Updated Yoga' }]).as('sessionsAfterUpdate');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');
  });
});
