package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def quiz
    def quizQuestion
    def failedAnswer

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        quiz = createQuiz(1)
        quizQuestion = createQuestion(1, quiz)
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        failedAnswer = createFailedAnswer(questionAnswer, LocalDateTime.now().minusDays(8))
    }

    def "student gets failed answers from dashboard then removes it"() {
        given: 'a demo student'
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: "/students/failedAnswers/" + failedAnswer.getId(),
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        and:"removes failed answer"
        failedAnswerRepository.delete(failedAnswer)


        and:"has no value"
        failedAnswerRepository.findAll().size()==0
    }

    def "teacher can't get remove student's failed answers from dashboard"() {
        given: 'demo teacher'
        def teacher= new Teacher(USER_2_NAME,USER_2_USERNAME,USER_2_EMAIL,false,AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: "/students/failedAnswers/" + failedAnswer.getId(),
                requestContentType: 'application/json'
        )
        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()))
        userRepository.deleteById(teacher.getId())
        failedAnswerRepository.findAll().size()==1

    }

    def "student can't get another student's failed answers from dashboard"() {
        given: 'a demo student'
        def student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        student1.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)
        when: 'the web service is invoked'
        response = restClient.delete(
                path: "/students/failedAnswers/" + failedAnswer.getId(),
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student1.getId()))
        userRepository.deleteById(student1.getId())
        failedAnswerRepository.findAll().size()==1
    }

    def cleanup(){
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()))
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }

}