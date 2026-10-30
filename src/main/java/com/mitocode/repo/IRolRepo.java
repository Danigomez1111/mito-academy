package com.mitocode.repo;

import com.mitocode.model.Rol;

import java.util.Optional;

public interface IRolRepo extends IGenericRepo<Rol, Integer> {

    Optional<Rol> findOneByNombre(String nombre);
}
