package opinnaytetyo.arviointikirja.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;


public interface StudentRepository extends CrudRepository<Student, Long> {

    List<Student> findByTeachingGroup(TeachingGroup teachingGroup);
    List<Student> findByTeachingGroupId(Long teachingGroupId);
    Student findByUsername(String username);
    //Optional<Student> findByUserId(Long userId);

}
