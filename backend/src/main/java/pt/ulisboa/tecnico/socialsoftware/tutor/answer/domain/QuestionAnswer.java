package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_SEQUENCE_FOR_QUESTION_ANSWER;

@Entity
@Table(name = "question_answers")
public class QuestionAnswer implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "time_taken")
    private Integer timeTaken;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestion quizQuestion;

    @ManyToOne
    @JoinColumn(name = "quiz_answer_id")
    private QuizAnswer quizAnswer;

    @ManyToOne
    @JoinColumn(name = "tournament_question_id")
    private TournamentQuestion tournamentQuestion;

    @ManyToOne
    @JoinColumn(name = "tournament_answer_id")
    private TournamentAnswer tournamentAnswer;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    private Integer sequence;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionAnswer",fetch = FetchType.LAZY)
    private Set<ClarificationRequest> clarificationRequests = new HashSet<>();

    public QuestionAnswer() {
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, Integer timeTaken, Option option, int sequence) {
        setTimeTaken(timeTaken);
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setOption(option);
        setSequence(sequence);
    }

    public QuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, int sequence) {
        setQuizAnswer(quizAnswer);
        setQuizQuestion(quizQuestion);
        setSequence(sequence);
    }

    public QuestionAnswer(TournamentAnswer tournamentAnswer, TournamentQuestion tournamentQuestion, int sequence) {
        setTournamentAnswer(tournamentAnswer);
        setTournamentQuestion(tournamentQuestion);
        setSequence(sequence);
    }

    public boolean isFromTournament() { return tournamentQuestion != null && tournamentAnswer != null; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionAnswer(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Question getQuestion() {
        return isFromTournament() ?
                tournamentQuestion.getQuestion() :
                quizQuestion.getQuestion();
    }

    public CourseExecution getCourseExecution() {
        return isFromTournament() ?
                tournamentQuestion.getQuiz().getTournament().getCourseExecution() :
                quizQuestion.getQuiz().getCourseExecution();
    }

    public User getUser() {
        return isFromTournament() ?
                tournamentAnswer.getUser() :
                quizAnswer.getUser();
    }

    public boolean isCompleted() {
        return isFromTournament() ?
                tournamentAnswer.isFinished() :
                quizAnswer.isCompleted();
    }

    public QuizQuestion getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
        quizQuestion.addQuestionAnswer(this);
    }

    public QuizAnswer getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswer = quizAnswer;
        quizAnswer.addQuestionAnswer(this);
    }

    public TournamentQuestion getTournamentQuestion() {
        return tournamentQuestion;
    }

    public void setTournamentQuestion(TournamentQuestion tournamentQuestion) {
        this.tournamentQuestion = tournamentQuestion;
        tournamentQuestion.addQuestionAnswer(this);
    }

    public TournamentAnswer getTournamentAnswer() {
        return tournamentAnswer;
    }

    public void setTournamentAnswer(TournamentAnswer tournamentAnswer) {
        this.tournamentAnswer = tournamentAnswer;
        tournamentAnswer.addQuestionAnswer(this);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_QUESTION_ANSWER);

        this.sequence = sequence;
    }

    public Set<ClarificationRequest> getClarificationRequests() { return this.clarificationRequests; }

    public void setClarificationRequests(Set<ClarificationRequest> clarificationRequests) { this.clarificationRequests = clarificationRequests; }

    public void addClarification(ClarificationRequest clarificationRequest) { this.clarificationRequests.add(clarificationRequest); }

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "id=" + id +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                '}';
    }

    public boolean isCorrect() {
        return getOption() != null && getOption().getCorrect();
    }

    public void remove() {
        if (!isFromTournament()) {
            quizAnswer.getQuestionAnswers().remove(this);
            quizAnswer = null;

            quizQuestion.getQuestionAnswers().remove(this);
            quizQuestion = null;
        }
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }
}
