package ritualfresh.usuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_mail", columnNames = "mail"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, length = 20)
    private String dni;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String mail;

    @Column(nullable = false, length = 100)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCuenta estadoCuenta;

    @Column(nullable = false)
    private LocalDateTime fechaHoraCreacionPerfil;

    private LocalDateTime fechaHoraBaja;

    @Column(length = 80)
    private String tokenValidacionCuenta;

    @Column(length = 80)
    private String tokenRecuperacionContrasena;

    private LocalDateTime fechaHoraExpiracionRecuperacionContrasena;

    public Usuario(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String mail,
            String contrasenaHash,
            RolUsuario rol,
            String tokenValidacionCuenta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.mail = mail;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
        this.estadoCuenta = EstadoCuenta.PENDIENTE_VALIDACION;
        this.fechaHoraCreacionPerfil = LocalDateTime.now();
        this.tokenValidacionCuenta = tokenValidacionCuenta;
    }

    void asignarIdSiNoTiene(long idUsuario) {
        if (this.idUsuario == null) {
            this.idUsuario = idUsuario;
        }
    }

    public boolean estaActivo() {
        return estadoCuenta == EstadoCuenta.ACTIVA;
    }

    public void validarCuenta() {
        this.estadoCuenta = EstadoCuenta.ACTIVA;
        this.tokenValidacionCuenta = null;
    }

    public void editarDatos(String nombre, String apellido, String dni, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
    }

    public void cambiarContrasena(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
        this.tokenRecuperacionContrasena = null;
        this.fechaHoraExpiracionRecuperacionContrasena = null;
    }

    public void iniciarRecuperacionContrasena(String tokenRecuperacionContrasena, LocalDateTime fechaHoraExpiracion) {
        this.tokenRecuperacionContrasena = tokenRecuperacionContrasena;
        this.fechaHoraExpiracionRecuperacionContrasena = fechaHoraExpiracion;
    }

    public boolean tieneTokenRecuperacionVigente(LocalDateTime fechaHoraActual) {
        return tokenRecuperacionContrasena != null
                && fechaHoraExpiracionRecuperacionContrasena != null
                && fechaHoraExpiracionRecuperacionContrasena.isAfter(fechaHoraActual);
    }

    public void darDeBajaCuenta() {
        this.estadoCuenta = EstadoCuenta.BAJA;
        this.fechaHoraBaja = LocalDateTime.now();
    }
}
