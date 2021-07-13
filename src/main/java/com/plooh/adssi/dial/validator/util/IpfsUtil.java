package com.plooh.adssi.dial.validator.util;

import io.ipfs.cid.Cid;
import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

public class IpfsUtil {

    public static Multihash fromBase58(String base58String){
        Cid cid = Cid.decode(base58String);
        return cid;
    }

    public static String toBase58(Multihash multihash){
        return Multibase.encode(Multibase.Base.Base58BTC, multihash.toBytes());
    }

}
