package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TOPICS_INSUFFICIENT_QUESTIONS;

@Entity
@Table(name = "tournament_quizzes")
public class TournamentQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<TournamentQuestion> tournamentQuestions = new ArrayList<>();

    public TournamentQuiz() {
    }

    public TournamentQuiz(Tournament tournament) {
        setTournament(tournament);
        generate();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
    
    public Tournament getTournament() {
        return tournament;
    }
    
    public List<TournamentQuestion> getTournamentQuestions() {
        return tournamentQuestions;
    }
    
    public void addTournamentQuestion(TournamentQuestion tournamentQuestion) {
        this.tournamentQuestions.add(tournamentQuestion);
    }
    
    public void generate() {
        List<List<Question>> topicsQuestions = getTournament().getTopics().stream()
                .map(topic -> new ArrayList<>(topic.getQuestions()))
                .collect(Collectors.toList());
        topicsQuestions.forEach(Collections::shuffle);
        Set<Question> questions = new HashSet<>();

        int qRem = getTournament().getNrQuestions();
        boolean hadQuestion;
        do {
            hadQuestion = false;
            Iterator<List<Question>> topicIt = topicsQuestions.iterator();
            while (qRem > 0 && topicIt.hasNext()) {
                List<Question> topicQuestions = topicIt.next();
                while (!topicQuestions.isEmpty()) {
                    if (questions.add(topicQuestions.remove(0))) {
                        qRem--;
                        hadQuestion = true;
                        break;
                    }
                }
                if (topicQuestions.isEmpty()) topicIt.remove();
            }
        } while (hadQuestion);

        if (qRem > 0) throw new TutorException(TOURNAMENT_TOPICS_INSUFFICIENT_QUESTIONS);
        for (Question question: questions) new TournamentQuestion(this, question);
    }
}
