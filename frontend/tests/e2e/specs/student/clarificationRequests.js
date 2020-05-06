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

  beforeEach( () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create a invalid clarification request', () => {
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createInvalidClarificationRequest();
  });

  it('Creates a clarification request', () => {
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
  });

  it('Change ClarificationState in Chat', () => {
    cy.showClarifications();
    cy.changeState(CLARIFICATION_CONTENT,'Unresolved', 'RESOLVED');
    cy.changeState(CLARIFICATION_CONTENT,'Resolved', 'UNRESOLVED');
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
