package com.ritualfresh.profiles.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(
        name = "worker_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_worker_profiles_user", columnNames = "user_id"))
public class WorkerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_worker_profiles_users"))
    private User user;

    @Column(length = 500)
    private String photoUrl;

    @Column(nullable = false)
    private int rankingPosition;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private int yearsOfExperience;

    @Column(length = 500)
    private String offeredServices;

    @Column(length = 120)
    private String workArea;

    @Column(length = 300)
    private String availability;

    @Column(precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    protected WorkerProfile() {
    }

    public WorkerProfile(
            User user,
            String photoUrl,
            String description,
            int yearsOfExperience,
            String offeredServices,
            String workArea,
            String availability,
            BigDecimal hourlyRate) {
        this.user = user;
        this.photoUrl = photoUrl;
        this.rankingPosition = 0;
        this.description = description;
        this.yearsOfExperience = yearsOfExperience;
        this.offeredServices = offeredServices;
        this.workArea = workArea;
        this.availability = availability;
        this.hourlyRate = hourlyRate;
    }

    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public String getDescription() {
        return description;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public String getOfferedServices() {
        return offeredServices;
    }

    public String getWorkArea() {
        return workArea;
    }

    public String getAvailability() {
        return availability;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void edit(
            String photoUrl,
            String description,
            int yearsOfExperience,
            String offeredServices,
            String workArea,
            String availability,
            BigDecimal hourlyRate) {
        this.photoUrl = photoUrl;
        this.description = description;
        this.yearsOfExperience = yearsOfExperience;
        this.offeredServices = offeredServices;
        this.workArea = workArea;
        this.availability = availability;
        this.hourlyRate = hourlyRate;
    }
}
