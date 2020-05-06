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

describe('Teacher creating evaluation walktrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    data = generateData();
    cy.createStudentSuggestion(data);
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
  });

  it('Create Evaluation', () => {
    cy.createEvaluation(data, 'Good Question', true);
  });

  it('Create invalid Evaluation', () => {
    cy.contains('Management').click();
    cy.contains('Student Questions').click();
    cy.contains('Title').click();
    cy.contains(data.questionContent)
      .parent()
      .parent()
      .children()
      .find('[data-cy="evaluationBtn"]')
      .click();
    cy.contains('Save').click();
    cy.contains('Rejected student questions must have a justification')
      .parent()
      .find('button')
      .click();
  });

  it('Publish student question', () => {
    cy.createEvaluation(data, 'Good Question', true);
    cy.contains(data.questionTitle)
      .parent()
      .contains('publish')
      .parent()
      .find('[data-cy="publish"]')
      .click();

    cy.contains('Management').click();
    cy.contains('Questions').click();
    cy.contains(data.questionTitle).click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });
});
