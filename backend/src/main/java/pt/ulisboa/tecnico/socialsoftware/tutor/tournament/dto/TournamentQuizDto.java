package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TournamentQuizDto implements Serializable {

    private int tournamentId;
    private List<QuestionDto> questions;

    public TournamentQuizDto(TournamentQuiz quiz) {

        setTournamentId(quiz.getTournament().getId());
        questions = new ArrayList<>();
        List<TournamentQuestion> tournamentQuestions = quiz.getTournamentQuestions();

        for (int seq = 0; seq < tournamentQuestions.size(); seq++) {
            QuestionDto question = new QuestionDto(tournamentQuestions.get(seq).getQuestion());
            question.setSequence(seq);
            questions.add(question);
        }
    }

    public int getTournamentId() { return tournamentId; }

    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }
}
