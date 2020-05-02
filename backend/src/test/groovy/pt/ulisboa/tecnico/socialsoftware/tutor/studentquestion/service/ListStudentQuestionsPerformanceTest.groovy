package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.image.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.image.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class ListStudentQuestionsPerformanceTest extends Specification{
    static final USERNAME = 'username'

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ImageRepository imageRepository

    User user

    Course course

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "performance testing to list 1000 student questions"() {
        given: "1000 student questions"
        1.upto(1, {
            "Create 1000 questions"
            def question = new Question()
            question.setTitle("QuestionTitle")
            question.setKey(it as Integer)
            question.setContent('Question')
            question.setStatus(Question.Status.AVAILABLE)
            question.setNumberOfAnswers(2)
            question.setNumberOfCorrect(1)
            question.setCourse(course)
            and: "an image"
            def image = new Image()
            image.setUrl('URL')
            image.setWidth(20)
            imageRepository.save(image)
            question.setImage(image)
            and: "two options"
            def optionOK = new Option()
            optionOK.setSequence(1)
            optionOK.setContent('Option1')
            optionOK.setCorrect(true)
            optionOK.setQuestion(question)
            optionRepository.save(optionOK)
            question.addOption(optionOK)
            def optionKO = new Option()
            optionKO.setSequence(2)
            optionKO.setContent('Option2')
            optionKO.setCorrect(false)
            optionKO.setQuestion(question)
            optionRepository.save(optionKO)
            question.addOption(optionKO)
            questionRepository.save(question)

            studentQuestionRepository.save(new StudentQuestion(user, question))
        })

        when: "list 1000 times 1000 student questions"
        1.upto(1, { studentQuestionService.list(course.getId(), user.getId())})

        then:
        true
    }

    @TestConfiguration
    static class ListStudentQuestionsPerformanceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService();
        }

        @Bean
        UserService userService() {
            return new UserService();
        }

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
