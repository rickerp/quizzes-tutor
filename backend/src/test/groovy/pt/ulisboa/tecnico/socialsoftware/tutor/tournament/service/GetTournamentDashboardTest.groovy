package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDashboardDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class GetTournamentDashboardTest extends Specification {
    public static final String T_NAME = "Demo-Tournament"
    public static final String NAME = "Name"
    public static final Integer KEY_1 = 1
    public static final Integer KEY_2 = 2
    public static final String USERNAME_1 = "Username_" + KEY_1
    public static final String USERNAME_2 = "Username_" + KEY_2
    public static final LocalDateTime NOW = DateHandler.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentService tournamentService

    def creator
    def user
    def course
    def courseExecution
    def topic
    def topicSet
    def question
    def option
    def tournament

    def setup() {
        "Create a Course"
        course = new Course(NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        "Create a Course Execution"
        courseExecution = new CourseExecution(course, NAME, NAME, Course.Type.TECNICO)
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)
        "Create a User Creator"
        creator = new User(NAME, USERNAME_1, KEY_1, STUDENT)
        creator.addCourse(courseExecution)
        userRepository.save(creator)
        "Create a User"
        user = new User(NAME, USERNAME_2, KEY_2, STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        "Create an Option"
        option = new Option()
        option.setSequence(KEY_1)
        option.setCorrect(true)
        option.setContent(NAME)
        optionRepository.save(option)
        "Create a Question"
        question = new Question()
        question.setKey(KEY_1)
        question.setCourse(course)
        question.setTitle(NAME)
        questionRepository.save(question)
        option.setQuestion(question)
        "Create a Topic"
        topic = new Topic()
        topic.setName(NAME)
        topic.setCourse(course)
        topicRepository.save(topic)
        question.addTopic(topic)
        topicSet = new HashSet<Topic>()
        topicSet.add(topic)
        "Create Opened Tournament"
        tournament = new Tournament()
        tournament.setName(T_NAME + KEY_1)
        tournament.setCreator(creator)
        tournament.setTopics(topicSet)
        tournament.setNrQuestions(1)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartTime(NOW.plusMinutes(10))
        tournament.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(tournament)
        "Enroll Users in Opened Tournament"
        tournamentService.enroll(creator.getId(), tournament.getId())
        tournamentService.enroll(user.getId(), tournament.getId())
    }

    def "Check Dashboard Without Finish any Tournament"() {

        when: "Get Dashboard Information"
        TournamentDashboardDto dashDto = tournamentService.getTournamentDashboard(user.getId(), courseExecution.getId())

        then: "Check Dashboard Information"
        !dashDto.getIsPublic()
        dashDto.getNrQuestions() == 0
        dashDto.getNrAnswers() == 0
        dashDto.getNrCorrectAnswers() == 0
        dashDto.getTournamentScores().size() == 0
    }

    def "Solve a Tournament Quiz and Check Dashboard"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))
        and: "Answer a Tournament"
        TournamentQuizDto quizDto = tournamentService.beginQuiz(tournament.getId(), user.getId())
        StatementAnswerDto answer = quizDto.getAnswers().get(0)
        answer.setOptionId(option.getId())
        tournamentService.selectQuestionOption(tournament.getId(), user.getId(), answer)
        tournamentService.finishQuiz(tournament.getId(), user.getId())

        when: "Get Dashboard Information"
        TournamentDashboardDto dashDto = tournamentService.getTournamentDashboard(user.getId(), courseExecution.getId())

        then: "Check Dashboard Information"
        !dashDto.getIsPublic()
        dashDto.getNrQuestions() == 1
        dashDto.getNrAnswers() == 1
        dashDto.getNrCorrectAnswers() == 1
        List<TournamentAnswerDto> answers = dashDto.getTournamentScores()
        answers.size() == 1
        answers.get(0).getNrQuestions() == 1
        answers.get(0).getNrCorrectAnswers() == 1
        answers.get(0).getTournamentId() == tournament.getId()
        answers.get(0).getTournamentName() == tournament.getName()
        answers.get(0).getTopicsName().size() == 1
        answers.get(0).getTopicsName()[0] == topic.getName()
    }


    def "Solve Two Tournament Quizzes and Check Dashboard (Scores Order)"() {

        given: "Create Another Opened Tournament"
        Tournament tournament_2 = new Tournament()
        tournament_2.setName(T_NAME + KEY_2)
        tournament_2.setCreator(user)
        tournament_2.setTopics(topicSet)
        tournament_2.setNrQuestions(1)
        tournament_2.setCourseExecution(courseExecution)
        tournament_2.setStartTime(NOW.plusMinutes(10))
        tournament_2.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(tournament_2)
        and: "Enroll Users in Opened Tournament"
        tournamentService.enroll(creator.getId(), tournament_2.getId())
        tournamentService.enroll(user.getId(), tournament_2.getId())
        and: "Opened Tournaments -> In Progress Tournaments"
        tournament.setStartTime(NOW.plusMinutes(-10))
        tournament_2.setStartTime(NOW.plusMinutes(-10))
        and: "Answer a Tournament"
        TournamentQuizDto quizDto = tournamentService.beginQuiz(tournament.getId(), user.getId())
        StatementAnswerDto answer = quizDto.getAnswers().get(0)
        answer.setOptionId(option.getId())
        tournamentService.selectQuestionOption(tournament.getId(), user.getId(), answer)
        tournamentService.finishQuiz(tournament.getId(), user.getId())
        and: "Answer another Tournament"
        quizDto = tournamentService.beginQuiz(tournament_2.getId(), user.getId())
        answer = quizDto.getAnswers().get(0)
        answer.setOptionId(option.getId())
        tournamentService.selectQuestionOption(tournament_2.getId(), user.getId(), answer)
        tournamentService.finishQuiz(tournament_2.getId(), user.getId())

        when: "Get Dashboard Information"
        TournamentDashboardDto dashDto = tournamentService.getTournamentDashboard(user.getId(), courseExecution.getId())

        then: "Check Dashboard Information"
        !dashDto.getIsPublic()
        dashDto.getNrQuestions() == 2
        dashDto.getNrAnswers() == 2
        dashDto.getNrCorrectAnswers() == 2
        List<TournamentAnswerDto> answers = dashDto.getTournamentScores()
        answers.size() == 2
        answers.get(0).getNrQuestions() == 1
        answers.get(0).getNrCorrectAnswers() == 1
        answers.get(0).getTournamentId() == tournament_2.getId()
        answers.get(0).getTournamentName() == tournament_2.getName()
        answers.get(0).getTopicsName().size() == 1
        answers.get(0).getTopicsName()[0] == topic.getName()
        answers.get(1).getNrQuestions() == 1
        answers.get(1).getNrCorrectAnswers() == 1
        answers.get(1).getTournamentId() == tournament.getId()
        answers.get(1).getTournamentName() == tournament.getName()
        answers.get(1).getTopicsName().size() == 1
        answers.get(1).getTopicsName()[0] == topic.getName()
    }

    @TestConfiguration
    static class GetTournamentDashboardConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
