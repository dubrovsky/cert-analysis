package org.isc.certanalysis.web;

import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.service.FileService;
import org.isc.certanalysis.service.bean.dto.CertDetailsDTO;
import org.isc.certanalysis.service.bean.dto.CrlDetailsDTO;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.isc.certanalysis.service.bean.dto.FileDTO;
import org.isc.certanalysis.web.error.X509ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Collection;
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
	public ResponseEntity<Collection<CertificateDTO>> getAllFilesBySchemeId(@PathVariable Long schemeId, @RequestParam String sortField, @RequestParam int sortOrder) {
		final Collection<CertificateDTO> dtos = fileService.findAllFilesBySchemeId(schemeId, sortField, sortOrder);
		return ResponseEntity.ok().body(dtos);
	}

	@GetMapping("/file/{id}")
	public ResponseEntity<FileDTO> getFile(@PathVariable Long id) {
		final FileDTO fileDTO = fileService.findFile(id);
		return ResponseEntity.ok().body(fileDTO);
	}

	@PostMapping(value = "/file")
	public ResponseEntity<String> createFile(@RequestPart("uploadFile") MultipartFile[] uploadFiles, @RequestPart("file") FileDTO file) throws X509ParseException {
        String result = fileService.createFile(uploadFiles, file);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/file")
	public ResponseEntity<FileDTO> updateFile(@RequestBody FileDTO file) {
		FileDTO result = fileService.updateFile(file);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/file/replace")
	public ResponseEntity<FileDTO> replaceFile(@RequestPart("uploadFile") MultipartFile uploadFile, @RequestPart("file") FileDTO file) throws NoSuchAlgorithmException, IOException, X509ParseException {
		FileDTO result = fileService.replaceFile(uploadFile, file);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/file/{certificateId}/{fileId}")
	public ResponseEntity<Void> deleteFile(@PathVariable long certificateId, @PathVariable long fileId) {
		fileService.deleteFile(certificateId, fileId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/file/download/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable("id") long id) throws IOException {
		File file = fileService.getFile(id);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(file.getSize());
		headers.set(HttpHeaders.CONTENT_TYPE, file.getContentType());
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment" + "; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
		return ResponseEntity.ok().headers(headers).body(file.getBytes());
	}

	@GetMapping("/file/crls/update")
	public ResponseEntity<String> updateCrls() throws IOException {
        String resultMsg = fileService.updateCrls();
        return ResponseEntity.ok().body(resultMsg);
	}

    @GetMapping("/certificate/{id}/view")
	public ResponseEntity<CertDetailsDTO> viewCertificate(@PathVariable("id") long id) throws Exception {
        CertDetailsDTO certificateBean = fileService.viewCertificate(id);
        return ResponseEntity.ok().body(certificateBean);
    }

    @GetMapping("/crl/{id}/view")
    public ResponseEntity<CrlDetailsDTO> viewCrl(@PathVariable("id") long id) throws Exception {
        CrlDetailsDTO crlBean = fileService.viewCrl(id);
        return ResponseEntity.ok().body(crlBean);
    }
}
