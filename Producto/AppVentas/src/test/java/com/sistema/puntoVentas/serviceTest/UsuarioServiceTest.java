package com.sistema.puntoVentas.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sistema.puntoventas.modelo.AuditoriaEvento;
import com.sistema.puntoventas.modelo.Role;
import com.sistema.puntoventas.modelo.Usuario;
import com.sistema.puntoventas.repository.IUsuarioRepository;
import com.sistema.puntoventas.repository.impl.VentaRepositoryimpl;
import com.sistema.puntoventas.service.AuditoriaService;
import com.sistema.puntoventas.service.UsuarioService;
import com.sistema.puntoventas.util.Encriptador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock private IUsuarioRepository usuarioRepository;
    @Mock private AuditoriaService auditoriaService;
    @Mock private VentaRepositoryimpl ventaRepository;
    private UsuarioService usuarioService;

    private Usuario usuarioValido;

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, auditoriaService, ventaRepository);
        usuarioValido = Usuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .rut("19.876.543-2")
                .contraseña("clave123")
                .rol(Role.VENDEDOR)
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("TC-01: Camino Feliz - Registro exitoso")
    public void testTC01_RegistroUsuarioExitoso() {
        when(usuarioRepository.registrarUsuario(any(Usuario.class))).thenReturn(true);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        String resultado = usuarioService.registrarNuevoUsuario(usuarioValido);

        assertEquals("Usuario registrado exitosamente", resultado);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).registrarUsuario(usuarioCaptor.capture());

        String hashEsperado = Encriptador.hashPassword("clave123");
        assertEquals(hashEsperado, usuarioCaptor.getValue().getContraseña());
        assertEquals(64, usuarioCaptor.getValue().getContraseña().length());
    }

    @Test
    @DisplayName("TC-02: Registro con Usuario Nulo")
    public void testTC02_RegistroUsuarioNulo() {
        String resultado = usuarioService.registrarNuevoUsuario(null);
        assertEquals("El usuario no puede ser nulo.", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-03: Registro con Nombre Vacío")
    public void testTC03_RegistroNombreVacio() {
        usuarioValido.setNombre("   ");
        String resultado = usuarioService.registrarNuevoUsuario(usuarioValido);
        assertEquals("El nombre del usuario es obligatorio.", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-04: Registro con Apellido Vacío")
    public void testTC04_RegistroApellidoVacio() {
        usuarioValido.setApellido("");
        String resultado = usuarioService.registrarNuevoUsuario(usuarioValido);
        assertEquals("El apellido del usuario es obligatorio.", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-05: Registro con RUT Vacío")
    public void testTC05_RegistroRutVacio() {
        usuarioValido.setRut(" ");
        String resultado = usuarioService.registrarNuevoUsuario(usuarioValido);
        assertEquals("El RUT del usuario es obligatorio.", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-06: Registro con Contraseña Vacía")
    public void testTC06_RegistroContrasenaVacia() {
        usuarioValido.setContraseña("");
        String resultado = usuarioService.registrarNuevoUsuario(usuarioValido);
        assertEquals("La contraseña del usuario es obligatoria.", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-07: Camino Feliz - Actualización exitosa")
    public void testTC07_ActualizarUsuarioExitoso() {
        when(usuarioRepository.actualizarUsuario(any(Usuario.class))).thenReturn(true);

        String resultado = usuarioService.actualizarUsuario(usuarioValido);

        assertEquals("Usuario actualizado exitosamente", resultado);
        verify(usuarioRepository, times(1)).actualizarUsuario(any(Usuario.class));
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
    }

    @Test
    @DisplayName("TC-08: Actualización sin RUT")
    public void testTC08_ActualizarUsuarioSinRut() {
        usuarioValido.setRut(null);
        String resultado = usuarioService.actualizarUsuario(usuarioValido);
        assertEquals("El RUT es obligatorio para actualizar", resultado);
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-09: Login Exitoso - Administrador Maestro")
    public void testTC09_LoginAdminMaestro() {
        String rutAdmin = "12.345.678-9";
        String clave = "admin123";
        String hash = Encriptador.hashPassword(clave);

        Usuario mockUser = Usuario.builder().rut(rutAdmin).contraseña(hash).build();
        when(usuarioRepository.iniciarSesion(rutAdmin, hash)).thenReturn(mockUser);

        Usuario resultado = usuarioService.iniciarSesion(rutAdmin, clave);

        assertNotNull(resultado);
        assertEquals(Role.ADMIN, resultado.getRol());
    }

    @Test
    @DisplayName("TC-10: Login Exitoso - Vendedor Maestro")
    public void testTC10_LoginVendedorMaestro() {
        String rutVendedor = "23.456.789-0";
        String clave = "vend123";
        String hash = Encriptador.hashPassword(clave);

        Usuario mockUser = Usuario.builder().rut(rutVendedor).contraseña(hash).build();
        when(usuarioRepository.iniciarSesion(rutVendedor, hash)).thenReturn(mockUser);

        Usuario resultado = usuarioService.iniciarSesion(rutVendedor, clave);

        assertNotNull(resultado);
        assertEquals(Role.VENDEDOR, resultado.getRol());
    }

    @Test
    @DisplayName("TC-11: Login con Credenciales Incorrectas")
    public void testTC11_LoginCredencialesIncorrectas() {
        String hash = Encriptador.hashPassword("wrongPass");
        when(usuarioRepository.iniciarSesion("11.111.111-1", hash)).thenReturn(null);

        Usuario resultado = usuarioService.iniciarSesion("11.111.111-1", "wrongPass");

        assertNull(resultado);
    }

    @Test
    @DisplayName("TC-12: Login con Campos Vacíos")
    public void testTC12_LoginCamposVacios() {
        assertNull(usuarioService.iniciarSesion("", "123"));
        assertNull(usuarioService.iniciarSesion("11.111.111-1", null));
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("TC-13: Camino Feliz - Eliminación exitosa")
    public void testTC13_EliminarUsuarioExitoso() {
        String rut = "19.876.543-2";
        when(usuarioRepository.obtenerUsuarioPorRut(rut)).thenReturn(usuarioValido);
        when(ventaRepository.obtenerVentas()).thenReturn(Collections.emptyList());
        when(usuarioRepository.eliminarUsuario(rut)).thenReturn(usuarioValido);
        when(auditoriaService.registrarEvento(any(AuditoriaEvento.class))).thenReturn(true);

        Usuario resultado = usuarioService.eliminarUsuario(rut);

        assertNotNull(resultado);
        assertEquals(usuarioValido.getNombre(), resultado.getNombre());
        verify(usuarioRepository, times(1)).eliminarUsuario(rut);
        verify(auditoriaService, times(1)).registrarEvento(any(AuditoriaEvento.class));
    }

    @Test
    @DisplayName("TC-14: Eliminación con RUT Inválido")
    public void testTC14_EliminarUsuarioRutInvalido() {
        assertNull(usuarioService.eliminarUsuario(null));
        assertNull(usuarioService.eliminarUsuario(""));
        verifyNoInteractions(usuarioRepository, auditoriaService);
    }

    @Test
    @DisplayName("TC-15: Persistencia Inmutable del Hash")
    public void testTC15_PersistenciaHashInmutable() {
        when(usuarioRepository.registrarUsuario(any(Usuario.class))).thenReturn(true);
        usuarioService.registrarNuevoUsuario(usuarioValido);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).registrarUsuario(captor.capture());

        String hash = captor.getValue().getContraseña();
        assertTrue(hash.matches("^[a-f0-9]{64}$"));
    }
}