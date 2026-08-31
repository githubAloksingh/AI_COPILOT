import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../core/api';

@Component({
  selector: 'app-knowledge-base',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './knowledge-base.html'
})
export class KnowledgeBase implements OnInit, OnDestroy {
  documents: any[] = [];
  loading = true;
  uploading = false;
  error = '';
  private pollInterval: any = null;

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadDocuments();
  }

  ngOnDestroy() {
    this.stopPolling();
  }

  loadDocuments(silent = false) {
    if (!silent) {
      this.loading = true;
      this.cdr.markForCheck();
    }

    this.api.getDocuments().subscribe({
      next: (res) => {
        if (res.success) {
          this.documents = res.data || [];
          this.checkPollingNeeded();
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load documents';
        this.stopPolling();
        this.cdr.markForCheck();
      }
    });
  }

  private checkPollingNeeded() {
    const hasActiveProcessing = this.documents.some(
      doc => doc.status === 'PROCESSING' || doc.status === 'UPLOADING'
    );

    if (hasActiveProcessing) {
      if (!this.pollInterval) {
        this.pollInterval = setInterval(() => {
          this.loadDocuments(true);
        }, 3000);
      }
    } else {
      this.stopPolling();
    }
  }

  private stopPolling() {
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
      this.pollInterval = null;
    }
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
            this.loadDocuments(true);
          } else {
            this.error = res.message || 'Upload failed';
          }
          this.uploading = false;
          event.target.value = '';
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to upload document. Please check file size and format.';
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
