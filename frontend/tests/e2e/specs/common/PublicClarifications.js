const QUIZ_TITLE = 'My Quiz Test';
const CLARIFICATION_CONTENT = 'FOR A PUBLIC CLARIFICATION TEST';
const QUESTION = 'UtilityTree';

describe('Public Clarification walkthrough', () => {
  before(() => {
    cy.demoTeacherLogin();
    cy.createQuiz(QUIZ_TITLE, QUESTION);
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.respondQuiz(QUIZ_TITLE);
    cy.goToMyClarifications(QUIZ_TITLE);
    cy.createClarificationRequest(CLARIFICATION_CONTENT);
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.changeState(CLARIFICATION_CONTENT,'Unresolved', 'RESOLVED');
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create a Public Clarification', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.changeType(CLARIFICATION_CONTENT, 'PRIVATE', 'PUBLIC');
  });

  it('Show Public Clarification to Student with a Public Clarification', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.goToPublicClarifications(QUIZ_TITLE);
    cy.contains(CLARIFICATION_CONTENT);
  });


  it('Make Public Clarification not Visible To Student', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.goToQuestionPublicClarification(QUESTION);
    cy.changeAvailabilityPClarification(CLARIFICATION_CONTENT, 'INVISIBLE');
  });

  it('Show Public Clarification to Student with no Public Clarification', () => {
    cy.demoStudentLogin();
    cy.get('.quizzesButton').click();
    cy.goToPublicClarifications(QUIZ_TITLE);
    cy.contains(CLARIFICATION_CONTENT).should('not.exist');
  });

  it('Make Public Clarification Visible To Student', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.goToQuestionPublicClarification(QUESTION);
    cy.changeAvailabilityPClarification(CLARIFICATION_CONTENT, 'VISIBLE');
  });

  it('Make Public Clarification Private', () => {
    cy.demoTeacherLogin();
    cy.get('.bttnManagement').click();
    cy.showClarificationsTeacher();
    cy.changeType(CLARIFICATION_CONTENT, 'PUBLIC', 'PRIVATE');
  });
});
