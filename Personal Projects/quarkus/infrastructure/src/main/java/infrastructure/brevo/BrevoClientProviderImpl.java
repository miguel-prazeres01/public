package infrastructure.brevo;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.AccountApi;
import brevoApi.TransactionalEmailsApi;
import domain.email.BrevoClientProvider;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.config.inject.ConfigProperty;

//Pass brevo: emailTicketLine1_#

@ApplicationScoped
@NoArgsConstructor
public class BrevoClientProviderImpl implements BrevoClientProvider {

    @ConfigProperty(name = "brevo.api.key")
    String brevoApiKey;

    private TransactionalEmailsApi transactionalEmailsApi;

    @PostConstruct
    void init() {
        System.out.println(brevoApiKey);
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(brevoApiKey); 

        AccountApi accountApi = new AccountApi();
        try {
            var account = accountApi.getAccount();
            System.out.println("Connected to Brevo as: " + account.getEmail());
        } catch (ApiException e) {
            e.printStackTrace();
        }

        this.transactionalEmailsApi = new TransactionalEmailsApi();
    }

    public TransactionalEmailsApi getTransactionalEmailsApi() {
        return transactionalEmailsApi;
    }
}


