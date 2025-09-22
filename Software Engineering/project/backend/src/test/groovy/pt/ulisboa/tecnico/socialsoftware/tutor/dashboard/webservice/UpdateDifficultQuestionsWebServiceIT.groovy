package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateDifficultQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port
    private long compare

    def response
    def student
    def question
    def dashboard

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        def now = DateHandler.now()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and:
        def optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        and:
        def optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and:
        def quiz = new Quiz()
        quiz.setAvailableDate(now.minusMinutes(5))
        quiz.setResultsDate(now)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)
        and:
        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
    }

    def "student updates difficult questions"() {
        given: 'student'
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        def dashboardDto = dashboardService.getDashboard(externalCourseExecution.getId(), student.getId())
        compare = difficultQuestionRepository.count();
        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/difficultQuestions/Update',
                requestContentType: 'application/json'
        )


        then: "the request returns 200"
        response.status == 200
        and: "has value"
        response != null
        compare == 0L
        difficultQuestionRepository.findAll().get(0).getQuestion().getId()==question.getId()
        difficultQuestionRepository.count() == 1L

    }

    def "teacher cant update student's difficult questions"() {
        given: 'Teacher'
        demoTeacherLogin()

        def dashboardDto = dashboardService.getDashboard(externalCourseExecution.getId(), student.getId())

        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/difficultQuestions/Update',
                requestContentType: 'application/json'
        )

        then: "the request returns 403 - not authorized"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        difficultQuestionRepository.count() == 0L


    }


    def "student cant update another students difficult questions"() {
        given: 'a demo student'
        def student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        student1.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        createdUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        def dashboardDto = dashboardService.getDashboard(externalCourseExecution.getId(), student.getId())

        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/difficultQuestions/Update',
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        difficultQuestionRepository.count() == 0L

    }
    def cleanup(){
        userRepository.deleteAll()
        optionRepository.deleteAll()
        dashboardRepository.deleteAll()
        difficultQuestionRepository.deleteAll()
        questionRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
    }

}