describe('Tournaments Calendar Tests', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
    cy.contains('Calendar').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('creates a tournament', () => {
    cy.createTournament('Demo-Tournament', 8, 9, 5);
  });

  it('creates a tournament and displays its topics', () => {
    cy.createTournament('Demo-Tournament-JS', 10, 11, 10);
    cy.clickRowButton('Demo-Tournament-JS', 'topicsTdP');
  });

  it('creates a tournament, displays its topics and enrolls in it', () => {
    cy.createTournament('Demo-Tournament-Python', 12, 13, 15);
    cy.clickRowButton('Demo-Tournament-Python', 'topicsTdP');
    cy.clickRowButton('Demo-Tournament-Python', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-Python', 'enrollmentsTdP', '1');
  });

  it('creates 2 tournaments and enrolls in them', () => {
    cy.createTournament('Demo-Tournament-PEPE', 14, 15, 20);
    cy.clickRowButton('Demo-Tournament-PEPE', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-PEPE', 'enrollmentsTdP', '1');
    cy.createTournament('Demo-Tournament-C++', 16, 17, 25);
    cy.clickRowButton('Demo-Tournament-C++', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-C++', 'enrollmentsTdP', '1');
  });
});
