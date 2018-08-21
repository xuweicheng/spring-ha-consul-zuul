package com.weicheng.faststartup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.net.URI;
import java.util.Optional;

/**
 * Created by Weicheng on 8/16/2018.
 */
@RefreshScope
@Configuration
@RestController
public class GreetingController {

    @Value("${greeting}")
    String greeting;

    @Value("${name}")
    String name;

    @Value("${port}")
    String port;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;


    public Optional<URI> serviceUrl() {
        return discoveryClient.getInstances("faststartup")
                .stream()
                .map(si -> si.getUri())
                .findFirst();
    }

    @GetMapping("/restfindclient")
    public String restfindclient(){
        return restTemplate.getForEntity("http://faststartup/api/greeting", String.class).getBody();
    }

    @GetMapping("/findclient")
    public String discoverGreeting() throws ServiceUnavailableException {
        RestTemplate restTemplate = new RestTemplate();
        URI service = serviceUrl()
                .map(s -> s.resolve("/api/greeting"))
                .orElseThrow(ServiceUnavailableException::new);
        return restTemplate.getForEntity(service, String.class).getBody();
    }

    @GetMapping("/api/greeting")
    public String foo() {
        return String.join("-", greeting,name, port);
    }
}
