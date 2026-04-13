import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { AuthService } from './auth.service';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('AuthService — Integration', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('login() doit envoyer un POST sur /api/auth/login et retourner les infos de session', () => {
    const loginRequest: LoginRequest = { email: 'user@test.com', password: 'password123' };
    const mockResponse: SessionInformation = {
      token: 'fake-jwt',
      type: 'Bearer',
      id: 1,
      username: 'user@test.com',
      firstName: 'Alice',
      lastName: 'Dupont',
      admin: false,
    };

    service.login(loginRequest).subscribe(response => {
      expect(response.token).toBe('fake-jwt');
      expect(response.username).toBe('user@test.com');
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginRequest);
    req.flush(mockResponse);
  });

  it('register() doit envoyer un POST sur /api/auth/register avec les données d\'inscription', () => {
    const registerRequest: RegisterRequest = {
      email: 'new@test.com',
      firstName: 'Bob',
      lastName: 'Martin',
      password: 'securePass!',
    };

    service.register(registerRequest).subscribe();

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });
});
