package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.UsuarioDTO;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setNome(usuarioDTO.getNome());
        usuario.setSobrenome(usuarioDTO.getSobrenome());
        usuario.setAtivo(true);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioSalvo);
    }
    
    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.setNome(usuarioDTO.getNome());
        usuario.setSobrenome(usuarioDTO.getSobrenome());
        usuario.setUltimoAcesso(LocalDateTime.now());
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioAtualizado);
    }
    
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return converterParaDTO(usuario);
    }
    
    public UsuarioDTO buscarPorUuid(String uuid) {
        Usuario usuario = usuarioRepository.findByUuidAndAtivoTrue(uuid)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return converterParaDTO(usuario);
    }
    
    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return converterParaDTO(usuario);
    }
    
    
    public List<UsuarioDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContaining(nome)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<UsuarioDTO> buscarUsuariosPorEmail(String email) {
        return usuarioRepository.findByEmailContainingAndAtivoTrue(email)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public void desativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    
    
    public UsuarioDTO converterParaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUuid(usuario.getUuid());
        dto.setEmail(usuario.getEmail());
        // NUNCA retornar a senha - segurança
        dto.setSenha(null);
        dto.setNome(usuario.getNome());
        dto.setSobrenome(usuario.getSobrenome());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setUltimoAcesso(usuario.getUltimoAcesso());
        dto.setAtivo(usuario.isAtivo());
        return dto;
    }
}
