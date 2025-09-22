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
class RemoveDifficultQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response
    def student
    def dashboard
    def difficultQuestion
    def question

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
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        difficultQuestion = new DifficultQuestion(dashboard, question, 24)
        difficultQuestionRepository.save(difficultQuestion)
    }

    def "student removes difficult questions"() {
        given: 'a student'

        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: 'the web service is invoked'

        response = restClient.delete(path: '/students/dashboards/difficultQuestions/Remove/' + difficultQuestion.getId(),)

        then:
        response.status == 200
        difficultQuestionRepository.count() == 1L
        difficultQuestionRepository.findAll().get(0).isRemoved() == true
        difficultQuestionRepository.findAll().get(0).getQuestion().getId() == question.getId()

        and:
        def result = difficultQuestionRepository.findAll().get(0)
        result.getId() == difficultQuestion.getId()
        result.isRemoved() == true

    }

    def "teacher cant remove student's difficult questions"() {
        given: 'a teacher'

        demoTeacherLogin()

        when:
        response = restClient.delete(path: '/students/dashboards/difficultQuestions/Remove/' + difficultQuestion.getId(),)

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

    }

    def "student cant remove another students difficult questions"() {
        given: 'student'
        def student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        student1.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        createdUserLogin(USER_2_USERNAME, USER_2_PASSWORD)


        when:
        response = restClient.delete(path: '/students/dashboards/difficultQuestions/Remove/' + difficultQuestion.getId(),)

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN


    }


    def cleanup() {
        userRepository.deleteAll()
        optionRepository.deleteAll()
        dashboardRepository.deleteAll()
        difficultQuestionRepository.deleteAll()
        questionRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
    }
}