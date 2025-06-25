package opinnaytetyo.arviointikirja.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface StudentRepository extends CrudRepository<Student, Long> {

    List<Student> findByTeachingGroup(TeachingGroup teachingGroup);
    List<Student> findByTeachingGroupId(Long teachingGroupId);

}
