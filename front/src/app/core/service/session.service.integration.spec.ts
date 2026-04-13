import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('SessionService — Integration', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: 'fake-jwt',
    type: 'Bearer',
    id: 1,
    username: 'user@test.com',
    firstName: 'Alice',
    lastName: 'Dupont',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('logIn() doit mettre isLogged à true et stocker les informations de session', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('logOut() doit remettre isLogged à false et effacer les informations de session', () => {
    service.logIn(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('$isLogged() doit émettre les bonnes valeurs lors du logIn puis du logOut', done => {
    const emitted: boolean[] = [];

    service.$isLogged().subscribe(value => emitted.push(value));

    service.logIn(mockUser);
    service.logOut();

    expect(emitted).toEqual([false, true, false]);
    done();
  });
});
