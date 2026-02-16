package es.urjc.daw.equis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    List<User> findByRoles(String roles);

    Page<User> findAll(Pageable pageable);
}
