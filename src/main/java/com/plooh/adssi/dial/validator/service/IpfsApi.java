package com.plooh.adssi.dial.validator.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpfsApi {

    private final IPFS ipfs;

    /**
     * Add a file on the IPFS network by uploading it to the connected IPFS
     * node and stored in its local datastore.
     *
     * @param content
     * @return unique identifier of the file called "multihash"
     */
    public String addFile(String content){
        try {
            NamedStreamable file = new NamedStreamable.ByteArrayWrapper(content.getBytes(StandardCharsets.UTF_8));
            MerkleNode response = ipfs.add(file).get(0);
            String hash = response.hash.toBase58();
            log.info("Filename: {}, Hash (base 58): {}", response.name.orElse("unknown"), hash);
            return hash;
        } catch (IOException e) {
            throw new RuntimeException("Error while adding file to the IPFS node", e);
        }
    }

    /**
     * Get the file content of the given hash.
     *
     * @param hash
     * @return file content
     */
    public byte[] getFileByHash(String hash){
        try {
            byte[] content = ipfs.cat(Multihash.fromBase58(hash));
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Error while getting file from the IPFS node", e);
        }
    }

    /**
     * Get file information of the given hash.
     *
     * @param hash
     * @return list of the content-addressable objects added on the IPFS network
     */
    public List<MerkleNode> getFileInfoByHash(String hash) {
        try {
            List<MerkleNode> list = ipfs.ls(Multihash.fromBase58(hash));
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Error while getting info from the IPFS node", e);
        }
    }

    /**
     * Pin the file with the given hash to the local storage, such that other nodes on the IPFS
     * network know they can access the file from the owner machine. Pinning an Object linked
     * to other Objects (children) such as a directory automatically pins all the subsequent children.
     *
     * @param hash
     * @return list of the unique identifiers of the pinned files
     */
    public List<Multihash> pinFileByHash(String hash) {
        try {
            List<Multihash> list = ipfs.pin.add(Multihash.fromBase58(hash));
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Error while pining to the IPFS node", e);
        }
    }

    /**
     * Remove a file with the given hash from the connected IPFS node.
     *
     * @param hash
     * @return list of the unique identifiers of the removed files
     */
    public List<Multihash> unpinFileByHash(String hash) {
        try {
            List<Multihash> list = ipfs.pin.rm(Multihash.fromBase58(hash));
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Error while pining to the IPFS node", e);
        }
    }

    /**
     * list all the content hosted on the connected IPFS node
     *
     * @return list of all content
     */
    public Map<Multihash, Object> listAllFiles() {
        try {
            Map<Multihash, Object> list = ipfs.pin.ls(IPFS.PinType.all);
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Error while pining to the IPFS node", e);
        }
    }

}
