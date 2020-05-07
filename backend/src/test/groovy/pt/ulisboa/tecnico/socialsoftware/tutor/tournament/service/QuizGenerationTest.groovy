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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuiz
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TOPICS_INSUFFICIENT_QUESTIONS
import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

import java.time.LocalDateTime

@DataJpaTest
class QuizGenerationTest extends Specification {
    public static final String T_NAME = "Demo-Tournament"
    public static final Integer NR_QUESTIONS = 20 // Has to be Pair
    public static final String NAME = "Name"
    public static final Integer KEY = 1
    public static final String USERNAME = "Username_" + KEY
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

    def topics
    def questions
    def tournament

    def setup() {
        "Create a Course"
        Course course = new Course(NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        "Create a #NR_QUESTIONS Questions"
        questions = new HashSet<Question>()
        1.upto(NR_QUESTIONS, {
            Option option = new Option()
            option.setSequence(KEY + it)
            option.setCorrect(true)
            option.setContent(NAME + it)
            optionRepository.save(option)

            Question question = new Question()
            question.setKey(KEY + it)
            question.setCourse(course)
            question.setTitle(NAME + it)
            questionRepository.save(question)
            option.setQuestion(question)

            questions.add(question)
        })

        "Create Opened Tournament"
        CourseExecution courseExecution = new CourseExecution(course, NAME, NAME, Course.Type.TECNICO)
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)

        User creator = new User(NAME, USERNAME, KEY, STUDENT)
        creator.addCourse(courseExecution)
        userRepository.save(creator)

        topics = new HashSet<Topic>()
        1.upto(NR_QUESTIONS/2, {
            Topic topic = new Topic()
            topic.setName(NAME + it)
            topic.setCourse(course)
            topicRepository.save(topic)
            topics.add(topic)
        })

        tournament = new Tournament()
        tournament.setName(T_NAME)
        tournament.setCreator(creator)
        tournament.setTopics(topics)
        tournament.setNrQuestions(NR_QUESTIONS)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartTime(NOW.plusMinutes(10))
        tournament.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(tournament)
    }

    def "Create a Tournament Quiz"() {

        given: "Associated Topic has enough Questions"
        1.upto(NR_QUESTIONS/2, {
            ((Question[])questions.toArray())[2*it-2].addTopic(topics.toArray()[it-1])
            ((Question[])questions.toArray())[2*it-1].addTopic(topics.toArray()[it-1])
        })

        when:
        tournamentService.generateQuiz(tournament.getId())

        then: "Check if created correctly"
        TournamentQuiz quiz = tournament.getQuiz()
        quiz != null
        quiz.getId() != null
        quiz.getTournament() == tournament

        List<TournamentQuestion> recQuestions = quiz.getTournamentQuestions()
        recQuestions.size() == NR_QUESTIONS
        int[] nrQuestionsPerTopic = new int[NR_QUESTIONS]
        recQuestions.each {rQ ->
            rQ.getQuestion().getTopics().each { t ->
                nrQuestionsPerTopic[topics.toArray().findIndexOf { it == t }]++
            }
        }
        nrQuestionsPerTopic.each { nQ -> nQ >= 2 }
    }

    def "Generate a Tournament Quiz whose Topics don't have enough Questions"() {

        given: "Only half the Questions that need to"
        1.upto(NR_QUESTIONS/2, {
            ((Question[])questions.toArray())[it-1].addTopic(topics.toArray()[it-1])
        })

        when:
        tournamentService.generateQuiz(tournament.getId())

        then: "Check exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_TOPICS_INSUFFICIENT_QUESTIONS
    }

    def "Generate a Tournament Quiz whose Topics have no Questions"() {

        when:
        tournamentService.generateQuiz(tournament.getId())

        then: "Check exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_TOPICS_INSUFFICIENT_QUESTIONS
    }

    @TestConfiguration
    static class QuizAnsweringConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
