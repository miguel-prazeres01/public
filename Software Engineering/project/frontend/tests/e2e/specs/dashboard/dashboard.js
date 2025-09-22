describe('Student Walkthrough', () => {
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();
        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Title',
            'Question',
            'Option',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuestion(
            'Question Title2',
            'Question',
            'Option',
            'Option',
            'Option',
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
        cy.deleteQuestionsAndAnswers();
    });

    it('student creates discussion', () => {
        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        );

        cy.demoStudentLogin();
        cy.solveQuizz('Quiz Title', 2);

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});
