import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../core/api';

@Component({
  selector: 'app-knowledge-base',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './knowledge-base.html'
})
export class KnowledgeBase implements OnInit {
  documents: any[] = [];
  loading = true;
  uploading = false;
  error = '';

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments() {
    this.loading = true;
    this.cdr.markForCheck();
    this.api.getDocuments().subscribe({
      next: (res) => {
        if (res.success) {
          this.documents = res.data;
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load documents';
        this.cdr.markForCheck();
      }
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.uploading = true;
      this.error = '';
      this.cdr.markForCheck();
      this.api.uploadDocument(file).subscribe({
        next: (res) => {
          if (res.success) {
            this.loadDocuments();
          } else {
            this.error = res.message || 'Upload failed';
          }
          this.uploading = false;
          event.target.value = ''; // Reset file input
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Failed to upload document';
          this.uploading = false;
          event.target.value = '';
          this.cdr.markForCheck();
        }
      });
    }
  }

  deleteDocument(id: number) {
    if (confirm('Are you sure you want to delete this document?')) {
      this.api.deleteDocument(id).subscribe({
        next: () => {
          this.loadDocuments();
          this.cdr.markForCheck();
        }
      });
    }
  }

  formatBytes(bytes: number, decimals = 2) {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  }
}
