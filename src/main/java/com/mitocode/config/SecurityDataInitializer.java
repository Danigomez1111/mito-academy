package com.mitocode.config;

import com.mitocode.model.Rol;
import com.mitocode.model.Usuario;
import com.mitocode.repo.IRolRepo;
import com.mitocode.repo.IUsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    private final IRolRepo rolRepo;
    private final IUsuarioRepo usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Rol adminRol = rolRepo.findOneByNombre("ADMIN")
                .orElseGet(() -> rolRepo.save(new Rol(null, "ADMIN", "Administrador del sistema")));

        Rol userRol = rolRepo.findOneByNombre("USER")
                .orElseGet(() -> rolRepo.save(new Rol(null, "USER", "Usuario estándar")));

        if (usuarioRepo.findOneByUsername("admin") == null) {
            Usuario admin = new Usuario(
                    null,
                    "admin",
                    passwordEncoder.encode("123"),
                    adminRol,
                    true
            );
            usuarioRepo.save(admin);
        }

        if (usuarioRepo.findOneByUsername("mito") == null) {
            Usuario user = new Usuario(
                    null,
                    "mito",
                    passwordEncoder.encode("123"),
                    userRol,
                    true
            );
            usuarioRepo.save(user);
        }
    }
}
