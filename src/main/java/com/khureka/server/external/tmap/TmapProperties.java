package com.khureka.server.external.tmap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tmap")
public class TmapProperties {
    private boolean enabled;
    private String baseUrl;
    private String apiKey;
}
