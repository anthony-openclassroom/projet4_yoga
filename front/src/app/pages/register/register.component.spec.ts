import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect, jest } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const mockRouter = { navigate: jest.fn() };
  const mockAuthService = {
    register: jest.fn().mockReturnValue(of(void 0)),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submit() doit appeler authService.register et naviguer vers /login en cas de succès', () => {
    mockAuthService.register.mockReturnValue(of(void 0));

    component.submit();

    expect(mockAuthService.register).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it("submit() doit passer onError à true en cas d'erreur", () => {
    mockAuthService.register.mockReturnValue(
      throwError(() => new Error('Email already taken')),
    );

    component.submit();

    expect(component.onError).toBe(true);
  });
});
