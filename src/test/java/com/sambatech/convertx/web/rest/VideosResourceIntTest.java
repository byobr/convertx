package com.sambatech.convertx.web.rest;

import com.sambatech.convertx.ConvertXApp;

import com.sambatech.convertx.domain.Videos;
import com.sambatech.convertx.repository.VideosRepository;
import com.sambatech.convertx.service.VideosService;
import com.sambatech.convertx.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.sambatech.convertx.web.rest.TestUtil.sameInstant;
import static com.sambatech.convertx.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the VideosResource REST controller.
 *
 * @see VideosResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConvertXApp.class)
public class VideosResourceIntTest {

    private static final String DEFAULT_NOME_PASTA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_PASTA = "BBBBBBBBBB";

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_ARQUIVO = "AAAAAAAAAA";
    private static final String UPDATED_NOME_ARQUIVO = "BBBBBBBBBB";

    private static final String DEFAULT_EXTENSAO_ARQUIVO = "AAAA";
    private static final String UPDATED_EXTENSAO_ARQUIVO = "BBBB";

    private static final ZonedDateTime DEFAULT_DATA_ENVIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_ENVIADO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Long DEFAULT_VISUALIZACOES = 1L;
    private static final Long UPDATED_VISUALIZACOES = 2L;

    @Autowired
    private VideosRepository videosRepository;

    @Autowired
    private VideosService videosService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVideosMockMvc;

