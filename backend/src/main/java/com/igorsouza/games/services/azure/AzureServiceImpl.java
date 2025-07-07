package com.igorsouza.games.services.azure;

import com.azure.ai.openai.assistants.*;
import com.azure.ai.openai.assistants.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AzureServiceImpl implements AzureService {

    private final AssistantsClient client;

    @Value("${AZURE_OPENAI_ASSISTANT_ID}")
    private String assistantId;

    @Override
    public String sendMessageToAssistant(String message) throws InterruptedException {
        AssistantThread thread = client.createThread(new AssistantThreadCreationOptions());

        client.createMessage(
                thread.getId(),
                new ThreadMessageOptions(MessageRole.USER, message)
        );

        CreateRunOptions runOptions = new CreateRunOptions(assistantId);
        ThreadRun run = client.createRun(thread.getId(), runOptions);

        while (!run.getStatus().equals(RunStatus.COMPLETED)) {
            Thread.sleep(3000);
            run = client.getRun(thread.getId(), run.getId());
        }

        var messages = client.listMessages(thread.getId()).getData();

        for (ThreadMessage msg : messages) {
            if (msg.getRole() == MessageRole.ASSISTANT) {
                MessageTextContent messageTextContent = (MessageTextContent) msg.getContent().getFirst();
                return messageTextContent.getText().getValue();
            }
        }

        return null;
    }
}
