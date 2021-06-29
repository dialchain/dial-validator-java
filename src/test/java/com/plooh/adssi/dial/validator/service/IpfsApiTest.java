package com.plooh.adssi.dial.validator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.plooh.adssi.dial.validator.util.ReflectionUtils;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
public class IpfsApiTest {

    public static final String CONTENT_HELLO_WORLD = "Hello World!";

    @Mock
    private IPFS ipfs;

    @Mock
    public IPFS.Pin pin;

    @InjectMocks
    private IpfsApi uut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ReflectionUtils.setFinalFieldValue(ipfs, "pin", pin);
    }

    @Test
    void shouldAddFile() throws IOException {
        var hash = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG";
        when(ipfs.add(any(NamedStreamable.class))).thenReturn(List.of(new MerkleNode(hash, Optional.of("hello-world.txt"))));

        var actual = uut.addFile(CONTENT_HELLO_WORLD);

        assertThat(actual).isEqualTo(hash);
        verify(ipfs, times(1)).add(any(NamedStreamable.class));
    }

    @Test
    void shouldGetFileByHash() throws IOException {
        var hash = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG"; // Hash of a file
        when(ipfs.cat(any(Multihash.class))).thenReturn(CONTENT_HELLO_WORLD.getBytes(StandardCharsets.UTF_8));

        var actual = uut.getFileByHash(hash);

        assertThat(new String(actual, Charset.forName("UTF-8"))).isEqualTo(CONTENT_HELLO_WORLD);
        verify(ipfs, times(1)).cat(any(Multihash.class));
    }

    @Test
    void shouldGetFileInfoByHash() throws IOException {
        var input = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG"; // Hash of a file
        when(ipfs.ls(any(Multihash.class))).thenReturn(List.of(new MerkleNode(input, Optional.of("hello-world.txt"))));

        var actual = uut.getFileInfoByHash(input);

        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0).hash.toBase58()).isEqualTo(input);
        verify(ipfs, times(1)).ls(any(Multihash.class));
    }

    @Test
    void shouldPinFileByHash() throws IOException {
        var hash = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG"; // Hash of a file
        var expected = List.of(Multihash.fromBase58(hash));
        when(ipfs.pin.add(any(Multihash.class))).thenReturn(expected);

        var actual = uut.pinFileByHash(hash);

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(expected);
        verify(ipfs.pin, times(1)).add(any(Multihash.class));
    }

    @Test
    void shouldUnpinFileByHash() throws IOException {
        var hash = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG"; // Hash of a file
        var expected = List.of(Multihash.fromBase58(hash));
        when(ipfs.pin.rm(any(Multihash.class))).thenReturn(expected);

        var actual = uut.unpinFileByHash(hash);

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(expected);
        verify(ipfs.pin, times(1)).rm(any(Multihash.class));
    }

    @Test
    void shouldListAllFiles() throws IOException {
        var hash = "QmfM2r8seH2GiRaC4esTjeraXEachRt8ZsSeGaWTPLyMoG"; // Hash of a file
        var expected = Map.of(Multihash.fromBase58(hash), (Object)IPFS.PinType.all);
        when(ipfs.pin.ls(IPFS.PinType.all)).thenReturn(expected);

        var actual = uut.listAllFiles();

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(expected);
        verify(ipfs.pin, times(1)).ls(IPFS.PinType.all);
    }

}
