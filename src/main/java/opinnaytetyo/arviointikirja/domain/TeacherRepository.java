package opinnaytetyo.arviointikirja.domain;

import org.springframework.data.repository.CrudRepository;

public interface TeacherRepository extends CrudRepository<Teacher, Long>{
Teacher findByUsername(String username);
}
