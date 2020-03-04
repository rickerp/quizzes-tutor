package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

import javax.management.relation.Role

class CreateStudentQuestionTest extends Specification {

    static final USERNAME = 'username'

    StudentQuestionService studentQuestionService
    User user

    def setup() {
        studentQuestionService = new StudentQuestionService();
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
    }

    def "create studentQuestion with no question"() {
        when: "no question exists"
        studentQuestionService.createStudentQuestion(USERNAME, null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUESTION_IS_EMPTY
    }

    def "createStudentQuestion" () {
        given: "a valid question"
        def questionDto = new QuestionDto()

        when: "a student question is created"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, questionDto);

        then: "the question contains correct information"
        studentQuestionDto != null
        studentQuestionDto.question != null
        studentQuestionDto.question.title == questionDto.title
        studentQuestionDto.question.image == questionDto.image
    }

}
