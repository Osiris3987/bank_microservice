package com.example.hackathon_becoder_backend.domain.legal_entity;

import com.example.hackathon_becoder_backend.domain.client.Client;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.repository.cdi.Eager;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "legal_entity")
@Data
public class LegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "status")
    private String status;

    @Column(name = "owner")
    private String owner;

    @Version
    @Column(name = "version")
    private Integer version;

    @ManyToMany
    @JoinTable(name = "legal_entity_clients",
            joinColumns = @JoinColumn(name = "legal_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    @EqualsAndHashCode.Exclude
    private Set<Client> clients;
}
