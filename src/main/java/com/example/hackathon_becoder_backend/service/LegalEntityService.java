package com.example.hackathon_becoder_backend.service;

import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntity;
import com.example.hackathon_becoder_backend.domain.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LegalEntityService {

    LegalEntity create(LegalEntity legalEntity, String owner);

    LegalEntity findById(UUID id);

    void changeBalance(UUID id, BigDecimal amount, TransactionType type);

    void deleteById(UUID id);

    LegalEntity assignClientToLegalEntity(UUID legalEntityId, UUID clientId);

    LegalEntity findClientsByLegalEntityId(UUID legalEntityId);

    List<LegalEntity> getAll();

    List<LegalEntity> getAllLegalEntitiesByClientId(UUID fromString);
}
