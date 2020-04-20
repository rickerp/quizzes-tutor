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
Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

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
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
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

Cypress.Commands.add('createTournament', (name, day, nrQuestions) => {
  cy.get('[data-cy="newTdPButton"]').click();
  cy.get('[data-cy="newTdPName"]').type(name);
  cy.get('[data-cy="newTdPStartDateMenu"]').click();
  cy.get('.v-date-picker-header')
    .children()
    .last()
    .click();
  cy.wait(500);
  cy.get('.v-date-picker-table')
    .children()
    .contains(day.toString())
    .click();
  cy.wait(500);
  cy.get('[data-cy="newTdPNrQuestions"]')
    .clear()
    .type(nrQuestions.toString());
  cy.get('[data-cy="newTdPTopicsMenu"]').click();
  cy.get('[data-cy="newTdPTopic"]')
    .first()
    .click();
  cy.get('[data-cy="newTdPTopic"]')
    .last()
    .click();
  cy.get('[data-cy="newTdPSave"]').click();
  cy.wait(500);
});
