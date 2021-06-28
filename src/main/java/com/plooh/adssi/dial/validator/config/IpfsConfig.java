package com.plooh.adssi.dial.validator.config;

import io.ipfs.api.IPFS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IpfsConfig {

    @Value("${ipfs.host}")
    private String host;

    @Value("${ipfs.port}")
    private int port;

    @Value("${ipfs.multiaddr}")
    private String multiaddr;

    @Bean
    public IPFS ipfs() {
        if (StringUtils.isNotBlank(multiaddr)){
            return new IPFS(multiaddr);
        }
        return new IPFS(host, port);
    }

}
