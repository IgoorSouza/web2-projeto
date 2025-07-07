package com.igorsouza.games.services.azure;

public interface AzureService {
    String sendMessageToAssistant(String message) throws InterruptedException;
}
