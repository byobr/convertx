package com.sambatech.convertx.service;

import com.sambatech.convertx.domain.Videos;
import com.sambatech.convertx.repository.VideosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Videos.
 */
@Service
@Transactional
public class VideosService {

    private final Logger log = LoggerFactory.getLogger(VideosService.class);

    private final VideosRepository videosRepository;

    public VideosService(VideosRepository videosRepository) {
        this.videosRepository = videosRepository;
    }

    /**
     * Save a videos.
     *
     * @param videos the entity to save
     * @return the persisted entity
     */
    public Videos save(Videos videos) {
        log.debug("Request to save Videos : {}", videos);
        return videosRepository.save(videos);
    }

    /**
     * Get all the videos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Videos> findAll(Pageable pageable) {
        log.debug("Request to get all Videos");
        return videosRepository.findAll(pageable);
    }

    /**
     * Get one videos by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Videos findOne(Long id) {
        log.debug("Request to get Videos : {}", id);
        return videosRepository.findOne(id);
    }

    /**
     * Delete the videos by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Videos : {}", id);
        videosRepository.delete(id);
    }
}
