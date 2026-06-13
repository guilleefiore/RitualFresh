package com.ritualfresh.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sesiones_usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_sesiones_usuario_token", columnNames = "token"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SesionUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSesionUsuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sesiones_usuario_usuarios"))
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String token;

    @Column(nullable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaHoraExpiracion;

    private LocalDateTime fechaHoraCierre;

    public SesionUsuario(Usuario usuario, String token, LocalDateTime fechaHoraCreacion, LocalDateTime fechaHoraExpiracion) {
        this.usuario = usuario;
        this.token = token;
        this.fechaHoraCreacion = fechaHoraCreacion;
        this.fechaHoraExpiracion = fechaHoraExpiracion;
    }

    void asignarIdSiNoTiene(long idSesionUsuario) {
        if (this.idSesionUsuario == null) {
            this.idSesionUsuario = idSesionUsuario;
        }
    }

    public boolean estaVigente(LocalDateTime fechaHoraActual) {
        return fechaHoraCierre == null && fechaHoraExpiracion.isAfter(fechaHoraActual);
    }

    public void cerrar(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }
}
