const QUIZ_TITLE = 'Allocation viewtype';
const CLARIFICATION_CONTENT = 'THIS IS A TEST';
const QUESTION = 'A utility tree';

describe('Clarification Request walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.respondQuiz(QUIZ_TITLE);
  });

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Creates a clarification request', () => {
    cy.goToClarification(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
  });

  it('Show Question of a clarification request submitted', () => {
    cy.showClarifications();
    cy.showQuestionClarification(CLARIFICATION_CONTENT);
  });

  it('Show Clarification of a Clarification request submitted', () => {
    cy.showClarifications();
    cy.showClarification(CLARIFICATION_CONTENT);
  });
});
