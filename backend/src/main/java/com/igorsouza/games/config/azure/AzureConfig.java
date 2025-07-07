package com.igorsouza.games.config.azure;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {

    @Value("${AZURE_OPENAI_ENDPOINT}")
    private String endpoint;

    @Value("${AZURE_OPENAI_API_KEY}")
    private String apiKey;

    @Bean
    public AssistantsClient assistantsClient() {
        return new AssistantsClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();
    }
}
