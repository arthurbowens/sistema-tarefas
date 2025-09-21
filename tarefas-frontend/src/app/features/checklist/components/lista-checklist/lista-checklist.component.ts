import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChecklistItem } from '../../../../core/models/tarefa.model';
import { TarefaService } from '../../../../core/services/tarefa.service';
import { ItemChecklistComponent } from '../item-checklist/item-checklist.component';

@Component({
  selector: 'app-lista-checklist',
  standalone: true,
  imports: [CommonModule, FormsModule, ItemChecklistComponent],
  templateUrl: './lista-checklist.component.html',
  styleUrls: ['./lista-checklist.component.scss']
})
export class ListaChecklistComponent implements OnInit {
  @Input() tarefaId!: number;
  @Input() checklist: ChecklistItem[] = [];
  @Output() checklistAtualizado = new EventEmitter<ChecklistItem[]>();

  isAdicionando = false;
  novoTitulo = '';
  novaDescricao = '';
  isLoading = false;

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {}

  iniciarAdicao(): void {
    this.isAdicionando = true;
    this.novoTitulo = '';
    this.novaDescricao = '';
  }

  adicionarItem(): void {
    if (this.novoTitulo.trim()) {
      this.isLoading = true;
      this.tarefaService.criarChecklistItem(this.tarefaId, {
        titulo: this.novoTitulo.trim(),
        descricao: this.novaDescricao.trim() || undefined
      }).subscribe({
        next: (novoItem) => {
          this.checklist = [...this.checklist, novoItem];
          this.checklistAtualizado.emit(this.checklist);
          this.cancelarAdicao();
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  cancelarAdicao(): void {
    this.isAdicionando = false;
    this.novoTitulo = '';
    this.novaDescricao = '';
  }

  onItemAtualizado(itemAtualizado: ChecklistItem): void {
    const index = this.checklist.findIndex(item => item.id === itemAtualizado.id);
    if (index !== -1) {
      this.checklist[index] = itemAtualizado;
      this.checklistAtualizado.emit([...this.checklist]);
    }
  }

  onItemExcluido(itemId: number): void {
    this.checklist = this.checklist.filter(item => item.id !== itemId);
    this.checklistAtualizado.emit([...this.checklist]);
  }

  getProgressoChecklist(): number {
    if (this.checklist.length === 0) return 0;
    const concluidos = this.checklist.filter(item => item.concluido).length;
    return Math.round((concluidos / this.checklist.length) * 100);
  }

  getItensConcluidos(): number {
    return this.checklist.filter(item => item.concluido).length;
  }
}
