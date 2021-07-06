package com.plooh.adssi.dial.validator.controller;

import com.plooh.adssi.dial.validator.service.IpfsApi;
import io.ipfs.api.MerkleNode;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/ipfs")
public class IpfsController {

    private final IpfsApi ipfsApi;

    @GetMapping(value = "/health")
    public ResponseEntity<String> health() {
        String response;
        if (StringUtils.isNotBlank(ipfsApi.getIpfsName())) {
            response = "Ipfs server is connected to: " + ipfsApi.getIpfsName();
        } else {
            response = "Ipfs server is not connected...";
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/files", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addFile(@RequestParam("file") MultipartFile file) {
        if ( file == null || file.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Adding file: {}, Content Type: {} to IPFS.", file.getOriginalFilename(), file.getContentType());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ipfsApi.addFile(file.getBytes(), file.getOriginalFilename()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/files/{hash}")
    public ResponseEntity<byte[]> getFileByHash(@PathVariable("hash") String hash) {
        if (StringUtils.isBlank(hash)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Getting file info for {} from IPFS.", hash);
        return ResponseEntity.ok(ipfsApi.getFileByHash(hash));
    }

    @GetMapping(value = "/files/{hash}/info")
    public ResponseEntity<List<MerkleNode>> getFileInfoByHash(@PathVariable("hash") String hash) {
        if (StringUtils.isBlank(hash)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        }

        log.info("Getting file content for {} from IPFS.", hash);
        List<MerkleNode> list = ipfsApi.getFileInfoByHash(hash);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/files/{hash}/pin")
    public ResponseEntity<List<String>> pinFileByHash(@PathVariable("hash") String hash) {
        if (StringUtils.isBlank(hash)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        }

        log.info("Pinning file info for {} from IPFS.", hash);
        List<String> list = ipfsApi.pinFileByHash(hash);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/files/{hash}/unpin")
    public ResponseEntity<List<String>> unpinFileByHash(@PathVariable("hash") String hash) {
        if (StringUtils.isBlank(hash)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        }

        log.info("Unpinning file info for {} from IPFS.", hash);
        List<String> list = ipfsApi.unpinFileByHash(hash);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/files")
    public ResponseEntity<List<String>> getAllFiles() {
        List<String> list = ipfsApi.listAllFiles();
        return ResponseEntity.ok(list);
    }

}
