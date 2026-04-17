import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';

import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 },
  };

  const mockSession = {
    id: 1,
    name: 'Test',
    description: 'desc',
    date: new Date(),
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockTeacher = {
    id: 1,
    lastName: 'Doe',
    firstName: 'John',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    delete: jest.fn().mockReturnValue(of(void 0)),
    participate: jest.fn().mockReturnValue(of(void 0)),
    unParticipate: jest.fn().mockReturnValue(of(void 0)),
  };

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(mockTeacher)),
  };

  const mockRouter = { navigate: jest.fn() };

  const mockActivatedRoute = {
    snapshot: { paramMap: { get: () => '1' } },
  };

  beforeEach(async () => {
    jest.clearAllMocks();
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

  it('ngOnInit() doit charger la session et le professeur', () => {
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('back() doit appeler window.history.back()', () => {
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('delete() doit supprimer la session, afficher un message et rediriger', () => {
    const snackBar = fixture.debugElement.injector.get(MatSnackBar);
    const openSpy = jest.spyOn(snackBar, 'open').mockReturnValue({} as any);

    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(openSpy).toHaveBeenCalledWith('Session deleted !', 'Close', {
      duration: 3000,
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('participate() doit appeler sessionApiService.participate et recharger la session', () => {
    component.participate();
    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
  });

  it('unParticipate() doit appeler sessionApiService.unParticipate et recharger la session', () => {
    component.unParticipate();
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
  });
});
