// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', () => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add(
  'createStudentSuggestion',
  ({ questionTitle, questionContent, options }) => {
    cy.contains('Suggested').click();
    cy.get('[data-cy="newQuestionBtn"]').click();
    questionTitle && cy.get('.questionTitle').type(questionTitle);
    cy.get('[data-cy="questionContent"]').type(questionContent);
    cy.get('[data-cy="correct1"]').click({ force: true });
    cy.get('[data-cy="option1"]').type(options[0]);
    cy.get('[data-cy="option2"]').type(options[1]);
    cy.get('[data-cy="option3"]').type(options[2]);
    cy.get('[data-cy="option4"]').type(options[3]);
    cy.get('[data-cy="saveQuestionBtn"]').click();
    cy.wait(100);
  }
);

Cypress.Commands.add(
  'createEvaluation',
  ({ questionTitle, questionContent, options }, justification, evaluation) => {
    cy.contains('Management').click();
    cy.contains('Student Questions').click();
    cy.contains('Title').click();
    cy.contains(questionContent)
      .parent()
      .parent()
      .children()
      .find('[data-cy="evaluationBtn"]')
      .click();
    if (evaluation) cy.get('[data-cy="accepted"]').click({ force: true });
    cy.get('[data-cy="justification"]').type(justification);
    cy.contains('Save').click();
    cy.contains(questionContent)
      .parent()
      .parent()
      .children()
      .find('[data-cy="evaluationBtn"]')
      .click();
    cy.contains('close').click();
  }
);

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('createQuiz', (title, question) => {
  cy.get('.bttnManagement').click();
  cy.get('[href="/management/quizzes"]').click();
  cy.contains('Title').click();
  cy.get('[data-cy="bttnCreateQuiz"]').click();
  cy.get('[data-cy="QuizTitle"]').type(title);
  cy.get('[data-cy="bttnSearch"]').type(question);
  cy.contains(question)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="ActAddQuestion"]')
    .click();
  cy.get('[data-cy="availableDate"]').click();
  cy.contains('Now').click();
  cy.contains('Save').click();
  cy.wait(100);
});

Cypress.Commands.add('respondQuiz', quiz_title => {
  cy.contains('Available').click();
  cy.contains(quiz_title).click();
  cy.get('.end-quiz').click();
  cy.get('[data-cy="imSure"]').click();
  cy.get('.quizzesButton').click();
});

Cypress.Commands.add('goToMyClarifications', quiz_title => {
  cy.contains('Solved').click();
  cy.contains(quiz_title).click();
  cy.contains('My Clarifications').click();
});

Cypress.Commands.add('goToPublicClarifications', quiz_title => {
  cy.contains('Solved').click();
  cy.contains(quiz_title).click();
  cy.contains('Public Clarifications').click();
});

Cypress.Commands.add('goToQuestionPublicClarification', question => {
  cy.contains('Questions').click();
  cy.get('[data-cy="bttnSearch"]').type(question);
  cy.contains(question)
    .parent()
    .should('have.length', 1)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 10)
    .find('[data-cy="ShowPClarifications"]')
    .click();
});

Cypress.Commands.add('createInvalidClarificationRequest', () => {
  cy.contains('Create').click();
  cy.get('[data-cy="bttnClrfSave"]').click();
  cy.contains('Clarification must have Content');
});

Cypress.Commands.add('createClarificationRequest', clrfContent => {
  cy.contains('Create').click();
  cy.get('[data-cy="ClrfReq"]').type(clrfContent);
  cy.get('[data-cy="bttnClrfSave"]').click();
  cy.contains(clrfContent).click();
});

