package eu.openaire.mas.eosc;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * A service using the EOSC AAI API to periodically regenerate the access token for the accounting API
 * from a refresh token.
 */
@Service
class AccountingApiAuthenticationService {

    private static Logger log = LoggerFactory.getLogger(AccountingApiAuthenticationService.class);

	@Value("${eosc.accounting-api.aai-token-url}")
    private String tokenUrl;

    @Value("${eosc.accounting-api.aai-refresh-username}")
    private String username;

    @Value("${eosc.accounting-api.aai-refresh-password}")
    private String password;

    @Value("${eosc.accounting-api.refresh-token}")
    private String refreshToken;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private AccountingApiMetricsPushService client;

    private RestTemplate aai;

    @PostConstruct
    private void initialize() {
    	aai = restTemplateBuilder.basicAuthentication(username, password).build();
    }

	@Scheduled(fixedRateString = "${eosc.accounting-api.token-refresh-rate}")
	void doRefreshToken() {
		if (refreshToken != null) {
			MultiValueMap<String, String> req = new LinkedMultiValueMap<>();
			req.add("grant_type", "refresh_token");
			req.add("scope", "openid email profile voperson_id eduperson_entitlement");
			req.add("refresh_token", refreshToken);
			Map<String, String> ret = aai.postForObject(tokenUrl, req, Map.class);

			String newToken = ret.get("access_token");
			client.setAccessToken(newToken);
		} else {
			log.warn("Refresh token not set");
		}
	}

	/**
	 * Used to change the refresh token from the {@link AccountingApiControlController}
	 */
	void setRefreshToken(String token) {
    	refreshToken = token;
	}
}
