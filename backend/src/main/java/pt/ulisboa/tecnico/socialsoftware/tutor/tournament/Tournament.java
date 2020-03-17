package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "tournaments")
    private Set<User> players = new HashSet<>();

    @ManyToMany
    private Set<Topic> topics;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @Column(name = "nr_questions")
    private Integer nrQuestions;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    public Tournament() {}

    public Tournament(User creator, Set<Topic> topics, CourseExecution courseExecution, Integer nrQuestions, LocalDateTime startTime, LocalDateTime endTime) {
        setCreator(creator);
        setTopics(topics);
        setCourseExecution(courseExecution);
        setNrQuestions(nrQuestions);
        setStartTime(startTime);
        setEndTime(endTime);

        if (startTime == null || startTime.compareTo(LocalDateTime.now()) <= 0)
            throw new TutorException(TOURNAMENT_START_TIME_INVALID, startTime == null ? "" : startTime.toString());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public boolean isOpened() { return startTime.compareTo(LocalDateTime.now()) > 0; }

    public void playerEnroll(User player) {
        if (player == null || player.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_NOT_STUDENT, player == null ? -1 : player.getId());

        if (!isOpened()) throw new TutorException(TOURNAMENT_NOT_OPENED);
        if (!players.add(player)) throw new TutorException(DUPLICATE_TOURNAMENT_ENROLL);

        player.getTournaments().add(this);
    }

    public Set<User> getPlayers() { return this.players; }

    public Set<Topic> getTopics() { return this.topics; }

    public void setTopics(Set<Topic> topics) {
        if (topics == null || topics.isEmpty()) throw new TutorException(TOPIC_NOT_FOUND, -1);
        for (Topic topic : topics) {
            if (topic.getStatus() != Topic.Status.AVAILABLE)
                throw new TutorException(TOPIC_NOT_AVAILABLE, topic.getId());
        }
        this.topics = topics;
    }


    public CourseExecution getCourseExecution() { return this.courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) {
        if (courseExecution == null) throw new TutorException(COURSE_EXECUTION_NOT_FOUND, -1);
        if (courseExecution.getStatus() != CourseExecution.Status.ACTIVE)
            throw new TutorException(COURSE_EXECUTION_NOT_ACTIVE, courseExecution.getId());
        this.courseExecution = courseExecution;
    }

    public User getCreator() { return creator; }

    public void setCreator(User creator) {
        if (creator == null || creator.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_NOT_STUDENT, creator == null ? -1 : creator.getId());
        this.creator = creator;
    }

    public Integer getNrQuestions() {
        return nrQuestions;
    }

    public void setNrQuestions(Integer nrQuestions) {
        if (nrQuestions == null || nrQuestions < 1) throw new TutorException(TOURNAMENT_NR_QUESTIONS_INVALID);
        this.nrQuestions = nrQuestions;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) throw new TutorException(TOURNAMENT_START_TIME_INVALID, "");
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null || endTime.compareTo(getStartTime()) <= 0)
            throw new TutorException(TOURNAMENT_END_TIME_INVALID, endTime == null ? "" : endTime.toString());
        this.endTime = endTime;
    }
}
