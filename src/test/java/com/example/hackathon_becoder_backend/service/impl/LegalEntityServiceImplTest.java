package com.example.hackathon_becoder_backend.service.impl;

import com.example.hackathon_becoder_backend.domain.client.Client;
import com.example.hackathon_becoder_backend.exception.LackOfBalanceException;
import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntity;
import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntityStatus;
import com.example.hackathon_becoder_backend.domain.transaction.TransactionType;
import com.example.hackathon_becoder_backend.exception.ResourceNotFoundException;
import com.example.hackathon_becoder_backend.repository.LegalEntityRepository;
import com.example.hackathon_becoder_backend.service.impl.ClientServiceImpl;
import com.example.hackathon_becoder_backend.service.impl.LegalEntityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
class LegalEntityServiceImplTest {
    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Mock
    private ClientServiceImpl clientService;

    @InjectMocks
    private LegalEntityServiceImpl legalEntityService;

    @Test
    void create_shouldCreateNewLegalEntityWithDelegatedOwner() {
        String ownerName = "owner";
        Client client = new Client();
        client.setName(ownerName);
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setClients(new HashSet<>());

        when(clientService.findByUsername(ownerName)).thenReturn(client);
        legalEntityService.create(legalEntity, ownerName);

        assertEquals(ownerName, legalEntity.getOwner()); //  Для юр.лица установлен владелец
        assertEquals(LegalEntityStatus.EXISTS.name(), legalEntity.getStatus()); //  Статус юр. лица EXISTS
        assertTrue(legalEntity.getClients().contains(client));
        verify(legalEntityRepository, Mockito.times(1)).save(legalEntity); //  Единственное обращение к репозиторию
        System.out.println(LocalDateTime.now().toLocalTime() + "[create_shouldCreateNewLegalEntityWithDelegatedOwner] passed!");
    }

    @Test
    void findById_shouldReturnLegalEntityWithDelegatedID() {
        UUID legalEntityUUID = UUID.randomUUID();
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setId(legalEntityUUID);

        when(legalEntityRepository.findById(legalEntityUUID)).thenReturn(Optional.of(legalEntity));
        LegalEntity receivedLegalEntity = legalEntityService.findById(legalEntityUUID);

        assertNotNull(receivedLegalEntity); //  Юр. лицо было получено
        assertEquals(legalEntityUUID, receivedLegalEntity.getId());
        verify(legalEntityRepository, Mockito.times(1)).findById(legalEntityUUID);
        System.out.println(LocalDateTime.now().toLocalTime() + "[findById_shouldReturnLegalEntityWithDelegatedID] passed!");
    }

    @Test
    void changeBalance_shouldDebitAndRefillDelegatedLegalEntityBalance() {
        BigDecimal amount = new BigDecimal(1000);
        BigDecimal basicBalance = new BigDecimal(10000);
        UUID legalEntityUUID = UUID.randomUUID();
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setBalance(basicBalance);
        legalEntity.setId(legalEntityUUID);

        when(legalEntityRepository.findById(legalEntityUUID)).thenReturn(Optional.of(legalEntity));
        legalEntityService.changeBalance(legalEntityUUID, amount, TransactionType.DEBIT);
        legalEntityService.changeBalance(legalEntityUUID, amount, TransactionType.REFILL);
        legalEntityService.changeBalance(legalEntityUUID, amount, TransactionType.REFILL);

        assertThrows(LackOfBalanceException.class, () -> legalEntityService.changeBalance(legalEntityUUID, amount.multiply(BigDecimal.valueOf(12)), TransactionType.DEBIT));
        assertEquals(BigDecimal.valueOf(11000), legalEntity.getBalance()); //  Баланс соответствует ожидаемому значению
        verify(legalEntityRepository, Mockito.times(4)).findById(legalEntityUUID); //  Поиск по id был выполнен 3 раза
        verify(legalEntityRepository, Mockito.times(3)).save(legalEntity); //  Сохранение в БД было выполнено 3 раза
        System.out.println(LocalDateTime.now().toLocalTime() + "[changeBalance_shouldDebitAndRefillDelegatedLegalEntityBalance] passed!");
    }

