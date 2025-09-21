import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChecklistItem } from '../../../../core/models/tarefa.model';
import { TarefaService } from '../../../../core/services/tarefa.service';

@Component({
  selector: 'app-item-checklist',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './item-checklist.component.html',
  styleUrls: ['./item-checklist.component.scss']
})
export class ItemChecklistComponent implements OnInit {
  @Input() item!: ChecklistItem;
  @Input() tarefaId!: number;
  @Output() itemAtualizado = new EventEmitter<ChecklistItem>();
  @Output() itemExcluido = new EventEmitter<number>();

  isEditando = false;
  tituloEditado = '';
  descricaoEditada = '';
  isLoading = false;

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.tituloEditado = this.item.titulo;
    this.descricaoEditada = this.item.descricao || '';
  }

  toggleConcluido(): void {
    this.isLoading = true;
    this.tarefaService.marcarChecklistItemConcluido(this.tarefaId, this.item.id).subscribe({
      next: (itemAtualizado) => {
        this.item = itemAtualizado;
        this.itemAtualizado.emit(itemAtualizado);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  iniciarEdicao(): void {
    this.isEditando = true;
    this.tituloEditado = this.item.titulo;
    this.descricaoEditada = this.item.descricao || '';
  }

  salvarEdicao(): void {
    if (this.tituloEditado.trim()) {
      this.isLoading = true;
      this.tarefaService.atualizarChecklistItem(this.tarefaId, this.item.id, {
        titulo: this.tituloEditado.trim(),
        descricao: this.descricaoEditada.trim() || undefined
      }).subscribe({
        next: (itemAtualizado) => {
          this.item = itemAtualizado;
          this.itemAtualizado.emit(itemAtualizado);
          this.isEditando = false;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  cancelarEdicao(): void {
    this.isEditando = false;
    this.tituloEditado = this.item.titulo;
    this.descricaoEditada = this.item.descricao || '';
  }

  excluirItem(): void {
    if (confirm('Tem certeza que deseja excluir este item do checklist?')) {
      this.isLoading = true;
      this.tarefaService.excluirChecklistItem(this.tarefaId, this.item.id).subscribe({
        next: () => {
          this.itemExcluido.emit(this.item.id);
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }
}
