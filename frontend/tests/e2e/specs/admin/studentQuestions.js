it('Create question suggestion', () => {
  cy.demoStudentLogin();
  cy.contains('Suggested').click();

  cy.get('[data-cy="newQuestionBtn"]').click();
  cy.get('#input-92').type('NEW_QUESTION'); // FIXME: #input-91
  cy.get('[data-cy="questionContent"]').type('content');
  cy.get('[data-cy="correct1"]').click({ force: true });
  cy.get('[data-cy="option1"]').type('blablabla');
  cy.get('[data-cy="option2"]').type('blablabla');
  cy.get('[data-cy="option3"]').type('blablabla');
  cy.get('[data-cy="option4"]').type('blablabla');
  cy.get('[data-cy="saveQuestionBtn"]').click();

  cy.contains('NEW_QUESTION');
  cy.contains('Logout').click();
});

//describe('Submit question suggestion', () => {
//beforeEach(() => {
//cy.demoAdminLogin();
//});

//afterEach(() => {
//cy.contains('Logout').click();
//});

//it('login creates and deletes a course execution', () => {
//cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');

//cy.deleteCourseExecution('TEST-AO3');
//});

//it('login creates two course executions and deletes it', () => {
//cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');

//cy.log('try to create with the same name');
//cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');

//cy.closeErrorMessage();

//cy.log('close dialog');
//cy.get('[data-cy="cancelButton"]').click();

//cy.deleteCourseExecution('TEST-AO3');
//});

//it('login creates FROM a course execution and deletes it', () => {
//cy.createFromCourseExecution('Demo Course', 'TEST-AO3', 'Winter Semester');

//cy.deleteCourseExecution('TEST-AO3');
//});
//});
