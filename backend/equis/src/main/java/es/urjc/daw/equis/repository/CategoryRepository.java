package es.urjc.daw.equis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}