package org.isc.certanalysis.web;

import org.isc.certanalysis.service.FileService;
import org.isc.certanalysis.service.dto.CertificateDTO;
import org.isc.certanalysis.service.dto.FileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@GetMapping("/files/{schemeId}")
	public ResponseEntity<List<CertificateDTO>> getAllFilesBySchemeId(@PathVariable Long schemeId) {
		final List<CertificateDTO> dtos = fileService.findAllFilesBySchemeId(schemeId);
		return ResponseEntity.ok().body(dtos);
	}

	@GetMapping("/file/{id}")
	public ResponseEntity<FileDTO> getFile(@PathVariable Long id) {
		final FileDTO fileDTO = fileService.findFile(id);
		return ResponseEntity.ok().body(fileDTO);
	}

	@PostMapping(value = "/file")
	public ResponseEntity<List<FileDTO>> createFiles(@RequestPart("uploadFile") MultipartFile[] uploadFiles, @RequestPart("file") FileDTO file) throws IOException, CertificateException, NoSuchAlgorithmException, CRLException {
		List<FileDTO> files = fileService.createFile(uploadFiles, file);
		return ResponseEntity.ok().body(files);
	}

	@PutMapping("/file")
	public ResponseEntity<FileDTO> updateFile(@RequestBody FileDTO file) {
		FileDTO result = fileService.updateFile(file);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/file/replace")
	public ResponseEntity<FileDTO> replaceFile(@RequestPart("uploadFile") MultipartFile uploadFile, @RequestPart("file") FileDTO file) throws CertificateException, NoSuchAlgorithmException, CRLException, IOException {
		FileDTO result = fileService.replaceFile(uploadFile, file);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/file/{certificateId}/{fileId}")
	public ResponseEntity<Void> deleteFile(@PathVariable long certificateId, @PathVariable long fileId) {
		fileService.deleteFile(certificateId, fileId);
		return ResponseEntity.ok().build();
	}
}