    private Videos videos;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VideosResource videosResource = new VideosResource(videosService);
        this.restVideosMockMvc = MockMvcBuilders.standaloneSetup(videosResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Videos createEntity(EntityManager em) {
        Videos videos = new Videos()
            .nomePasta(DEFAULT_NOME_PASTA)
            .titulo(DEFAULT_TITULO)
            .nomeArquivo(DEFAULT_NOME_ARQUIVO)
            .extensaoArquivo(DEFAULT_EXTENSAO_ARQUIVO)
            .dataEnviado(DEFAULT_DATA_ENVIADO)
            .visualizacoes(DEFAULT_VISUALIZACOES);
        return videos;
    }

    @Before
    public void initTest() {
        videos = createEntity(em);
    }

    @Test
    @Transactional
    public void createVideos() throws Exception {
        int databaseSizeBeforeCreate = videosRepository.findAll().size();

        // Create the Videos
        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isCreated());

        // Validate the Videos in the database
        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeCreate + 1);
        Videos testVideos = videosList.get(videosList.size() - 1);
        assertThat(testVideos.getNomePasta()).isEqualTo(DEFAULT_NOME_PASTA);
        assertThat(testVideos.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testVideos.getNomeArquivo()).isEqualTo(DEFAULT_NOME_ARQUIVO);
        assertThat(testVideos.getExtensaoArquivo()).isEqualTo(DEFAULT_EXTENSAO_ARQUIVO);
        assertThat(testVideos.getDataEnviado()).isEqualTo(DEFAULT_DATA_ENVIADO);
        assertThat(testVideos.getVisualizacoes()).isEqualTo(DEFAULT_VISUALIZACOES);
    }

    @Test
    @Transactional
    public void createVideosWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = videosRepository.findAll().size();

        // Create the Videos with an existing ID
        videos.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        // Validate the Videos in the database
        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNomePastaIsRequired() throws Exception {
        int databaseSizeBeforeTest = videosRepository.findAll().size();
        // set the field null
        videos.setNomePasta(null);

        // Create the Videos, which fails.

        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomeArquivoIsRequired() throws Exception {
        int databaseSizeBeforeTest = videosRepository.findAll().size();
        // set the field null
        videos.setNomeArquivo(null);

        // Create the Videos, which fails.

        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExtensaoArquivoIsRequired() throws Exception {
        int databaseSizeBeforeTest = videosRepository.findAll().size();
        // set the field null
        videos.setExtensaoArquivo(null);

        // Create the Videos, which fails.

        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDataEnviadoIsRequired() throws Exception {
        int databaseSizeBeforeTest = videosRepository.findAll().size();
        // set the field null
        videos.setDataEnviado(null);

        // Create the Videos, which fails.

        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVisualizacoesIsRequired() throws Exception {
        int databaseSizeBeforeTest = videosRepository.findAll().size();
        // set the field null
        videos.setVisualizacoes(null);

        // Create the Videos, which fails.

        restVideosMockMvc.perform(post("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isBadRequest());

        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVideos() throws Exception {
        // Initialize the database
        videosRepository.saveAndFlush(videos);

        // Get all the videosList
        restVideosMockMvc.perform(get("/api/videos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(videos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomePasta").value(hasItem(DEFAULT_NOME_PASTA.toString())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO.toString())))
            .andExpect(jsonPath("$.[*].nomeArquivo").value(hasItem(DEFAULT_NOME_ARQUIVO.toString())))
            .andExpect(jsonPath("$.[*].extensaoArquivo").value(hasItem(DEFAULT_EXTENSAO_ARQUIVO.toString())))
            .andExpect(jsonPath("$.[*].dataEnviado").value(hasItem(sameInstant(DEFAULT_DATA_ENVIADO))))
            .andExpect(jsonPath("$.[*].visualizacoes").value(hasItem(DEFAULT_VISUALIZACOES.intValue())));
    }

    @Test
    @Transactional
    public void getVideos() throws Exception {
        // Initialize the database
        videosRepository.saveAndFlush(videos);

        // Get the videos
        restVideosMockMvc.perform(get("/api/videos/{id}", videos.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(videos.getId().intValue()))
            .andExpect(jsonPath("$.nomePasta").value(DEFAULT_NOME_PASTA.toString()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO.toString()))
            .andExpect(jsonPath("$.nomeArquivo").value(DEFAULT_NOME_ARQUIVO.toString()))
            .andExpect(jsonPath("$.extensaoArquivo").value(DEFAULT_EXTENSAO_ARQUIVO.toString()))
            .andExpect(jsonPath("$.dataEnviado").value(sameInstant(DEFAULT_DATA_ENVIADO)))
            .andExpect(jsonPath("$.visualizacoes").value(DEFAULT_VISUALIZACOES.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingVideos() throws Exception {
        // Get the videos
        restVideosMockMvc.perform(get("/api/videos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVideos() throws Exception {
        // Initialize the database
        videosService.save(videos);

        int databaseSizeBeforeUpdate = videosRepository.findAll().size();

        // Update the videos
        Videos updatedVideos = videosRepository.findOne(videos.getId());
        // Disconnect from session so that the updates on updatedVideos are not directly saved in db
        em.detach(updatedVideos);
        updatedVideos
            .nomePasta(UPDATED_NOME_PASTA)
            .titulo(UPDATED_TITULO)
            .nomeArquivo(UPDATED_NOME_ARQUIVO)
            .extensaoArquivo(UPDATED_EXTENSAO_ARQUIVO)
            .dataEnviado(UPDATED_DATA_ENVIADO)
            .visualizacoes(UPDATED_VISUALIZACOES);

        restVideosMockMvc.perform(put("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVideos)))
            .andExpect(status().isOk());

        // Validate the Videos in the database
        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeUpdate);
        Videos testVideos = videosList.get(videosList.size() - 1);
        assertThat(testVideos.getNomePasta()).isEqualTo(UPDATED_NOME_PASTA);
        assertThat(testVideos.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testVideos.getNomeArquivo()).isEqualTo(UPDATED_NOME_ARQUIVO);
        assertThat(testVideos.getExtensaoArquivo()).isEqualTo(UPDATED_EXTENSAO_ARQUIVO);
        assertThat(testVideos.getDataEnviado()).isEqualTo(UPDATED_DATA_ENVIADO);
        assertThat(testVideos.getVisualizacoes()).isEqualTo(UPDATED_VISUALIZACOES);
    }

    @Test
    @Transactional
    public void updateNonExistingVideos() throws Exception {
        int databaseSizeBeforeUpdate = videosRepository.findAll().size();

        // Create the Videos

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVideosMockMvc.perform(put("/api/videos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(videos)))
            .andExpect(status().isCreated());

        // Validate the Videos in the database
        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteVideos() throws Exception {
        // Initialize the database
        videosService.save(videos);

        int databaseSizeBeforeDelete = videosRepository.findAll().size();

        // Get the videos
        restVideosMockMvc.perform(delete("/api/videos/{id}", videos.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Videos> videosList = videosRepository.findAll();
        assertThat(videosList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Videos.class);
        Videos videos1 = new Videos();
        videos1.setId(1L);
        Videos videos2 = new Videos();
        videos2.setId(videos1.getId());
        assertThat(videos1).isEqualTo(videos2);
        videos2.setId(2L);
        assertThat(videos1).isNotEqualTo(videos2);
        videos1.setId(null);
        assertThat(videos1).isNotEqualTo(videos2);
    }
}
