import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';
import { SessionService } from '../../core/service/session.service';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 },
    logIn: jest.fn(),
  };
  const mockRouter = { navigate: jest.fn() };
  const mockAuthService = {
    login: jest.fn().mockReturnValue(of({ token: '', type: '', id: 1, username: '', firstName: '', lastName: '', admin: false })),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submit() doit appeler logIn et naviguer vers /sessions en cas de succès', () => {
    const sessionInfo = { token: 'tok', type: 'Bearer', id: 1, username: 'u@test.com', firstName: 'A', lastName: 'B', admin: false };
    mockAuthService.login.mockReturnValue(of(sessionInfo));

    component.submit();

    expect(mockAuthService.login).toHaveBeenCalled();
    expect(mockSessionService.logIn).toHaveBeenCalledWith(sessionInfo);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('submit() doit passer onError à true en cas d\'erreur', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('Unauthorized')));

    component.submit();

    expect(component.onError).toBe(true);
  });
});
