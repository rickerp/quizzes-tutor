package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Integer> {

    @Query(value = "SELECT * FROM student_questions WHERE student_id = :studentId", nativeQuery = true)
    List<StudentQuestion> getStudentQuestions(int studentId);


    @Query(value = "SELECT * FROM student_questions s JOIN questions q ON s.question_id = q.id JOIN courses c ON q.course_id = c.id WHERE c.id = :courseId", nativeQuery = true)
    List<StudentQuestion> getStudentQuestionsByCourse(int courseId);

}