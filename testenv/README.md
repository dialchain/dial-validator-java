# Test Env

## Start IPFS docker container

```
docker run -d -v $PWD/ipfs/export:/export -v $PWD/ipfs/data/ipfs:/data/ipfs -p 4001:4001 -p 8080:8080 -p 5001:5001 ipfs/go-ipfs:v0.9.0
```

## Test application

Health Check

```
curl -v -k -X GET http://localhost:9091/ipfs/health --fail
```

Add File to IPFS

```
curl -v -k -F 'file=@dialchain.txt' http://localhost:9091/ipfs/files --fail
```

Get File from IPFS

```
curl -v -k -X GET http://localhost:9091/ipfs/files/QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2 --fail
```

Get File Information from IPFS

```
curl -v -k -X GET http://localhost:9091/ipfs/files/QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2/info --fail
```

Pin File on IPFS

```
curl -v -k -X POST http://localhost:9091/ipfs/files/QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2/pin --fail
```

Unpin File on IPFS

```
curl -v -k -X POST http://localhost:9091/ipfs/files/QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2/unpin --fail
```

Get all files from IPFS

```
curl -v -k -X GET http://localhost:9091/ipfs/files --fail
```

Example Content:

CID: `QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2`

Check File Accessibility:

Run below link to check file dialchain.txt, fully loaded on IPFS: https://ipfs.io/ipfs/QmbzdRF9v2c7NPTfmSJUvWgup4oFBE9LMnvUbBhFg8BLj2