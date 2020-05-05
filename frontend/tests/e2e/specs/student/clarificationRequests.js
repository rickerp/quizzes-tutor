const QUIZ_TITLE = 'My Quiz Test';
const CLARIFICATION_CONTENT = 'THIS IS A CLARIFICATION REQUEST TEST';
const QUESTION = 'Utility tree';

describe('Clarification Request walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.respondQuiz(QUIZ_TITLE);
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create a invalid clarification request', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.goToClarification(QUIZ_TITLE);
    cy.createInvalidClarificationRequest();
  });

  it('Creates a clarification request', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.goToClarification(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
  });

  it('Change ClarificationState', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.showClarifications();
    cy.changeState(CLARIFICATION_CONTENT,'Unresolved', 'RESOLVED');
    cy.changeState(CLARIFICATION_CONTENT,'Resolved', 'UNRESOLVED');
  });


  it('Change ClarificationType', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarifications();
    cy.changeState(CLARIFICATION_CONTENT,'Unresolved', 'RESOLVED');
    cy.changeType(CLARIFICATION_CONTENT, 'PRIVATE', 'PUBLIC');
    cy.changeType(CLARIFICATION_CONTENT, 'PUBLIC', 'PRIVATE');
  });

  it('Show Question of a clarification request submitted', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.showClarifications();
    cy.showQuestionClarification(CLARIFICATION_CONTENT);
  });

  it('Show Clarification of a Clarification request submitted', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.showClarifications();
    cy.showClarification(CLARIFICATION_CONTENT);
  });
});
