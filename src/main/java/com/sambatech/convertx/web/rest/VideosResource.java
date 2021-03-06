package com.sambatech.convertx.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sambatech.convertx.domain.Videos;
import com.sambatech.convertx.service.VideosService;
import com.sambatech.convertx.web.rest.errors.BadRequestAlertException;
import com.sambatech.convertx.web.rest.util.HeaderUtil;
import com.sambatech.convertx.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Videos.
 */
@RestController
@RequestMapping("/api")
public class VideosResource {

	private final Logger log = LoggerFactory.getLogger(VideosResource.class);

	private static final String ENTITY_NAME = "videos";

	private final VideosService videosService;

	public VideosResource(VideosService videosService) {
		this.videosService = videosService;
	}

	/**
	 * POST /videos : Create a new videos.
	 *
	 * @param videos
	 *            the videos to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         videos, or with status 400 (Bad Request) if the videos has already an
	 *         ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PostMapping("/videos")
	@Timed
	public ResponseEntity<Videos> createVideos(@Valid @RequestBody Videos videos) throws URISyntaxException {
		log.debug("REST request to save Videos : {}", videos);
		if (videos.getId() != null) {
			throw new BadRequestAlertException("A new videos cannot already have an ID", ENTITY_NAME, "idexists");
		}
		Videos result = videosService.save(videos);
		return ResponseEntity.created(new URI("/api/videos/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
	}

	@PostMapping("/videos/adicionar")
	public String addVideo(@RequestParam("file") MultipartFile file, @RequestParam("user") String usuario)
			throws IOException {

		// Converte arquivo para formato File
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		videosService.enviarVideo(convFile, usuario);

		return "<script>alert('Vídeo enviado com sucesso! Favor acompanhar o processo na página de vídeos!');location.href = '../../#/';</script>";
	}

	/**
	 * PUT /videos : Updates an existing videos.
	 *
	 * @param videos
	 *            the videos to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         videos, or with status 400 (Bad Request) if the videos is not valid,
	 *         or with status 500 (Internal Server Error) if the videos couldn't be
	 *         updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PutMapping("/videos")
	@Timed
	public ResponseEntity<Videos> updateVideos(@Valid @RequestBody Videos videos) throws URISyntaxException {
		log.debug("REST request to update Videos : {}", videos);
		if (videos.getId() == null) {
			return createVideos(videos);
		}
		Videos result = videosService.save(videos);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, videos.getId().toString()))
				.body(result);
	}

	/**
	 * GET /videos : get all the videos.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of videos in
	 *         body
	 */
	@GetMapping("/videos")
	@Timed
	public ResponseEntity<List<Videos>> getAllVideos(Pageable pageable, String user) {
		log.debug("REST request to get a page of Videos");
		Page<Videos> page;
		if (user.equals("admin")) {
			page = videosService.findAll(pageable);
		} else {
			page = videosService.listarPorUsuario(user, pageable);
		}
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/videos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /videos/:id : get the "id" videos.
	 *
	 * @param id
	 *            the id of the videos to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the videos, or
	 *         with status 404 (Not Found)
	 */
	@GetMapping("/videos/{id}")
	@Timed
	public ResponseEntity<Videos> getVideos(@PathVariable Long id) {
		log.debug("REST request to get Videos : {}", id);
		Videos videos = videosService.findOne(id);
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(videos));
	}

	@GetMapping("/videos/play/{id}")
	@Timed
	public HttpEntity<byte[]> playVideo(@PathVariable Long id) throws IOException {
		log.debug("REST request to get Videos : {}", id);
		byte[] image = videosService.recuperarVideo(id);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentLength(image.length);

		return new HttpEntity<byte[]>(image, headers);
	}

	/**
	 * DELETE /videos/:id : delete the "id" videos.
	 *
	 * @param id
	 *            the id of the videos to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/videos/{id}")
	@Timed
	public ResponseEntity<Void> deleteVideos(@PathVariable Long id) {
		log.debug("REST request to delete Videos : {}", id);
		videosService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}

	@PostMapping("/videos/callback")
	@Timed
	public boolean callback(@RequestBody String retorno) {
		try {
			JSONObject r = new JSONObject(retorno);
			Long job_id = Long.parseLong(r.getJSONArray("outputs").getJSONObject(0).get("id").toString());
			String status = r.getJSONArray("outputs").getJSONObject(0).get("state").toString();
			String url = r.getJSONArray("outputs").getJSONObject(0).get("url").toString();
			videosService.retorno_video(job_id, status, url);
			log.debug("Status recebido: ", r);
			return true;
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return false;
		}

	}
}
