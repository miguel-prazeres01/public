describe('Difficult Question', () => {
  beforeEach(() => {
    cy.deleteQuestionsAndAnswers();
    //create quiz
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Title',
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'

    );
    cy.createQuestion(
      'Question Title2',
      'Question',
      'Option',
      'Option',
      'ChooseThisWrong',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Quiz Title',
      'Question Title',
      'Question Title2'
    );
    cy.contains('Logout').click();
  });

  afterEach(() => {
    cy.deleteDifficultQuestions();
    cy.deleteQuestionsAndAnswers();
  })

  it('student creates discussion', () => {
    cy.intercept('GET', '**/students/dashboards/executions/*').as(
      'getDashboard'
    );

    cy.intercept('GET', '**/students/dashboards/*/difficultQuestions/Get').as(
      'getDifficultQuestions'
    );

    cy.intercept('PUT', '**/students/dashboards/*/difficultQuestions/Update').as(
      'updateDifficultQuestions'
    );

    cy.intercept('DELETE', '**/students/dashboards/difficultQuestions/Remove/*').as(
      'deleteDifficultQuestion'
    );

    cy.demoStudentLogin();
    cy.solveQuizzWrong('Quiz Title', 2);

    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.wait('@getDashboard');

    cy.get('[data-cy="difficultQuestionsMenuButton"]').click();
    cy.wait('@getDifficultQuestions');

    cy.get('[data-cy="refreshDifficultQuestionsButton"]').click();
    cy.wait('@updateDifficultQuestions');

    cy.get('[data-cy="deleteDifficultQuestionButton"]').should("have.length.at.least",1).eq(0).click();
    cy.wait('@deleteDifficultQuestion');

    cy.contains('Logout').click();
    Cypress.on('uncaught:exception', (err, runnable) => {
      // returning false here prevents Cypress from
      // failing the test
      return false;
    });
  });
});
