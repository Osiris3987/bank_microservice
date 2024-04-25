package com.example.hackathon_becoder_backend;

import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntity;
import com.example.hackathon_becoder_backend.service.LegalEntityService;
import com.example.hackathon_becoder_backend.web.dto.LegalEntityWithClientsDto;
import com.example.hackathon_becoder_backend.web.mapper.LegalEntityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class LegalEntityServiceImplTest extends IntegrationEnvironment {
    @Autowired
    LegalEntityService legalEntityService;
    @Autowired
    LegalEntityMapper legalEntityMapper;

    @Rollback(value = false)
    @Test
    void deleteById() {
        final UUID testId = UUID.fromString("b3ec6a4c-6245-419d-b884-024a69fea3eb");
        legalEntityService.deleteById(testId);
        assertEquals("DELETED", legalEntityService.findById(testId).getStatus());
        System.out.println("Test passed!");
    }

    @Test
    void getAllLegalEntitiesByClientId() {
        final UUID testId = UUID.fromString("f0caf844-5a61-43a7-b1c2-e66971f5e08a");
        List<LegalEntity> legalEntities = legalEntityService.getAllLegalEntitiesByClientId(testId);
        for (var legalEntity : legalEntities) {
            System.out.println(legalEntity.getId());
        }
    }

    @Test
    void getAll() {
        List<LegalEntity> legalEntities = legalEntityService.getAll();
        for (var legalEntity : legalEntities) {
            System.out.println(legalEntity.getId());
        }
    }

    @Rollback(value = false)
    @Test
    void assignClientToLegalEntity() {
        final UUID testClientId = UUID.fromString("f0caf844-5a61-43a7-b1c2-e66971f5e08a");
        final UUID testLegalEntityId = UUID.fromString("b3ec6a4c-6245-419d-b884-024a69fea3ec");
        LegalEntity legalEntity = legalEntityService.assignClientToLegalEntity(testLegalEntityId, testClientId);
        LegalEntityWithClientsDto legalEntityWithClientsDto = legalEntityMapper.toDtoWithClients(legalEntity);
        assertEquals(1, legalEntityWithClientsDto.getClients().size());
    }
}