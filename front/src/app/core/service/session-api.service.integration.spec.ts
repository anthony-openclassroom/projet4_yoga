import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../models/session.interface';

describe('SessionApiService — Integration', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Morning Yoga',
    description: 'A relaxing session',
    date: new Date('2024-01-15'),
    teacher_id: 2,
    users: [10, 11],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('all() doit envoyer un GET sur api/session et retourner la liste', () => {
    const mockList: Session[] = [mockSession];

    service.all().subscribe(sessions => {
      expect(sessions.length).toBe(1);
      expect(sessions[0].name).toBe('Morning Yoga');
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });

  it('detail() doit envoyer un GET sur api/session/:id', () => {
    service.detail('1').subscribe(session => {
      expect(session.id).toBe(1);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('create() doit envoyer un POST sur api/session avec le body', () => {
    const newSession: Session = { name: 'Evening Flow', description: 'Calm', date: new Date(), teacher_id: 3, users: [] };

    service.create(newSession).subscribe(session => {
      expect(session.name).toBe('Evening Flow');
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.name).toBe('Evening Flow');
    req.flush({ ...newSession, id: 2 });
  });

  it('update() doit envoyer un PUT sur api/session/:id avec le body', () => {
    const updated: Session = { ...mockSession, name: 'Updated Yoga' };

    service.update('1', updated).subscribe(session => {
      expect(session.name).toBe('Updated Yoga');
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.name).toBe('Updated Yoga');
    req.flush(updated);
  });

  it('delete() doit envoyer un DELETE sur api/session/:id', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('participate() doit envoyer un POST sur api/session/:id/participate/:userId', () => {
    service.participate('1', '42').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/42');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('unParticipate() doit envoyer un DELETE sur api/session/:id/participate/:userId', () => {
    service.unParticipate('1', '42').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/42');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
