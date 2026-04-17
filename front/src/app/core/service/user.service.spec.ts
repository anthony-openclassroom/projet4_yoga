import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { User } from '../models/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpClient: jest.Mocked<Pick<HttpClient, 'get' | 'delete'>>;

  const mockUser: User = {
    id: 1,
    email: 'a@a.com',
    lastName: 'Doe',
    firstName: 'John',
    admin: false,
    password: 'encoded',
    createdAt: new Date(),
  };

  beforeEach(() => {
    httpClient = { get: jest.fn(), delete: jest.fn() };
    service = new UserService(httpClient as unknown as HttpClient);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getById should call GET api/user/:id and return user', (done) => {
    (httpClient.get as jest.Mock).mockReturnValue(of(mockUser));

    service.getById('1').subscribe((result) => {
      expect(result).toEqual(mockUser);
      done();
    });

    expect(httpClient.get).toHaveBeenCalledWith('api/user/1');
  });

  it('delete should call DELETE api/user/:id', (done) => {
    (httpClient.delete as jest.Mock).mockReturnValue(of(undefined));

    service.delete('1').subscribe(() => done());

    expect(httpClient.delete).toHaveBeenCalledWith('api/user/1');
  });
});
