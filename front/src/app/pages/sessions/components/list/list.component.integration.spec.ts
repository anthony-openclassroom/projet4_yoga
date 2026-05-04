import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { provideRouter } from '@angular/router';

import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { Session } from '../../../../core/models/session.interface';
import { ListComponent } from './list.component';

describe('ListComponent — Integration', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let httpMock: HttpTestingController;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'user@test.com',
      firstName: 'John',
      lastName: 'Doe',
    },
  };

  const mockSessions: Session[] = [
    { id: 1, name: 'Morning Yoga', description: 'Séance du matin', date: new Date('2024-01-15'), teacher_id: 1, users: [] },
    { id: 2, name: 'Evening Flow', description: 'Séance du soir', date: new Date('2024-01-16'), teacher_id: 2, users: [1, 2] },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListComponent, HttpClientTestingModule],
      providers: [
        provideRouter([]),
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('doit créer le composant avec le vrai SessionApiService', () => {
    component.sessions$.subscribe();
    httpMock.expectOne('api/session').flush([]);
    expect(component).toBeTruthy();
  });

  it('sessions$ doit envoyer un GET sur api/session', () => {
    component.sessions$.subscribe();
    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('sessions$ doit retourner la liste des sessions reçues de l\'API', (done) => {
    component.sessions$.subscribe(sessions => {
      expect(sessions).toHaveLength(2);
      expect(sessions[0].name).toBe('Morning Yoga');
      expect(sessions[1].users).toHaveLength(2);
      done();
    });
    httpMock.expectOne('api/session').flush(mockSessions);
  });

  it('sessions$ doit retourner une liste vide si l\'API répond []', (done) => {
    component.sessions$.subscribe(sessions => {
      expect(sessions).toHaveLength(0);
      done();
    });
    httpMock.expectOne('api/session').flush([]);
  });

  it('user doit exposer les informations de session via SessionService', () => {
    component.sessions$.subscribe();
    httpMock.expectOne('api/session').flush([]);
    expect(component.user?.admin).toBe(true);
    expect(component.user?.username).toBe('user@test.com');
  });

  it('sessions$ doit retourner les ids des participants pour chaque session', (done) => {
    component.sessions$.subscribe(sessions => {
      expect(sessions[0].users).toEqual([]);
      expect(sessions[1].users).toEqual([1, 2]);
      done();
    });
    httpMock.expectOne('api/session').flush(mockSessions);
  });
});
