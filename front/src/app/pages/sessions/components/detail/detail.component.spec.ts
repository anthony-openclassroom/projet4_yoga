import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';

import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 }
  };

  const mockSession = {
    id: 1, name: 'Test', description: 'desc', date: new Date(),
    teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date()
  };

  const mockSessionApiService = {
    detail: () => of(mockSession),
    delete: () => of(void 0),
    participate: () => of(void 0),
    unParticipate: () => of(void 0),
  };

  const mockTeacherService = {
    detail: () => of({ id: 1, lastName: 'Doe', firstName: 'John', createdAt: new Date(), updatedAt: new Date() }),
  };

  const mockRouter = { navigate: () => {} };

  const mockActivatedRoute = {
    snapshot: { paramMap: { get: () => '1' } }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
