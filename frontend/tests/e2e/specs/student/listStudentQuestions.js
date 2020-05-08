function generateData() {
  const rand = Date.now();
  const questionContent = 'content' + rand;
  const options = Array(4)
    .fill()
    .map((_, i) => 'option' + i + rand);
  const questionTitle = 'NEW_QUESTION_' + rand;
  return { questionContent, options, questionTitle };
}

let data;

describe('List student question walktrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    data = generateData();
    cy.createStudentSuggestion(data);
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.createEvaluation(data, 'Good Question', true);
    cy.contains('Logout').click();
    cy.demoStudentLogin();
  });

  it('View student question info', () => {
    cy.contains('Suggested Questions').click();
    cy.contains(data.questionContent)
      .parent()
      .parent()
      .children()
      .find('[data-cy="evaluationBtn"]')
      .click();
    cy.contains('close').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });
});