    @Test
    void deleteById_shouldRemoveLegalEntityWithDelegatedId() {
        UUID clientUUID = UUID.randomUUID();
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setId(clientUUID);

        when(legalEntityRepository.findById(clientUUID)).thenReturn(Optional.of(legalEntity));
        legalEntityService.deleteById(clientUUID);

        assertEquals(LegalEntityStatus.DELETED, LegalEntityStatus.valueOf(legalEntity.getStatus())); //  Статус клиента DELETED
        verify(legalEntityRepository, Mockito.times(1)).findById(clientUUID); //  Поиск клиента был выполнен единожды
        verify(legalEntityRepository, Mockito.times(1)).save(legalEntity); //  Удаление конкретного клиента было выполнено единожды
        System.out.println(LocalDateTime.now().toLocalTime() + "[deleteById_shouldRemoveLegalEntityWithDelegatedId] passed!");
    }

    @Test
    void assignClientToLegalEntity_shouldAssignLegalEntityToClientByAssignedUUIDs() {
        UUID clientUUID = UUID.randomUUID();
        UUID legalEntityUUID = UUID.randomUUID();
        LegalEntity legalEntity = new LegalEntity();
        Client client = new Client();
        legalEntity.setId(legalEntityUUID);
        client.setId(clientUUID);
        legalEntity.setClients(new HashSet<>());

        when(clientService.findById(clientUUID)).thenReturn(client);
        when(legalEntityRepository.findById(legalEntityUUID)).thenReturn(Optional.of(legalEntity));
        legalEntityService.assignClientToLegalEntity(legalEntityUUID, clientUUID);

        assertTrue(legalEntity.getClients().contains(client)); //  Юр.лицо связано с клиентом
        verify(clientService, Mockito.times(1)).findById(clientUUID); //  Единожды обращаемся к сервису клиентов
        verify(legalEntityRepository, Mockito.times(1)).findById(legalEntityUUID); //  Единожды обращаемся к поиску юр.лиц
        verify(legalEntityRepository, Mockito.times(1)).save(legalEntity); //  Единожды сохраняем изменения в репозиторий юр.лиц
        System.out.println(LocalDateTime.now().toLocalTime() + "[assignClientToLegalEntity_shouldAssignLegalEntityToClientByAssignedUUIDs] passed!");
    }

    @Test
    void findClientsByLegalEntityId() {
        UUID legalEntityUUID = UUID.randomUUID();
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setId(legalEntityUUID);

        when(legalEntityRepository.findById(legalEntityUUID)).thenReturn(Optional.of(legalEntity));
        LegalEntity receivedLegalEntity = legalEntityService.findClientsByLegalEntityId(legalEntityUUID);

        assertThrows(ResourceNotFoundException.class, () -> legalEntityService.findClientsByLegalEntityId(UUID.randomUUID()));
        assertEquals(legalEntity, receivedLegalEntity);
        System.out.println(LocalDateTime.now().toLocalTime() + "[getAllLegalEntitiesByClientId_shouldReturnAllLegalEntitiesBelongsToDelegatedClient] passed!");
    }

    @Test
    void getAll_shouldReturnAllLegalEntitiesFromDataBase(){
        List<LegalEntity> legalEntities = List.of(new LegalEntity(), new LegalEntity(), new LegalEntity());

        when(legalEntityRepository.findAll()).thenReturn(legalEntities);

        List<LegalEntity> receivedLegalEntities = legalEntityService.getAll();

        assertArrayEquals(legalEntities.toArray(), receivedLegalEntities.toArray());
        verify(legalEntityRepository, Mockito.times(1)).findAll();
        System.out.println(LocalDateTime.now().toLocalTime() + "[getAll_shouldReturnAllLegalEntitiesFromDataBase] passed!");
    }

}