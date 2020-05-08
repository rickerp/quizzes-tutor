const QUIZ_TITLE = 'My Quiz Test';
const QUESTION = 'UtilityTree';
const COMMENT_CONTENT = 'THIS IS A COMMENT TO A CLARIFICATION';
const CLARIFICATION_CONTENT = 'THIS IS A CLARIFICATION REQUEST';
const CLARIFICATION_CONTENT2 = 'THIS IS A 2nd CLARIFICATION REQUEST';

describe('Clarification Comments Walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.respondQuiz(QUIZ_TITLE);
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
    cy.createClarificationRequest(CLARIFICATION_CONTENT2);
  });

  beforeEach(() => {});

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Teacher Creates a invalid Clarification Comment', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.createInvalidClarificationComment(CLARIFICATION_CONTENT);
  });

  it('Teacher Creates Clarification Comment in Action', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.createClarificationCommentAct(
      CLARIFICATION_CONTENT,
      COMMENT_CONTENT + ' TEACHER'
    );
  });

  it('Teacher Creates Create Clarification Comment in Chat View', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.createClarificationCommentChat(
      CLARIFICATION_CONTENT2,
      COMMENT_CONTENT + ' TEACHER'
    );
  });

  it('Student Creates a invalid Clarification Comment', () => {
    cy.demoStudentLogin();
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.createInvalidClarificationComment(CLARIFICATION_CONTENT);
  });

  it('Student Creates Clarification Comment in Action', () => {
    cy.demoStudentLogin();
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.createClarificationCommentAct(
      CLARIFICATION_CONTENT,
      COMMENT_CONTENT + ' STUDENT'
    );
  });

  it('Student Creates Clarification Comment in Chat View', () => {
    cy.demoStudentLogin();
    cy.get('.bttnClr').click();
    cy.showClarificationsStudent();
    cy.createClarificationCommentChat(
      CLARIFICATION_CONTENT2,
      COMMENT_CONTENT + ' STUDENT'
    );
  });
});