Cypress.Commands.add('createInvalidClarificationComment', crlfContent => {
  cy.contains(crlfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .find('[data-cy="showClrf"]')
    .click();
  cy.get('[data-cy="bttnAddComment"]').click();
  cy.get('[data-cy="bttnCmtSave"]').click();
  cy.contains('Comment must have Content');
});

Cypress.Commands.add(
  'createClarificationCommentAct',
  (clrfContent, cmtContent) => {
    cy.contains(clrfContent)
      .parent()
      .children()
      .should('have.length', 7)
      .find('[data-cy="addCmt"]')
      .click();
    cy.get('[data-cy="cmtContent"]').type(cmtContent);
    cy.get('[data-cy="bttnCmtSave"]').click();
    cy.contains(clrfContent)
      .parent()
      .children()
      .should('have.length', 7)
      .contains('RESOLVED')
      .click();
  }
);

Cypress.Commands.add(
  'createClarificationCommentChat',
  (clrfContent, cmtContent) => {
    cy.contains(clrfContent)
      .parent()
      .children()
      .should('have.length', 7)
      .find('[data-cy="showClrf"]')
      .click();
    cy.get('[data-cy="bttnAddComment"]').click();
    cy.get('[data-cy="cmtContent"]').type(cmtContent);
    cy.get('[data-cy="bttnCmtSave"]').click();
    cy.contains(cmtContent);
  }
);

Cypress.Commands.add('showClarificationsStudent', () => {
  cy.contains('My Clarifications').click();
});

Cypress.Commands.add('showClarificationsTeacher', () => {
  cy.contains('Clarifications').click();
});

Cypress.Commands.add('showClarificationsStats', () => {
  cy.contains('Dashboard').click();
  cy.contains('Total Clarifications').click();
});

Cypress.Commands.add('changeClarificationsDashState', (oldState, newState) => {
  cy.contains('Dashboard').click();
  cy.contains(oldState).click();
  cy.contains(newState);
});

Cypress.Commands.add('showPublicClarificationsStats', () => {
  cy.contains('Public Statistics').click();
  cy.contains('Demo Student').click();
});

Cypress.Commands.add('changeState', (clrfContent, oldState, newState) => {
  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .find('[data-cy="showClrf"]')
    .click();
  cy.contains(oldState).click();
  cy.get('[data-cy="bttnClose"]').click();
  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .contains(newState)
    .click();
});

Cypress.Commands.add('changeType', (clrfContent, oldType, newType) => {
  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .contains(oldType);

  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .find('[data-cy="changeType"]')
    .click();

  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .contains(newType);
});

Cypress.Commands.add(
  'changeAvailabilityPClarification',
  (clrfContent, newAvailability) => {
    cy.contains(clrfContent)
      .parent()
      .children()
      .should('have.length', 5)
      .find('[data-cy="changeVisibility"]')
      .click();

    cy.contains(clrfContent)
      .parent()
      .children()
      .should('have.length', 5)
      .contains(newAvailability);
  }
);

Cypress.Commands.add('showQuestionClarification', clrfContent => {
  cy.contains(clrfContent)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="showQuestion"]')
    .click();
  cy.get('[data-cy="bttnClose"]').click();
});

Cypress.Commands.add('showClarification', clrfContent => {
  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .find('[data-cy="showClrf"]')
    .click();
  cy.get('[data-cy="bttnClose"]').click();
});

Cypress.Commands.add('deleteClarification', clrfContent => {
  cy.on('window:confirm', () => true);
  cy.contains(clrfContent)
    .parent()
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteClrf"]')
    .click();
});

Cypress.Commands.add('clickRowButton', (elem, button) => {
  cy.contains(elem)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="' + button + '"]')
    .click();
});

Cypress.Commands.add('assertRowField', (elem, field, value) => {
  cy.contains(elem)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="' + field + '"]')
    .first()
    .should('have.text', value);
});

Cypress.Commands.add(
  'createTournament',
  (name, startDay, endDay, nrQuestions) => {
    cy.get('[data-cy="newTdPButton"]').click();
    cy.wait(500);
    cy.get('[data-cy="newTdPName"]').type(name);
    cy.setDay('newTdPStartTime', 'startDateInput', startDay);
    cy.setDay('newTdPEndTime', 'endDateInput', endDay);
    cy.get('[data-cy="newTdPNrQuestions"]')
      .clear()
      .type(nrQuestions.toString());
    cy.get('[data-cy="newTdPTopicsMenu"]').click();
    cy.wait(500);
    cy.get('[data-cy="newTdPTopic"]')
      .first()
      .click();
    cy.get('[data-cy="newTdPTopic"]')
      .last()
      .click();
    cy.get('[data-cy="newTdPSave"]').click();
    cy.wait(500);
  }
);

Cypress.Commands.add('setDay', (button, id, day) => {
  cy.get('[data-cy="' + button + '"]').click();
  cy.wait(500);
  cy.get(
    '#' +
      id +
      '-picker-container-DatePicker' +
      '> .calendar' +
      '> .datepicker-controls' +
      '> .text-right' +
      '> .datepicker-button'
  ).click();
  cy.wait(500);
  cy.get(
    '#' +
      id +
      '-picker-container-DatePicker' +
      '> .calendar' +
      '> .month-container' +
      '> :nth-child(1)' +
      '> .datepicker-days' +
      '> :nth-child(' +
      day +
      ')'
  ).click();
  cy.get(
    '#' +
      id +
      '-wrapper' +
      '> .datetimepicker' +
      '> .datepicker' +
      '> .datepicker-buttons-container' +
      '> .validate'
  ).click({ force: true });
  cy.wait(500);
});

Cypress.Commands.add('doTournamentQuiz', options => {
  let i = options.length;
  cy.wrap(options).each(option => {
    cy.get('.option-list > :nth-child(' + option + ')').click();
    cy.wait(500);
    if (--i > 0) cy.get('[data-cy="NextQuestionTdP"]').click();
    else cy.get('[data-cy="FinishQuizTdP"]').click();
  });
  cy.get('[data-cy="Im-SureTdP"]').click();
});

Cypress.Commands.add('seeTournamentQuizSolution', nrQuestions => {
  let i = nrQuestions;
  cy.wrap(Array.from({ length: nrQuestions })).each(() => {
    cy.wait(500);
    if (--i > 0) cy.get('[data-cy="NextQuestionTdP"]').click();
  });
});
