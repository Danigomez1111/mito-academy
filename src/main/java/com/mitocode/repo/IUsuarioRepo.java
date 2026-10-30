package com.mitocode.repo;

import com.mitocode.model.Usuario;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer> {

    Usuario findOneByUsername(String username);
}
