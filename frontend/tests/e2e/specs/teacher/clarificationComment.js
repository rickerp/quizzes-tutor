const QUIZ_TITLE = 'My Quiz Test';
const QUESTION = 'A utility tree';
const COMMENT_CONTENT = 'THIS IS A COMMENT TO A CLARIFICATION';
const CLARIFICATION_CONTENT = 'THIS IS A CLARIFICATION REQUEST';
const CLARIFICATION_CONTENT2 = 'THIS IS A 2nd CLARIFICATION REQUEST';

describe('Clarification Comment Walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.respondQuiz(QUIZ_TITLE);
    cy.goToClarification(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
    cy.createClarificationRequest(CLARIFICATION_CONTENT2);
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create a invalid Clarification Comment', () => {
    cy.showClarifications();
    cy.createInvalidClarificationComment(CLARIFICATION_CONTENT);
  });

  it('Create Clarification Comment in Action', () => {
    cy.showClarifications();
    cy.createClarificationCommentAct(CLARIFICATION_CONTENT, COMMENT_CONTENT);
  });

  it('Create Clarification Comment in Chat View', () => {
    cy.showClarifications();
    cy.createClarificationCommentChat(CLARIFICATION_CONTENT2, COMMENT_CONTENT);
  });
});
