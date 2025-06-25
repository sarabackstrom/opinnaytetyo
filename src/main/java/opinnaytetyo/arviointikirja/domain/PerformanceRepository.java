package opinnaytetyo.arviointikirja.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PerformanceRepository extends CrudRepository<Performance, Long>{

    Optional<Performance> findByStudentAndLesson(Student student, Lesson lesson);

}
