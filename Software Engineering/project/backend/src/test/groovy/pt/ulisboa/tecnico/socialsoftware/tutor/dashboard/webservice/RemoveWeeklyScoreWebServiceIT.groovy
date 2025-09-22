package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveWeeklyScoreWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def courseExecution
    def weeklyScore

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
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().minusDays(30).with(weekSunday).toLocalDate();
        weeklyScore = new WeeklyScore(dashboard, week)
        weeklyScoreRepository.save(weeklyScore)
    }

    def "student removes weekly score"() {
        given: 'a student'
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/students/dashboards/weeklyScores/' + weeklyScore.getId() +'/Remove',
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        and:"has value"
        weeklyScoreRepository.findAll().size()==0
    }


    def "teacher can't get remove student's weekly score from dashboard"() {
        given: 'a teacher'
        def teacher= new Teacher(USER_2_NAME,USER_2_USERNAME,USER_2_EMAIL,false,AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/students/dashboards/weeklyScores/' + weeklyScore.getId() +'/Remove',
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(teacher.getId()))
        userRepository.deleteById(teacher.getId())

    }

    def "student can't get another student's weekly score from dashboard"() {
        given: 'a student'
        def student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        student2.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/students/dashboards/weeklyScores/' + weeklyScore.getId() +'/Remove',
                requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        externalCourseExecution.getUsers().remove(userRepository.findById(student2.getId()))
        userRepository.deleteById(student2.getId())


    }

    def cleanup(){
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
        externalCourseExecution.getUsers().remove(userRepository.findById(student.getId()))
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(externalCourseExecution.getId())
        courseRepository.deleteById(externalCourse.getId())
    }

}