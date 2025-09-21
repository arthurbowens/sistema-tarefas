package com.arthur.tarefas.enums;

public enum CategoriaTarefa {
    TRABALHO("Trabalho"),
    PESSOAL("Pessoal"),
    ESTUDO("Estudo"),
    SAUDE("Saúde"),
    FINANCAS("Finanças"),
    CASA("Casa"),
    VIAGEM("Viagem"),
    HOBBIES("Hobbies"),
    FAMILIA("Família"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
