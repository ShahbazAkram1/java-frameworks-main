package com.example.demo.repositories;

import com.example.demo.domain.InhousePart;
import com.example.demo.domain.Part;
import com.example.demo.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 *
 *
 *
 */
public interface PartRepository extends CrudRepository <Part,Long> {
    @Query("SELECT p FROM Part p WHERE p.name LIKE %?1%")
    public List<Part> search(String keyword);
    Optional<Part> findByName(String name);

    // Custom query to find a part by name while excluding a specific ID
    @Query("SELECT p FROM Part p WHERE p.name = :name AND p.id <> :id")
    Optional<Part> findByNameAndIdNot(@Param("name") String name, @Param("id") Long id);


}