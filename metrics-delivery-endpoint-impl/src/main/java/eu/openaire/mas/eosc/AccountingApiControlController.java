package eu.openaire.mas.eosc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for performing some actions related to the EOSC accounting API,
 * eg. running a metrics push on demand or changing the refresh token.
 */
@RestController
public class AccountingApiControlController {

    @Autowired
    private AccountingApiAuthenticationService authService;

    @Autowired
    private AccountingApiMetricsPushService pushService;

    @PutMapping("/-/eosc/refresh-token")
    private void setRefreshToken(@RequestBody String token) {
    	authService.setRefreshToken(token.trim());
    }

    @PostMapping("/-/eosc/refresh-token")
	private void doRefreshToken() {
    	authService.doRefreshToken();
    }

	@PostMapping("/-/eosc/push-metrics")
	private void pushMetrics() {
		pushService.pushMetrics();
	}
}
