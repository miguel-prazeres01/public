package infrastructure.authentication.jwt;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SignatureValidationFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "HMAC_SECRET")
    String hmacSecret;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String signature = requestContext.getHeaderString("X-Signature");

        if (signature == null || signature.isBlank()) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Missing signature").build());
            return;
        }

        // Buffer the body to read it and pass it downstream
        InputStream originalInputStream = requestContext.getEntityStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        originalInputStream.transferTo(baos);
        byte[] bodyBytes = baos.toByteArray();

        // Reset the stream for the resource method
        requestContext.setEntityStream(new ByteArrayInputStream(bodyBytes));

        String body = new String(bodyBytes, StandardCharsets.UTF_8);

        if (!isValidSignature(body, signature)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Invalid signature").build());
        }
    }

    private boolean isValidSignature(String payload, String signature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}

