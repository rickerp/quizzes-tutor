/* To Run Tournament Quizzes Tests:
  - Run `npm install`
  - Set `tutordb` 'user' & 'password' at 'tests/e2e/plugins/index.js'
 */
describe('Tournament Quizzes and Dashboard Tests', () => {
  beforeEach(() => {
    /* Login */
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
    cy.contains('Calendar').click();
    /* Create a Tournament */
    cy.createTournament('Demo-Tournament-Quiz', 8, 9, 5);
    /* Display Topics of Tournament */
    cy.clickRowButton('Demo-Tournament-Quiz', 'topicsTdP');
    /* Get Tournament Id */
    cy.task('query', {
      sql:
        "SELECT id FROM tournaments WHERE name = 'Demo-Tournament-Quiz' " +
        'AND creator_id = 676'
    }).then(command => {
      let tid = command?.rows[command?.rows.length - 1].id;
      /* Enroll User in Tournament */
      cy.task('query', {
        sql:
          'INSERT INTO tournament_answers(tournament_id, user_id) VALUES ($1, 651)',
        values: [tid]
      });
      /* Enroll Creator in Tournament (Generating the Quiz for Tournament) */
      cy.clickRowButton('Demo-Tournament-Quiz', 'enrollTdP');
      cy.assertRowField('Demo-Tournament-Quiz', 'enrollmentsTdP', '2');
      /* Opened Tournament > In Progress Tournament */
      cy.task('query', {
        sql:
          "UPDATE tournaments SET start_time = '2000-01-01T12:00:00Z' WHERE id = $1",
        values: [tid]
      });
    });
    cy.contains('Tournaments').click();
    cy.contains('In progress').click();
  });

  afterEach(() => {
    /* Check Dashboard */
    cy.contains('Tournaments').click();
    cy.contains('Dashboard').click();
    cy.wait(1000);
    cy.get('[data-cy="dashPrivacyTdP"]').parent().click();
    cy.wait(500);
    cy.contains('Demo-Tournament-Quiz')
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 4);
    cy.wait(500);
    cy.contains('Logout').click();
  });

  it('Complete a Tournament Quiz & See Results & Check Dashboard', () => {
    cy.clickRowButton('Demo-Tournament-Quiz', 'playTdP');
    cy.wait(500);
    cy.doTournamentQuiz(['1', '2', '1', '2', '1']);
    cy.seeTournamentQuizSolution(5);
  });

  it('End Tournament Quiz (Without Answering) & See Results & Check Dashboard', () => {
    cy.clickRowButton('Demo-Tournament-Quiz', 'playTdP');
    cy.wait(500);
    cy.get('[data-cy="FinishQuizTdP"]').click();
    cy.get('[data-cy="Im-SureTdP"]').click();
    cy.seeTournamentQuizSolution(5);
  });
});
