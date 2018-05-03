package com.sambatech.convertx.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.sambatech.convertx.domain.User;
import com.sambatech.convertx.domain.Videos;
import com.sambatech.convertx.repository.UserRepository;
import com.sambatech.convertx.repository.VideosRepository;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

	// Configurações estáticas do Amazon S3
	private static String bucketName = "convert-x";
	private static String usuario_s3 = "AKIAJSXBNT2CA74IWB4Q";
	private static String senha_s3 = "Jn8qBLoxEzSWOQacgo5H4zFWQwmSLaXy50bCg2gj";
	private static String regiao_s3 = "us-east-1";

	private final Logger log = LoggerFactory.getLogger(VideosService.class);

	private final VideosRepository videosRepository;
	private final UserRepository userRepository;

	public VideosService(VideosRepository videosRepository, UserRepository userRepository) {
		this.videosRepository = videosRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Save a videos.
	 *
	 * @param videos
	 *            the entity to save
	 * @return the persisted entity
	 */
	public Videos save(Videos videos) {
		log.debug("Request to save Videos : {}", videos);
		return videosRepository.save(videos);
	}

	/**
	 * Get all the videos.
	 *
	 * @param pageable
	 *            the pagination information
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
	 * @param id
	 *            the id of the entity
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
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete Videos : {}", id);
		videosRepository.delete(id);
	}

	public boolean enviarVideo(File arquivo, String user) {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(usuario_s3, senha_s3);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(regiao_s3)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			File file = arquivo;
			String nome_chave = converterHex(arquivo.getName() + System.currentTimeMillis());
			String keyName = "videos/" + nome_chave + "/" + arquivo.getPath();
			s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
			Videos add = new Videos();
			add.setChave(nome_chave);
			add.setDataEnviado(ZonedDateTime.now());
			String[] Arquivo_quebrado = arquivo.getName().split(Pattern.quote("."));
			add.setExtensaoArquivo(Arquivo_quebrado[1]);
			add.setNomeArquivo(Arquivo_quebrado[0]);
			add.setNomePasta(keyName);
			add.setTitulo("teste");
			add.setUsuario(userRepository.findByLogin(user).getId());
			add.setVisualizacoes((long) 0);
			add.setStatus("Enviando");
			videosRepository.saveAndFlush(add);
			// Converte vídeo em um novo thread
			Thread t = new Thread(new Runnable() {
				public void run() {
					Runnable exec = new processarVideo(add);
					exec.run();
				}
			});
			t.start();

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			return false;
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			return false;
		}
		return true;
	}

	public String converterHex(String nome) {
		String hash;
		try {
			hash = DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(nome.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			// Retorna MS em caso de falha
			return "" + System.currentTimeMillis() + "";
		} catch (UnsupportedEncodingException e) {
			// Retorna MS em caso de falha
			return "" + System.currentTimeMillis() + "";
		}
		return hash;
	}

	public class processarVideo implements Runnable {

		private Videos video;

		public processarVideo(Videos video) {
			this.video = video;
		}

		public void run() {
			HttpClient http = HttpClientBuilder.create().build();
			try {
				video.setStatus("Encodando");
				HttpPost req = new HttpPost("https://app.zencoder.com/api/v2/jobs");
				// Cria um Objeto Json de Parametros
				JSONObject par = new JSONObject();
				par.put("input", "s3://" + bucketName + "/" + video.getNomePasta());
				par.put("credentials", "s3");
				par.put("api_key", "3afef87bffec61f274dabdde61d7f321");
				JSONArray not = new JSONArray();
				not.put(0, "http://whatsdice.hopto.org:8080/api/videos/callback");
				par.put("notifications", not);
				req.addHeader("content-type", "application/x-www-form-urlencoded");
				StringEntity params;
				params = new StringEntity(par.toString());
				req.setEntity(params);
				HttpResponse res = http.execute(req);
				JSONObject resposta = new JSONObject(EntityUtils.toString(res.getEntity()));
				video.setIdEncoder((long) resposta.getJSONArray("outputs").getJSONObject(0).getLong("id"));
				videosRepository.saveAndFlush(video);
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				//
			}
		}
	}

	public void retorno_video(Long id, String status, String url) throws IOException {
		Videos video = videosRepository.findByidEncoder(id);
		try {

			if (status.equals("failed")) {
				video.setStatus("Erro");
			}
			if (status.equals("finished")) {
				URL download = new URL(url);
				File arquivo = new File(video.getNomeArquivo() + ".mp4");
				FileUtils.copyURLToFile(download, arquivo);
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(usuario_s3, senha_s3);
				AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(regiao_s3)
						.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
				String keyName = "videos/" + video.getChave() + "/convertido/" + video.getNomeArquivo() + ".mp4";
				s3client.putObject(new PutObjectRequest(bucketName, keyName, arquivo));
				video.setNomePastaConvertido(keyName);
				video.setStatus("Finalizado");
			}

		} catch (Exception e) {
			video.setNomePastaConvertido(url);
			video.setStatus("Erro");
			videosRepository.saveAndFlush(video);
		}
		videosRepository.saveAndFlush(video);

	}

	public Page<Videos> listarPorUsuario(String login, Pageable pageable) {
		User usuario = userRepository.findByLogin(login);
		return videosRepository.findByUsuario(usuario.getId(), pageable);
	}

	public byte[] recuperarVideo(Long id) throws IOException {

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(usuario_s3, senha_s3);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(regiao_s3)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		Videos video = videosRepository.findById(id);

		S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, video.getNomePastaConvertido()));

		byte[] byteArray = IOUtils.toByteArray(s3object.getObjectContent());
		

		return byteArray;

	}
}
