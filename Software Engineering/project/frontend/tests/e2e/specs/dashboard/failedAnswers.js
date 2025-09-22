describe('Failed Answer', () =>{
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
        cy.deleteFailedAnswers();
        cy.deleteQuestionsAndAnswers();
    });

    it('student creates discussion', () => {
        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        );

        cy.intercept('GET', '**/students/dashboards/*').as(
            'getFailedAnswers'
        );

        cy.intercept('PUT', '**/students/update/*').as(
            'updateFailedAnswers'
        );

        cy.intercept('DELETE', '**/students/failedAnswers/*').as(
            'deleteFailedAnswer'
        );

        cy.demoStudentLogin();
        cy.solveQuizzWrong('Quiz Title', 2);

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.get('[data-cy="failedAnswersMenuButton"]').click();
        cy.wait('@getFailedAnswers');

        cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
        cy.wait('@updateFailedAnswers');

        cy.get('[data-cy="showStudentViewDialog"]').should("have.length.at.least",1).eq(0).click();
        cy.get('[data-cy="closeButton"]').click();

        cy.get('[data-cy="deleteFailedAnswerButton"]').should("have.length.at.least",1).eq(0).click();
        cy.wait('@deleteFailedAnswer');

        cy.contains('Error').parent().find("button").click();

        cy.setFailedAnswersAsOld();

        cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
        cy.wait('@updateFailedAnswers');

        cy.get('[data-cy="deleteFailedAnswerButton"]').should("have.length.at.least",1).eq(0).click();
        cy.wait('@deleteFailedAnswer');

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });

    });
});