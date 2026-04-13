import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { User } from '../models/user.interface';

describe('UserService — Integration', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    email: 'user@test.com',
    lastName: 'Dupont',
    firstName: 'Alice',
    admin: false,
    password: 'hashed',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-01'),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getById() doit envoyer un GET sur api/user/:id et retourner l\'utilisateur', () => {
    service.getById('1').subscribe(user => {
      expect(user.id).toBe(1);
      expect(user.email).toBe('user@test.com');
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('delete() doit envoyer un DELETE sur api/user/:id', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
