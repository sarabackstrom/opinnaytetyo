package opinnaytetyo.arviointikirja.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface PerformanceRepository extends CrudRepository<Performance, Long>{

    Optional<Performance> findByStudentAndLessonAndUser(Student student, Lesson lesson, User user);
    List<Performance> findByStudent(Student student);
    List<Performance> findByUser(User user);
    Optional<Performance> findByLessonIdAndStudentIdAndUserId(Long lessonId, Long studentId, Long userId);

}
