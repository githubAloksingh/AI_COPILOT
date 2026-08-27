import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../core/api';

@Component({
  selector: 'app-audit-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-history.html',
  styleUrl: './audit-history.scss'
})
export class AuditHistory implements OnInit {
  logs: any[] = [];
  loading = true;

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.api.getAuditLogs().subscribe({
      next: (res) => {
        if (res.success) {
          this.logs = (res.data || []).map((log: any) => {
            let parsed = null;
            if (log.output) {
              try {
                let str = log.output.trim();
                if (str.startsWith("```json")) str = str.substring(7);
                if (str.startsWith("```")) str = str.substring(3);
                if (str.endsWith("```")) str = str.substring(0, str.length() - 3);
                parsed = JSON.parse(str.trim());
              } catch (e) {
                parsed = null;
              }
            }
            return {
              ...log,
              parsedOutput: parsed,
              isArrayOutput: Array.isArray(parsed)
            };
          });
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  formatDate(timestamp: string) {
    if (!timestamp) return '';
    const d = new Date(timestamp);
    return `${d.toLocaleDateString()} ${d.toLocaleTimeString()}`;
  }

  toggleExpand(log: any) {
    log.expanded = !log.expanded;
    this.cdr.markForCheck();
  }
}
