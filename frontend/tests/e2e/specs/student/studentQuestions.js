function generateData() {
  const rand = Date.now();
  const questionContent = 'content' + rand;
  const options = Array(4)
    .fill()
    .map((_, i) => 'option' + i + rand);
  const questionTitle = 'NEW_QUESTION_' + rand;
  return { questionContent, options, questionTitle };
}

describe('Student suggested question workflow', () => {
  before(() => {
    cy.demoStudentLogin();
  });

  it('Create question suggestion', () => {
    let data;
    let { questionContent, options, questionTitle } = (data = generateData());
    cy.createStudentSuggestion(data);
    cy.contains(questionTitle);
    cy.contains(questionContent).click();
    options.forEach(o => cy.contains(o));
    cy.contains('close').click();
  });

  it('Create invalid question suggestion', () => {
    let data = generateData();
    data.questionTitle = '';
    cy.createStudentSuggestion(data);
    cy.contains('Question must have title and content')
      .parent()
      .find('button')
      .click();
  });

  it('Public dashboard screen', () => {
    cy.contains('Suggested Question').click();
    cy.contains('Participate in public dashboard');
  });

  it('Private stats screen', () => {
    cy.contains('Suggested Question').click();
    cy.contains('Statistics').click();
    cy.contains('Total').click();
  });

  after(() => {
    cy.contains('Logout').click();
  });
});
