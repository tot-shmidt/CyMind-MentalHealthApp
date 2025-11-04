package cymind.repository;

import cymind.model.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    ChatGroup findById(long id);

    @Query("SELECT c FROM ChatGroup c JOIN c.students s JOIN s.abstractUser a1 JOIN c.professionals p JOIN p.abstractUser a2 WHERE a1.id = :id OR a2.id  = :id")
    List<ChatGroup> findAllByUserId(Long id);
}
