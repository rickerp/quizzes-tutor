package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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

    public TournamentQuiz() {}

    public TournamentQuiz(Tournament tournament) {
        setTournament(tournament);
        generate();
    }

    public void generate() {
        /* TODO */
    }

    public Integer getId() { return id; }

    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Tournament getTournament() { return tournament; }

    public List<TournamentQuestion> getTournamentQuestions() { return tournamentQuestions; }

    public void addTournamentQuestion(TournamentQuestion tournamentQuestion) { this.tournamentQuestions.add(tournamentQuestion); }
}
