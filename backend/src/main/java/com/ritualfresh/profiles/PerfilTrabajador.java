package com.ritualfresh.profiles;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.ritualfresh.auth.Usuario;

import java.math.BigDecimal;

@Entity
@Table(
        name = "trabajadores",
        uniqueConstraints = @UniqueConstraint(name = "uk_trabajadores_usuario", columnNames = "usuario_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfilTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTrabajador;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trabajadores_usuarios"))
    private Usuario usuario;

    @Column(length = 500)
    private String urlFotoPerfil;

    @Column(nullable = false)
    private int puestoRanking;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private int aniosExperiencia;

    @Column(length = 500)
    private String serviciosOfrecidos;

    @Column(length = 120)
    private String zonaTrabajo;

    @Column(length = 300)
    private String disponibilidad;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioHoraOrientativo;

    public PerfilTrabajador(
            Usuario usuario,
            String urlFotoPerfil,
            String descripcion,
            int aniosExperiencia,
            String serviciosOfrecidos,
            String zonaTrabajo,
            String disponibilidad,
            BigDecimal precioHoraOrientativo) {
        this.usuario = usuario;
        this.urlFotoPerfil = urlFotoPerfil;
        this.puestoRanking = 0;
        this.descripcion = descripcion;
        this.aniosExperiencia = aniosExperiencia;
        this.serviciosOfrecidos = serviciosOfrecidos;
        this.zonaTrabajo = zonaTrabajo;
        this.disponibilidad = disponibilidad;
        this.precioHoraOrientativo = precioHoraOrientativo;
    }

    void asignarIdSiNoTiene(long idTrabajador) {
        if (this.idTrabajador == null) {
            this.idTrabajador = idTrabajador;
        }
    }

    public void editar(
            String urlFotoPerfil,
            String descripcion,
            int aniosExperiencia,
            String serviciosOfrecidos,
            String zonaTrabajo,
            String disponibilidad,
            BigDecimal precioHoraOrientativo) {
        this.urlFotoPerfil = urlFotoPerfil;
        this.descripcion = descripcion;
        this.aniosExperiencia = aniosExperiencia;
        this.serviciosOfrecidos = serviciosOfrecidos;
        this.zonaTrabajo = zonaTrabajo;
        this.disponibilidad = disponibilidad;
        this.precioHoraOrientativo = precioHoraOrientativo;
    }
}
