package com.chatbot.mcp;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RecruiterMcpTools {

    private final JavaMailSender mailSender;

    public RecruiterMcpTools(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @McpTool(description = "Read a résumé file and return its extracted text")
    public String readResume(
            @McpToolParam(description = "Path to the résumé file", required = true)
            String path) {

        List<Document> docs =
            new TikaDocumentReader(new FileSystemResource(path)).read();

        return docs.stream()
            .map(Document::getText)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"));
    }

    @McpTool(description = "Send finalized interview questions to a consultant by email")
    public String sendInterviewEmail(
            @McpToolParam(description = "Consultant email address", required = true)
            String to,
            @McpToolParam(description = "Email subject", required = true)
            String subject,
            @McpToolParam(description = "Interview questions to send", required = true)
            String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        return "Email sent to " + to;
    }
}