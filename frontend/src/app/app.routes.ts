import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { KnowledgeBase } from './knowledge-base/knowledge-base';
import { RequirementAssistant } from './requirement-assistant/requirement-assistant';
import { TestGenerator } from './test-generator/test-generator';
import { DefectTriage } from './defect-triage/defect-triage';
import { ReleaseNotes } from './release-notes/release-notes';
import { AuditHistory } from './audit-history/audit-history';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard },
  { path: 'knowledge-base', component: KnowledgeBase },
  { path: 'requirements', component: RequirementAssistant },
  { path: 'test-generator', component: TestGenerator },
  { path: 'defect-triage', component: DefectTriage },
  { path: 'release-notes', component: ReleaseNotes },
  { path: 'audit-history', component: AuditHistory },
  { path: '**', redirectTo: 'dashboard' }
];
