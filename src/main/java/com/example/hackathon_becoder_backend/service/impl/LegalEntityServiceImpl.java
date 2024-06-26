package com.example.hackathon_becoder_backend.service.impl;

import com.example.hackathon_becoder_backend.domain.client.Client;
import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntityStatus;
import com.example.hackathon_becoder_backend.exception.LackOfBalanceException;
import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntity;
import com.example.hackathon_becoder_backend.domain.transaction.TransactionType;
import com.example.hackathon_becoder_backend.exception.ResourceNotFoundException;
import com.example.hackathon_becoder_backend.repository.LegalEntityRepository;
import com.example.hackathon_becoder_backend.service.ClientService;
import com.example.hackathon_becoder_backend.service.LegalEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegalEntityServiceImpl implements LegalEntityService {
    private final LegalEntityRepository legalEntityRepository;
    private final ClientService clientService;

    @Override
    @Transactional
    public LegalEntity create(LegalEntity legalEntity, String owner) {
        legalEntity.setOwner(owner);
        legalEntity.setStatus(LegalEntityStatus.EXISTS.name());
        legalEntity.setClients(Set.of(clientService.findByUsername(owner)));
        return legalEntityRepository.save(legalEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public LegalEntity findById(UUID id) {
        return legalEntityRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found")
                );
    }

    @Override
    @Transactional
    public void changeBalance(UUID id, BigDecimal amount, TransactionType type) {
        LegalEntity legalEntity = findById(id);
        switch (type) {

            case DEBIT -> {
                BigDecimal newBalance = legalEntity.getBalance().subtract(amount);
                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new LackOfBalanceException("Not enough balance for this DEBIT");
                }
                legalEntity.setBalance(legalEntity.getBalance().subtract(amount));
            }

            case REFILL -> legalEntity.setBalance(legalEntity.getBalance().add(amount));

        }
        legalEntityRepository.save(legalEntity);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        LegalEntity legalEntity = findById(id);
        legalEntity.setStatus(String.valueOf(LegalEntityStatus.DELETED));
        legalEntityRepository.save(legalEntity);
    }

    @Override
    @Transactional
    public LegalEntity assignClientToLegalEntity(UUID legalEntityId, UUID clientId) {
        LegalEntity legalEntity = findById(legalEntityId);
        Client client = clientService.findById(clientId);
        legalEntity.getClients().add(client);
        legalEntityRepository.save(legalEntity);
        return legalEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public LegalEntity findClientsByLegalEntityId(UUID legalEntityId) {
        return findById(legalEntityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalEntity> getAll() {
        return legalEntityRepository.findAll();
    }

    @Override
    public List<LegalEntity> getAllLegalEntitiesByClientId(UUID fromString) {
        return legalEntityRepository.findAllLegalEntitiesByClientId(fromString);
    }
}
