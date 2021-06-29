package com.plooh.adssi.dial.validator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.plooh.adssi.dial.validator.service.IpfsApi;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.multihash.Multihash;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class IpfsControllerTest {

    public static final String HELLO_WORLD_HASH = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG";
    public static final String HELLO_WORLD_CONTENT = "Hello World!";
    public static final String HELLO_WORLD_FILENAME = "hello-world.txt";
    public static final byte[] HELLO_WORLD_CONTENT_AS_BYTES = HELLO_WORLD_CONTENT.getBytes(StandardCharsets.UTF_8);

    @Mock
    private IpfsApi ipfsApi;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private IpfsController uut;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCheckIpfsServerIsConnected() {
        String ipfsName = "localhost";
        when(ipfsApi.getIpfsName()).thenReturn(ipfsName);

        var actual = uut.health();

        assertThat(actual.getBody()).isEqualTo("Ipfs server is connected to: localhost");
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(ipfsApi, times(2)).getIpfsName();
    }

    @Test
    public void shouldCheckIpfsServerIsNotConnected() {
        when(ipfsApi.getIpfsName()).thenReturn(null);

        var actual = uut.health();

        assertThat(actual.getBody()).isEqualTo("Ipfs server is not connected...");
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(ipfsApi, times(1)).getIpfsName();
    }

    @Test
    public void shouldAddFile() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(HELLO_WORLD_CONTENT_AS_BYTES);
        when(multipartFile.getOriginalFilename()).thenReturn(HELLO_WORLD_FILENAME);
        when(ipfsApi.addFile(HELLO_WORLD_CONTENT_AS_BYTES, HELLO_WORLD_FILENAME)).thenReturn(HELLO_WORLD_HASH);

        var actual = uut.addFile(multipartFile);

        assertThat(actual.getBody()).isEqualTo(HELLO_WORLD_HASH);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(ipfsApi, times(1)).addFile(HELLO_WORLD_CONTENT_AS_BYTES, HELLO_WORLD_FILENAME);
    }

    @Test
    public void shouldNotAddFileBecauseEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);

        var actual = uut.addFile(multipartFile);

        assertThat(actual.getBody()).isNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(ipfsApi, times(0)).getFileByHash(HELLO_WORLD_HASH);
    }

    @Test
    public void shouldGetFileByHash() {
        when(ipfsApi.getFileByHash(HELLO_WORLD_HASH)).thenReturn(HELLO_WORLD_CONTENT_AS_BYTES);

        var actual = uut.getFileByHash(HELLO_WORLD_HASH);

        assertThat(actual.getBody()).isEqualTo(HELLO_WORLD_CONTENT_AS_BYTES);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(ipfsApi, times(1)).getFileByHash(HELLO_WORLD_HASH);
    }

    @Test
    void shouldGetFileInfoByHash() {
        when(ipfsApi.getFileInfoByHash(HELLO_WORLD_HASH)).thenReturn(List.of(new MerkleNode(HELLO_WORLD_HASH, Optional.of(HELLO_WORLD_FILENAME))));

        var actual = uut.getFileInfoByHash(HELLO_WORLD_HASH);

        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody().get(0).hash.toBase58()).isEqualTo(HELLO_WORLD_HASH);
        verify(ipfsApi, times(1)).getFileInfoByHash(HELLO_WORLD_HASH);
    }

    @Test
    void shouldPinFileByHash() {
        var expected = List.of(HELLO_WORLD_HASH);
        when(ipfsApi.pinFileByHash(HELLO_WORLD_HASH)).thenReturn(expected);

        var actual = uut.pinFileByHash(HELLO_WORLD_HASH);

        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody()).isEqualTo(expected);
        verify(ipfsApi, times(1)).pinFileByHash(HELLO_WORLD_HASH);
    }

    @Test
    void shouldUnpinFileByHash() {
        var expected = List.of(HELLO_WORLD_HASH);
        when(ipfsApi.unpinFileByHash(HELLO_WORLD_HASH)).thenReturn(expected);

        var actual = uut.unpinFileByHash(HELLO_WORLD_HASH);

        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody()).isEqualTo(expected);
        verify(ipfsApi, times(1)).unpinFileByHash(HELLO_WORLD_HASH);
    }

    @Test
    void shouldListAllFiles() {
        var expected = Map.of(Multihash.fromBase58(HELLO_WORLD_HASH), (Object) IPFS.PinType.all);
        when(ipfsApi.listAllFiles()).thenReturn(List.of(HELLO_WORLD_HASH));

        var actual = uut.getAllFiles();

        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody()).isEqualTo(List.of(HELLO_WORLD_HASH));
        verify(ipfsApi, times(1)).listAllFiles();
    }

}
