package opinnaytetyo.arviointikirja.domain;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface LessonGoalRepository extends CrudRepository<LessonGoal, Long>{

    @Modifying
    @Transactional
    @Query("DELETE FROM LessonGoal lg WHERE lg.lesson = :lesson")
    void deleteByLesson(Lesson lesson);

}
