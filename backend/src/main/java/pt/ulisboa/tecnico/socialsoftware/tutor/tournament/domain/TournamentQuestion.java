package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "tournament_questions")
public class TournamentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private TournamentQuiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quizQuestion", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    public TournamentQuestion() {}

    public TournamentQuestion(TournamentQuiz quiz, Question question) {
        setQuiz(quiz);
        setQuestion(question);
    }

    public Integer getId() {
        return id;
    }

    public TournamentQuiz getQuiz() {
        return quiz;
    }

    public void setQuiz(TournamentQuiz quiz) {
        this.quiz = quiz;
        quiz.addTournamentQuestion(this);
    }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) {
        this.question = question;
        question.addTournamentQuestion(this);
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    public boolean isQuestionOption(Option option) {
        return question.getOptions().stream().anyMatch(opt -> opt.getId().equals(option.getId()));
    }

    public void remove() {
        this.questionAnswers.forEach(QuestionAnswer::remove);
        this.question.getTournamentQuestions().remove(this);
        this.question = null;
    }
}
