package com.ritualfresh.profiles;

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

@Entity
@Table(
        name = "client_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_client_profiles_user", columnNames = "user_id"))
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

    @Column(length = 500)
    private String hiringPreferences;

    protected ClientProfile() {
    }

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
            String hiringPreferences) {
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
        this.hiringPreferences = hiringPreferences;
    }

    void assignIdIfMissing(long id) {
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

    public int getClientRating() {
        return clientRating;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getFloor() {
        return floor;
    }

    public String getApartment() {
        return apartment;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getHiringPreferences() {
        return hiringPreferences;
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
            String hiringPreferences) {
        this.photoUrl = photoUrl;
        this.contactPhone = contactPhone;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.floor = floor;
        this.apartment = apartment;
        this.postalCode = postalCode;
        this.city = city;
        this.province = province;
        this.hiringPreferences = hiringPreferences;
    }
}
