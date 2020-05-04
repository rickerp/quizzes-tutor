package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "tournament_quizzes")
public class TournamentQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<TournamentQuestion> tournamentQuestions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval = true)
    private final Set<TournamentAnswer> tournamentAnswers = new HashSet<>();

    public TournamentQuiz() {}

    public TournamentQuiz(Tournament tournament) {
        setTournament(tournament);
    }

    public void generate() {
        /* TODO */
    }

    public Integer getId() { return id; }

    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Tournament getTournament() { return tournament; }

    public List<TournamentQuestion> getTournamentQuestions() { return tournamentQuestions; }

    public Set<TournamentAnswer> getTournamentAnswers() { return tournamentAnswers; }

    public void addTournamentQuestion(TournamentQuestion tournamentQuestion) { this.tournamentQuestions.add(tournamentQuestion); }

    public void addTournamentAnswer(TournamentAnswer tournamentAnswer) {
        if (!this.tournamentAnswers.add(tournamentAnswer)) {
            throw new TutorException(DUPLICATE_TOURNAMENT_ENROLL);
        }
        if (this.tournamentAnswers.size() == 2) { generate(); }
    }

    public boolean isGenerated() {
        return this.tournamentAnswers.size() > 1;
    }

    public TournamentAnswer getTournamentAnswer(int userId) {
        return tournamentAnswers.stream()
                .filter(tournamentAnswer -> tournamentAnswer.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }
}
