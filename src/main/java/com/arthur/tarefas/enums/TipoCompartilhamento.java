package com.arthur.tarefas.enums;

public enum TipoCompartilhamento {
    LEITURA("Apenas Leitura"),
    ESCRITA("Leitura e Escrita"),
    ADMINISTRADOR("Administrador");

    private final String descricao;

    TipoCompartilhamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
