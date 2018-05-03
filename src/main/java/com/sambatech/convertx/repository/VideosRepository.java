package com.sambatech.convertx.repository;

import com.sambatech.convertx.domain.Videos;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Videos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VideosRepository extends JpaRepository<Videos, Long> {
	
	Videos findByStatus(String status);
	Videos findByidEncoder(Long id);
	Page<Videos> findByUsuario(Long id, Pageable pageable);
	Videos findById(Long id);

}
