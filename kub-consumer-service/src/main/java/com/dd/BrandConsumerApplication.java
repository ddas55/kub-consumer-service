package com.dd;

import java.util.Collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import io.prometheus.client.spring.boot.SpringBootMetricsCollector;

@EnablePrometheusEndpoint
@SpringBootApplication
@EnableSpringBootMetricsCollector
public class BrandConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrandConsumerApplication.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	@Bean
	public SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
	    SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(publicMetrics);
	    springBootMetricsCollector.register();
	    return springBootMetricsCollector;
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
	    DefaultExports.initialize();
	    return new ServletRegistrationBean(new MetricsServlet(), "/prometheus");
	}
}