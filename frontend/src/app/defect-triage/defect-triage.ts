import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api';

@Component({
  selector: 'app-defect-triage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './defect-triage.html'
})
export class DefectTriage {
  title = '';
  description = '';
  logs = '';
  environment = '';
  stepsToReproduce = '';
  expectedBehavior = '';
  actualBehavior = '';
  
  loading = false;
  error = '';
  result: any = null;
  feedbackGiven = false;

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

  generate() {
    if (!this.title || !this.description) return;
    
    this.loading = true;
    this.error = '';
    this.result = null;
    this.feedbackGiven = false;
    this.cdr.markForCheck();

    this.api.analyzeDefect({
      title: this.title,
      description: this.description,
      logs: this.logs,
      environment: this.environment,
      stepsToReproduce: this.stepsToReproduce,
      expectedBehavior: this.expectedBehavior,
      actualBehavior: this.actualBehavior
    }).subscribe({
      next: (res) => {
        if (res.success) {
          this.result = res.data;
        } else {
          this.error = res.message || 'Analysis failed';
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'We couldn\'t analyze the defect. Please try again.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  copy() {
    navigator.clipboard.writeText(JSON.stringify(this.result, null, 2));
    alert('Copied to clipboard');
  }

  submitFeedback(status: string) {
    if (!this.result?.id) return;
    this.api.submitFeedback({
      referenceId: this.result.id,
      referenceType: 'DEFECT',
      status: status
    }).subscribe({
      next: () => {
        this.feedbackGiven = true;
        this.cdr.markForCheck();
      }
    });
  }
}
