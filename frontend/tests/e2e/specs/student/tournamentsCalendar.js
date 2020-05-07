describe('Tournaments Calendar Tests', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
    cy.contains('Calendar').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Creates a Tournament & Cancels it', () => {
    cy.createTournament('Demo-Tournament', 8, 9, 5);
    cy.clickRowButton('Demo-Tournament', 'cancelTdP');
  });

  it('Creates a tournament & Displays its Topics & Cancels it', () => {
    cy.createTournament('Demo-Tournament-JS', 10, 11, 10);
    cy.clickRowButton('Demo-Tournament-JS', 'topicsTdP');
    cy.clickRowButton('Demo-Tournament-JS', 'cancelTdP');
  });

  it('Creates a Tournament & Displays its Topics & Enrolls in it & Cancels it', () => {
    cy.createTournament('Demo-Tournament-Python', 12, 13, 15);
    cy.clickRowButton('Demo-Tournament-Python', 'topicsTdP');
    cy.clickRowButton('Demo-Tournament-Python', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-Python', 'enrollmentsTdP', '1');
    cy.clickRowButton('Demo-Tournament-Python', 'cancelTdP');
  });

  it('Creates 2 Tournaments & Enrolls in them & Cancels them', () => {
    cy.createTournament('Demo-Tournament-PEPE', 14, 15, 20);
    cy.clickRowButton('Demo-Tournament-PEPE', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-PEPE', 'enrollmentsTdP', '1');
    cy.createTournament('Demo-Tournament-C++', 16, 17, 25);
    cy.clickRowButton('Demo-Tournament-C++', 'enrollTdP');
    cy.assertRowField('Demo-Tournament-C++', 'enrollmentsTdP', '1');
    cy.clickRowButton('Demo-Tournament-PEPE', 'cancelTdP');
    cy.clickRowButton('Demo-Tournament-C++', 'cancelTdP');
  });
});
