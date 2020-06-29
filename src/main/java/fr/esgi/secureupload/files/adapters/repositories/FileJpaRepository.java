package fr.esgi.secureupload.files.adapters.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.DoubleStream;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, String> {

    @Query(value = "select f from File f where f.owner = :id")
    Page<FileJpaEntity> findAllByUser(@Param(value="id")String id, Pageable pageable);
}
