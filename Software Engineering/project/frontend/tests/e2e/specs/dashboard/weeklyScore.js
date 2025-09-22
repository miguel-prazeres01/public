describe('Weekly Score', () =>{
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();
        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Title',
            'Question',
            'ChooseThisWrong',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuestion(
            'Question Title2',
            'Question',
            'ChooseThisWrong',
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
        cy.deleteWeeklyScores();
        cy.deleteQuestionsAndAnswers();
    });

    it('student creates discussion', () => {
        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        );

        cy.intercept('GET', '**/students/dashboards/*/weeklyScores/Get').as(
            'getWeeklyScores'
        );

        cy.intercept('PUT', '**/students/dashboards/*/weeklyScores/Update').as(
            'updateWeeklyScores'
        );

        cy.intercept('DELETE', '**/students/dashboards/weeklyScores/*/Remove').as(
            'deleteWeeklyScore'
        );

        cy.demoStudentLogin();
        cy.solveQuizzWrong('Quiz Title', 2);

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.get('[data-cy="weeklyScoresMenuButton"]').click();
        cy.wait('@getWeeklyScores');

        cy.get('[data-cy="refreshWeeklyScoresMenuButton"]').click();
        cy.wait('@updateWeeklyScores');

        cy.get('[data-cy="deleteWeeklyScoreButton"]').should("have.length.at.least",1).eq(0).click();
        cy.wait('@deleteWeeklyScore');

        cy.contains('Error').parent().find("button").click();

        cy.createWeeklyScore();

        cy.get('[data-cy="refreshWeeklyScoresMenuButton"]').click();
        cy.wait('@updateWeeklyScores');

        cy.get('[data-cy="deleteWeeklyScoreButton"]').should("have.length.at.least",1).eq(1).click();
        cy.wait('@deleteWeeklyScore');

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });

    });
});