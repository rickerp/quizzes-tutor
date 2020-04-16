describe('Tournaments Calendar Tests', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
    cy.contains('Calendar').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and displays the topics', () => {
    cy.clickButton('Demo-Tournament-JS', 'topicsTdP');
  });

  it('login, displays the topics and enrolls in a tournament', () => {
    cy.clickButton('Demo-Tournament-Python', 'topicsTdP');
    cy.clickButton('Demo-Tournament-Python', 'enrollTdP');
    cy.assertValue('Demo-Tournament-Python', 'enrollmentsTdP', '1');
  });

  it('login and enrolls in 2 tournaments', () => {
    cy.clickButton('Demo-Tournament-PEPE', 'enrollTdP');
    cy.assertValue('Demo-Tournament-PEPE', 'enrollmentsTdP', '1');
    cy.clickButton('Demo-Tournament-C++', 'enrollTdP');
    cy.assertValue('Demo-Tournament-C++', 'enrollmentsTdP', '1');
  });
});
