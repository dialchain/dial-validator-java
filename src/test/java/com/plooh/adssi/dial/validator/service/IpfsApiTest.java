package com.plooh.adssi.dial.validator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.plooh.adssi.dial.validator.util.IpfsUtil;
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

    public static final String HELLO_WORLD_HASH = "zb2rhfE3SX3q7Ha6UErfMqQReKsmLn73BvdDRagHDM6X1eRFN";
    public static final String HELLO_WORLD_CONTENT = "Hello World!";
    public static final String HELLO_WORLD_FILENAME = "hello-world.txt";

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
    void shouldAddFileAsBytes() throws IOException {
        when(ipfs.add(any(NamedStreamable.class), any(Map.class))).thenReturn(List.of(new MerkleNode(HELLO_WORLD_HASH, Optional.of(HELLO_WORLD_FILENAME))));

        var actual = uut.addFile(HELLO_WORLD_CONTENT.getBytes(StandardCharsets.UTF_8), HELLO_WORLD_FILENAME);

        assertThat(actual).isEqualTo(HELLO_WORLD_HASH);
        verify(ipfs, times(1)).add(any(NamedStreamable.class), any(Map.class));
    }

    @Test
    void shouldAddFileAsString() throws IOException {
        when(ipfs.add(any(NamedStreamable.class), any(Map.class))).thenReturn(List.of(new MerkleNode(HELLO_WORLD_HASH, Optional.of(HELLO_WORLD_FILENAME))));

        var actual = uut.addFile(HELLO_WORLD_CONTENT, HELLO_WORLD_FILENAME);

        assertThat(actual).isEqualTo(HELLO_WORLD_HASH);
        verify(ipfs, times(1)).add(any(NamedStreamable.class), any(Map.class));
    }

    @Test
    void shouldGetFileByHash() throws IOException {
        when(ipfs.cat(any(Multihash.class))).thenReturn(HELLO_WORLD_CONTENT.getBytes(StandardCharsets.UTF_8));

        var actual = uut.getFileByHash(HELLO_WORLD_HASH);

        assertThat(new String(actual, Charset.forName("UTF-8"))).isEqualTo(HELLO_WORLD_CONTENT);
        verify(ipfs, times(1)).cat(any(Multihash.class));
    }

    @Test
    void shouldGetFileInfoByHash() throws IOException {
        when(ipfs.ls(any(Multihash.class))).thenReturn(List.of(new MerkleNode(HELLO_WORLD_HASH, Optional.of(HELLO_WORLD_FILENAME))));

        var actual = uut.getFileInfoByHash(HELLO_WORLD_HASH);

        assertThat(actual).isNotEmpty();
        assertThat(IpfsUtil.toBase58(actual.get(0).hash)).isEqualTo(HELLO_WORLD_HASH);
        verify(ipfs, times(1)).ls(any(Multihash.class));
    }

    @Test
    void shouldPinFileByHash() throws IOException {
        var expected = List.of(IpfsUtil.fromBase58(HELLO_WORLD_HASH));
        when(ipfs.pin.add(any(Multihash.class))).thenReturn(expected);

        var actual = uut.pinFileByHash(HELLO_WORLD_HASH);

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(List.of(HELLO_WORLD_HASH));
        verify(ipfs.pin, times(1)).add(any(Multihash.class));
    }

    @Test
    void shouldUnpinFileByHash() throws IOException {
        var expected = List.of(IpfsUtil.fromBase58(HELLO_WORLD_HASH));
        when(ipfs.pin.rm(any(Multihash.class))).thenReturn(expected);

        var actual = uut.unpinFileByHash(HELLO_WORLD_HASH);

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(List.of(HELLO_WORLD_HASH));
        verify(ipfs.pin, times(1)).rm(any(Multihash.class));
    }

    @Test
    void shouldListAllFiles() throws IOException {
        var expected = Map.of(IpfsUtil.fromBase58(HELLO_WORLD_HASH), (Object)IPFS.PinType.all);
        when(ipfs.pin.ls(IPFS.PinType.all)).thenReturn(expected);

        var actual = uut.listAllFiles();

        assertThat(actual).isNotEmpty();
        assertThat(actual).isEqualTo(List.of(HELLO_WORLD_HASH));
        verify(ipfs.pin, times(1)).ls(IPFS.PinType.all);
    }

}
