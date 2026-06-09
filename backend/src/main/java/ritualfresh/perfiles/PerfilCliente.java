package ritualfresh.perfiles;

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
import ritualfresh.usuarios.Usuario;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = @UniqueConstraint(name = "uk_clientes_usuario", columnNames = "usuario_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfilCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_clientes_usuarios"))
    private Usuario usuario;

    @Column(length = 500)
    private String urlFotoPerfil;

    @Column(nullable = false)
    private int clasificacionCliente;

    @Column(length = 30)
    private String telefonoContacto;

    @Column(length = 120)
    private String nombreCalle;

    @Column(length = 20)
    private String numeroDomicilio;

    @Column(length = 20)
    private String piso;

    @Column(length = 40)
    private String departamentoDomicilio;

    @Column(length = 12)
    private String codigoPostal;

    @Column(length = 80)
    private String localidad;

    @Column(length = 80)
    private String provincia;

    @Column(length = 500)
    private String preferenciasContratacion;

    public PerfilCliente(
            Usuario usuario,
            String urlFotoPerfil,
            String telefonoContacto,
            String nombreCalle,
            String numeroDomicilio,
            String piso,
            String departamentoDomicilio,
            String codigoPostal,
            String localidad,
            String provincia,
            String preferenciasContratacion) {
        this.usuario = usuario;
        this.urlFotoPerfil = urlFotoPerfil;
        this.clasificacionCliente = 0;
        this.telefonoContacto = telefonoContacto;
        this.nombreCalle = nombreCalle;
        this.numeroDomicilio = numeroDomicilio;
        this.piso = piso;
        this.departamentoDomicilio = departamentoDomicilio;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.preferenciasContratacion = preferenciasContratacion;
    }

    void asignarIdSiNoTiene(long idCliente) {
        if (this.idCliente == null) {
            this.idCliente = idCliente;
        }
    }

    public void editar(
            String urlFotoPerfil,
            String telefonoContacto,
            String nombreCalle,
            String numeroDomicilio,
            String piso,
            String departamentoDomicilio,
            String codigoPostal,
            String localidad,
            String provincia,
            String preferenciasContratacion) {
        this.urlFotoPerfil = urlFotoPerfil;
        this.telefonoContacto = telefonoContacto;
        this.nombreCalle = nombreCalle;
        this.numeroDomicilio = numeroDomicilio;
        this.piso = piso;
        this.departamentoDomicilio = departamentoDomicilio;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.preferenciasContratacion = preferenciasContratacion;
    }
}
