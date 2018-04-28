package com.sambatech.convertx.repository;

import com.sambatech.convertx.domain.Videos;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Videos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VideosRepository extends JpaRepository<Videos, Long> {

}
