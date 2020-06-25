package fr.esgi.secureupload.files.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, String> {

}
