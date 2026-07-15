package com.ritualfresh.profiles.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "client_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_client_profiles_user", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_client_profiles_users"))
    private User user;

    @Column(length = 500)
    private String photoUrl;

    @Column(nullable = false)
    private int clientRating;

    @Column(length = 30)
    private String contactPhone;

    @Column(length = 120)
    private String streetName;

    @Column(length = 20)
    private String streetNumber;

    @Column(length = 20)
    private String floor;

    @Column(length = 40)
    private String apartment;

    @Column(length = 12)
    private String postalCode;

    @Column(length = 80)
    private String city;

    @Column(length = 80)
    private String province;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_frequency", length = 20)
    private ServiceFrequency serviceFrequency;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "client_profile_time_slots",
            joinColumns = @JoinColumn(name = "client_profile_id", foreignKey = @ForeignKey(name = "fk_client_time_slots_profile")))
    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false, length = 20)
    private Set<PreferredTimeSlot> preferredTimeSlots = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "client_profile_service_interests",
            joinColumns = @JoinColumn(name = "client_profile_id", foreignKey = @ForeignKey(name = "fk_client_services_profile")))
    @Enumerated(EnumType.STRING)
    @Column(name = "service_interest", nullable = false, length = 40)
    private Set<ServiceInterest> serviceInterests = new LinkedHashSet<>();

    @Column(name = "other_service_interest", length = 120)
    private String otherServiceInterest;

    // Conserva la columna anterior para no perder las preferencias ya cargadas.
    @Column(name = "hiring_preferences", length = 500)
    private String additionalNotes;

    public ClientProfile(
            User user,
            String photoUrl,
            String contactPhone,
            String streetName,
            String streetNumber,
            String floor,
            String apartment,
            String postalCode,
            String city,
            String province,
            ServiceFrequency serviceFrequency,
            Set<PreferredTimeSlot> preferredTimeSlots,
            Set<ServiceInterest> serviceInterests,
            String otherServiceInterest,
            String additionalNotes) {
        this.user = user;
        this.photoUrl = photoUrl;
        this.clientRating = 0;
        this.contactPhone = contactPhone;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.floor = floor;
        this.apartment = apartment;
        this.postalCode = postalCode;
        this.city = city;
        this.province = province;
        this.serviceFrequency = serviceFrequency;
        this.preferredTimeSlots = new LinkedHashSet<>(preferredTimeSlots);
        this.serviceInterests = new LinkedHashSet<>(serviceInterests);
        this.otherServiceInterest = otherServiceInterest;
        this.additionalNotes = additionalNotes;
    }

    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public void edit(
            String photoUrl,
            String contactPhone,
            String streetName,
            String streetNumber,
            String floor,
            String apartment,
            String postalCode,
            String city,
            String province,
            ServiceFrequency serviceFrequency,
            Set<PreferredTimeSlot> preferredTimeSlots,
            Set<ServiceInterest> serviceInterests,
            String otherServiceInterest,
            String additionalNotes) {
        this.photoUrl = photoUrl;
        this.contactPhone = contactPhone;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.floor = floor;
        this.apartment = apartment;
        this.postalCode = postalCode;
        this.city = city;
        this.province = province;
        this.serviceFrequency = serviceFrequency;
        this.preferredTimeSlots = new LinkedHashSet<>(preferredTimeSlots);
        this.serviceInterests = new LinkedHashSet<>(serviceInterests);
        this.otherServiceInterest = otherServiceInterest;
        this.additionalNotes = additionalNotes;
    }
}
