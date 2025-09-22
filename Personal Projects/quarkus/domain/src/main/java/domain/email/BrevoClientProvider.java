package domain.email;

import brevoApi.TransactionalEmailsApi;

public interface BrevoClientProvider {
    public TransactionalEmailsApi getTransactionalEmailsApi();
}
