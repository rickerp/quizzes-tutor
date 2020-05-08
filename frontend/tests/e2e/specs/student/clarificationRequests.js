const QUIZ_TITLE = 'My Quiz Test';
const CLARIFICATION_CONTENT = 'THIS IS A CLARIFICATION REQUEST TEST';
const QUESTION = 'UtilityTree';

describe('Clarification Requests walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.respondQuiz(QUIZ_TITLE);
  });

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create a invalid clarification request', () => {
    cy.get('.quizzesButton').click();
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createInvalidClarificationRequest();
  });

  it('Creates a clarification request', () => {
    cy.get('.quizzesButton').click();
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
  });

  it('Change ClarificationState', () => {
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.changeState(CLARIFICATION_CONTENT, 'Unresolved', 'RESOLVED');
    cy.changeState(CLARIFICATION_CONTENT, 'Resolved', 'UNRESOLVED');
  });

  it('Show Question of a clarification request submitted', () => {
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.showQuestionClarification(CLARIFICATION_CONTENT);
  });

  it('Show Clarification of a Clarification request submitted', () => {
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.showClarification(CLARIFICATION_CONTENT);
  });

  it('Delete a Clarification', () => {
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.deleteClarification(CLARIFICATION_CONTENT);
  });

  it('Show Clarification Stats', () => {
    cy.get('.bttnClr').click();
    cy.showClarificationsStats();
  });

  it('Change Clarifications Dashboard State', () => {
    cy.get('.bttnClr').click();
    cy.changeClarificationsDashState('Private', 'Public');
  });

  it('Show Public Clarification Stats', () => {
    cy.get('.bttnClr').click();
    cy.showPublicClarificationsStats();
    cy.get('.bttnClr').click();
    cy.changeClarificationsDashState('Public', 'Private');
  });

});
