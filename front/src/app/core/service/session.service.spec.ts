import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionService } from './session.service';
import { SessionInformation } from '../models/sessionInformation.interface';

const mockUser: SessionInformation = {
  token: 'tok',
  type: 'Bearer',
  id: 1,
  username: 'a@a.com',
  firstName: 'Alice',
  lastName: 'Doe',
  admin: false,
};

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have isLogged set to false by default', () => {
    expect(service.isLogged).toBe(false);
  });

  it('should have sessionInformation undefined by default', () => {
    expect(service.sessionInformation).toBeUndefined();
  });

  describe('logIn', () => {
    it('should set isLogged to true', () => {
      service.logIn(mockUser);
      expect(service.isLogged).toBe(true);
    });

    it('should store sessionInformation', () => {
      service.logIn(mockUser);
      expect(service.sessionInformation).toEqual(mockUser);
    });
  });

  describe('logOut', () => {
    beforeEach(() => service.logIn(mockUser));

    it('should set isLogged to false', () => {
      service.logOut();
      expect(service.isLogged).toBe(false);
    });

    it('should clear sessionInformation', () => {
      service.logOut();
      expect(service.sessionInformation).toBeUndefined();
    });
  });

  describe('$isLogged', () => {
    it('should emit false initially', (done) => {
      service.$isLogged().subscribe((val) => {
        expect(val).toBe(false);
        done();
      });
    });

    it('should emit true after logIn', (done) => {
      let emitCount = 0;
      service.$isLogged().subscribe((val) => {
        emitCount++;
        if (emitCount === 2) {
          expect(val).toBe(true);
          done();
        }
      });
      service.logIn(mockUser);
    });

    it('should emit false after logOut', (done) => {
      let emitCount = 0;
      service.$isLogged().subscribe((val) => {
        emitCount++;
        if (emitCount === 3) {
          expect(val).toBe(false);
          done();
        }
      });
      service.logIn(mockUser);
      service.logOut();
    });
  });
});
