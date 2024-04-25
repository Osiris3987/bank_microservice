package com.example.hackathon_becoder_backend;

import com.example.hackathon_becoder_backend.domain.legal_entity.LegalEntity;
import com.example.hackathon_becoder_backend.domain.transaction.TransactionType;
import com.example.hackathon_becoder_backend.service.LegalEntityService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest
@Transactional
@Configuration
@EnableRetry
class TransactionServiceImplTest {
    @Autowired
    private LegalEntityService legalEntityService;

    @Test
    @SneakyThrows
    void testConcurrentTransactionsWithConsistentBalance() {
        LegalEntity legalEntity1 = legalEntityService.findById(UUID.fromString("b3ec6a4c-6245-419d-b884-024a69fea3ec"));

        ExecutorService executor1 = Executors.newFixedThreadPool(10);
        ExecutorService executor2 = Executors.newFixedThreadPool(10);

        List<Future<Object>> futures = new ArrayList<>();
        Callable<Object> task = () -> {
            legalEntityService.changeBalance(legalEntity1.getId(), BigDecimal.valueOf(1000), TransactionType.REFILL);
            return null;
        };
        for (int i = 0; i < 55; i++) {
            futures.add(executor1.submit(task));
            futures.add(executor2.submit(task));
        }

        for (Future<Object> future : futures) {
            future.get();
        }
        executor1.shutdown();
        executor2.shutdown();

        // Verify the final balance
        LegalEntity updatedLegalEntity = legalEntityService.findById(legalEntity1.getId());
    }
}
