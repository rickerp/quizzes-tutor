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

describe('Teacher editing question walktrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    data = generateData();
    cy.createStudentSuggestion(data);
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.createEvaluation(data, 'Good Question', true);
  });

  it('Edit question', () => {
    cy.contains('Management').click();
    cy.contains('Student Questions').click();
    cy.contains(data.questionTitle)
      .parent()
      .contains('edit')
      .parent()
      .find('[data-cy="edit"]')
      .click();
    cy.get('[data-cy="questionContent"]').type('TESTE');
    cy.get('[data-cy="option1"]').type('TESTE1');
    cy.get('[data-cy="option2"]').type('TESTE2');
    cy.get('[data-cy="option3"]').type('TESTE3');
    cy.get('[data-cy="option4"]').type('TESTE4');
    cy.get('[data-cy="saveQuestionBtn"]').click();
    cy.wait(100);
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });
});
