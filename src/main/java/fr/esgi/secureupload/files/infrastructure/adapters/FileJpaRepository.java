package fr.esgi.secureupload.files.infrastructure.adapters;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, String> {

    @Query(value = "select f from File f where f.owner.id = :id")
    Page<FileJpaEntity> findAllByUser(@Param(value="id") String id, Pageable pageable);
}
