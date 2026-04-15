package com.payroll.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.audit.model.AuditEvent;
import com.payroll.audit.model.AuditResult;
import com.payroll.audit.repository.AuditEventRepository;
import com.payroll.identity.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditService {
    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditEventRepository auditEventRepository, ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = objectMapper;
    }

    public void record(UserAccount actor, String eventType, String resourceType, String resourceId, AuditResult result, Map<String, ?> metadata) {
        AuditEvent event = new AuditEvent();
        event.setActorUser(actor);
        event.setEventType(eventType);
        event.setResourceType(resourceType);
        event.setResourceId(resourceId);
        event.setResult(result);
        event.setMetadataJson(toJson(metadata));
        auditEventRepository.save(event);
    }

    private String toJson(Map<String, ?> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
