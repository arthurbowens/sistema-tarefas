package com.arthur.tarefas.repository;

import com.arthur.tarefas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByUuid(String uuid);
    
    boolean existsByEmail(String email);
    
    boolean existsByUuid(String uuid);
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.ativo = true")
    Optional<Usuario> findByEmailAndAtivoTrue(@Param("email") String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.uuid = :uuid AND u.ativo = true")
    Optional<Usuario> findByUuidAndAtivoTrue(@Param("uuid") String uuid);
    
    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome% OR u.sobrenome LIKE %:nome%")
    java.util.List<Usuario> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT u FROM Usuario u WHERE u.email LIKE %:email% AND u.ativo = true")
    java.util.List<Usuario> findByEmailContainingAndAtivoTrue(@Param("email") String email);
}
