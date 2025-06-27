package opinnaytetyo.arviointikirja.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TeachingGroupRepository extends CrudRepository<TeachingGroup, Long> {

    List<TeachingGroup> findByTeacherId(Long teacherId);
    List<TeachingGroup> findByTeacher(Teacher teacher);
}
