package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "tournament_answers")
public class TournamentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    private Integer nrCorrectAnswers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournamentAnswer", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

    public TournamentAnswer() {}

    public TournamentAnswer(Tournament tournament, User user) {
        setTournament(tournament);
        setUser(user);
        tournament.addTournamentAnswer(this);
        user.addTournamentAnswer(this);
        createQuestionAnswers();
    }

    public void createQuestionAnswers() {

        if (!tournament.quizIsGenerated()) return;

        List<TournamentQuestion> tournamentQuestions = tournament.getQuiz().getTournamentQuestions();

        for (int seq = 0; seq < tournamentQuestions.size(); seq++) {
            new QuestionAnswer(this, tournamentQuestions.get(seq), seq);
        }
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) { this.user = user; }

    public Tournament getTournament() { return tournament; }

    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Set<QuestionAnswer> getQuestionAnswers() { return questionAnswers; }

    public void addQuestionAnswer(QuestionAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    public LocalDateTime getFinishTime() { return finishTime; }

    public boolean isFinished() { return finishTime != null; }

    public void finish() {
        this.finishTime = DateHandler.now();
        Integer nrAnswers = (int)questionAnswers.stream().filter(qA -> qA.getOption() != null).count();
        this.nrCorrectAnswers = (int)questionAnswers.stream().filter(QuestionAnswer::isCorrect).count();
        this.user.addNrTournamentQuestions(getTournament().getNrQuestions());
        this.user.addNrTournamentAnswers(nrAnswers);
        this.user.addNrTournamentCorrectAnswers(this.nrCorrectAnswers);
    }

    public Integer getNrCorrectAnswers() { return nrCorrectAnswers; }

    @Override
    public int hashCode() {
        return user.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TournamentAnswer &&
                user.getId().equals(((TournamentAnswer) obj).getUser().getId()) &&
                tournament.getId().equals(((TournamentAnswer) obj).getTournament().getId());
    }

    public void remove() {
        this.questionAnswers.forEach(QuestionAnswer::remove);
        this.user.getTournamentAnswers().remove(this);
        this.user = null;
    }
}
