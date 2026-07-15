package com.ritualfresh.history.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_history_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceHistoryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Column(nullable = false, length = 120)
    private String serviceName;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceHistoryStatus status;

    @Column(precision = 12, scale = 2)
    private BigDecimal amountArs;

    private Integer workerRating;

    public static ServiceHistoryRecord create(
            User client,
            User worker,
            String serviceName,
            String category,
            LocalDateTime scheduledAt,
            ServiceHistoryStatus status,
            BigDecimal amountArs,
            Integer workerRating) {
        ServiceHistoryRecord record = new ServiceHistoryRecord();
        record.client = client;
        record.worker = worker;
        record.serviceName = serviceName;
        record.category = category;
        record.scheduledAt = scheduledAt;
        record.status = status;
        record.amountArs = amountArs;
        record.workerRating = workerRating;
        return record;
    }

    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }
}
