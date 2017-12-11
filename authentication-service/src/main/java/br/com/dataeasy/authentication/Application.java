package br.com.dataeasy.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import br.com.dataeasy.authentication.model.UserOrganization;
import br.com.dataeasy.authentication.repository.OrgUserRepository;

@SpringBootApplication
@RestController
@EnableResourceServer
@EnableAuthorizationServer
@EnableEurekaClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    OrgUserRepository orgUserRepository;

    @RequestMapping(value = {"/user"}, produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        String userName = user.getUserAuthentication().getPrincipal().toString();
        userInfo.put("user", userName);
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));

        UserOrganization userOrganization = orgUserRepository.findByUserName(userName);

        if (userOrganization != null) {
            userInfo.put("organizationId", userOrganization.getOrganizationId());
        }

        return userInfo;
    }
}
