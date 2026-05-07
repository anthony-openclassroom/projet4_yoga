import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef, TextOnlySnackBar } from '@angular/material/snack-bar';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';

import { FormComponent } from './form.component';

const mockSession = {
  id: 1,
  name: 'Morning Yoga',
  description: 'A relaxing session',
  date: new Date('2024-01-15'),
  teacher_id: 2,
  users: [],
};

function buildProviders(url: string, admin: boolean) {
  return [
    {
      provide: SessionService,
      useValue: { sessionInformation: { admin, id: 1 } },
    },
    {
      provide: SessionApiService,
      useValue: {
        detail: jest.fn().mockReturnValue(of(mockSession)),
        create: jest.fn().mockReturnValue(of(mockSession)),
        update: jest.fn().mockReturnValue(of(mockSession)),
      },
    },
    {
      provide: TeacherService,
      useValue: { all: jest.fn().mockReturnValue(of([])) },
    },
    { provide: Router, useValue: { navigate: jest.fn(), url } },
    {
      provide: ActivatedRoute,
      useValue: { snapshot: { paramMap: { get: () => '1' } } },
    },
  ];
}

// ─── MODE CRÉATION ──────────────────────────────────────────────────────────

describe('FormComponent — mode création', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockRouter: { navigate: jest.Mock; url: string };
  let mockSessionApiService: { detail: jest.Mock; create: jest.Mock; update: jest.Mock };

  beforeEach(async () => {
    const providers = buildProviders('/sessions/create', true);
    mockRouter = providers[3].useValue as { navigate: jest.Mock; url: string };
    mockSessionApiService = providers[1].useValue as { detail: jest.Mock; create: jest.Mock; update: jest.Mock };

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers,
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit() doit initialiser un formulaire vide et laisser onUpdate à false', () => {
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm!.value.name).toBe('');
  });

  it('submit() doit appeler sessionApiService.create() et rediriger', () => {
    const snackBar = fixture.debugElement.injector.get(MatSnackBar);
    const openSpy = jest.spyOn(snackBar, 'open').mockReturnValue({} as MatSnackBarRef<TextOnlySnackBar>);

    component.sessionForm!.setValue({
      name: 'Test',
      date: '2024-01-15',
      teacher_id: 1,
      description: 'Desc',
    });
    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(openSpy).toHaveBeenCalledWith('Session created !', 'Close', {
      duration: 3000,
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });
});

// ─── MODE MODIFICATION ──────────────────────────────────────────────────────

describe('FormComponent — mode modification', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockRouter: { navigate: jest.Mock; url: string };
  let mockSessionApiService: { detail: jest.Mock; create: jest.Mock; update: jest.Mock };

  beforeEach(async () => {
    const providers = buildProviders('/sessions/update/1', true);
    mockRouter = providers[3].useValue as { navigate: jest.Mock; url: string };
    mockSessionApiService = providers[1].useValue as { detail: jest.Mock; create: jest.Mock; update: jest.Mock };

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers,
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('ngOnInit() doit charger la session existante et passer onUpdate à true', () => {
    expect(component.onUpdate).toBe(true);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm!.value.name).toBe('Morning Yoga');
  });

  it('submit() doit appeler sessionApiService.update() et rediriger', () => {
    const snackBar = fixture.debugElement.injector.get(MatSnackBar);
    const openSpy = jest.spyOn(snackBar, 'open').mockReturnValue({} as MatSnackBarRef<TextOnlySnackBar>);

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith(
      '1',
      expect.any(Object),
    );
    expect(openSpy).toHaveBeenCalledWith('Session updated !', 'Close', {
      duration: 3000,
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });
});

// ─── UTILISATEUR NON-ADMIN ──────────────────────────────────────────────────

describe('FormComponent — utilisateur non-admin', () => {
  let mockRouter: { navigate: jest.Mock; url: string };

  beforeEach(async () => {
    const providers = buildProviders('/sessions/create', false);
    mockRouter = providers[3].useValue as { navigate: jest.Mock; url: string };

    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers,
    }).compileComponents();

    const fixture = TestBed.createComponent(FormComponent);
    fixture.componentInstance;
    fixture.detectChanges();
  });

  it("ngOnInit() doit rediriger vers /sessions si l'utilisateur n'est pas admin", () => {
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});
