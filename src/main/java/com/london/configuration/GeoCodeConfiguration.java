package com.london.configuration;

import com.london.dto.GeoCode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "geocode")
public class GeoCodeConfiguration {
    private Map<String, GeoCode> cityMap;
}
