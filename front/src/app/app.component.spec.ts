import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';
import { AuthService } from './core/service/auth.service';
import { SessionService } from './core/service/session.service';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  const mockSessionService = {
    $isLogged: jest.fn().mockReturnValue(of(false)),
    logOut: jest.fn(),
    sessionInformation: undefined,
  };
  const mockRouter = { navigate: jest.fn() };
  const mockAuthService = {};

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('$isLogged() doit retourner un Observable depuis SessionService', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    let result = true;
    app.$isLogged().subscribe((v) => (result = v));
    expect(result).toBe(false);
    expect(mockSessionService.$isLogged).toHaveBeenCalled();
  });

  it('logout() doit appeler sessionService.logOut() et naviguer vers la racine', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.logout();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });
});
