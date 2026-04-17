import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { SessionApiService } from './session-api.service';
import { Session } from '../models/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpClient: jest.Mocked<Pick<HttpClient, 'get' | 'post' | 'put' | 'delete'>>;

  const mockSession: Session = {
    id: 1,
    name: 'Morning Yoga',
    description: 'Relaxing session',
    date: new Date(),
    teacher_id: 1,
    users: [],
  };

  beforeEach(() => {
    httpClient = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    service = new SessionApiService(httpClient as unknown as HttpClient);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all should call GET api/session', (done) => {
    (httpClient.get as jest.Mock).mockReturnValue(of([mockSession]));

    service.all().subscribe((result) => {
      expect(result).toEqual([mockSession]);
      done();
    });

    expect(httpClient.get).toHaveBeenCalledWith('api/session');
  });

  it('detail should call GET api/session/:id', (done) => {
    (httpClient.get as jest.Mock).mockReturnValue(of(mockSession));

    service.detail('1').subscribe((result) => {
      expect(result).toEqual(mockSession);
      done();
    });

    expect(httpClient.get).toHaveBeenCalledWith('api/session/1');
  });

  it('delete should call DELETE api/session/:id', (done) => {
    (httpClient.delete as jest.Mock).mockReturnValue(of(undefined));

    service.delete('1').subscribe(() => done());

    expect(httpClient.delete).toHaveBeenCalledWith('api/session/1');
  });

  it('create should call POST api/session', (done) => {
    (httpClient.post as jest.Mock).mockReturnValue(of(mockSession));

    service.create(mockSession).subscribe((result) => {
      expect(result).toEqual(mockSession);
      done();
    });

    expect(httpClient.post).toHaveBeenCalledWith('api/session', mockSession);
  });

  it('update should call PUT api/session/:id', (done) => {
    (httpClient.put as jest.Mock).mockReturnValue(of(mockSession));

    service.update('1', mockSession).subscribe((result) => {
      expect(result).toEqual(mockSession);
      done();
    });

    expect(httpClient.put).toHaveBeenCalledWith('api/session/1', mockSession);
  });

  it('participate should call POST api/session/:id/participate/:userId', (done) => {
    (httpClient.post as jest.Mock).mockReturnValue(of(undefined));

    service.participate('1', '42').subscribe(() => done());

    expect(httpClient.post).toHaveBeenCalledWith('api/session/1/participate/42', null);
  });

  it('unParticipate should call DELETE api/session/:id/participate/:userId', (done) => {
    (httpClient.delete as jest.Mock).mockReturnValue(of(undefined));

    service.unParticipate('1', '42').subscribe(() => done());

    expect(httpClient.delete).toHaveBeenCalledWith('api/session/1/participate/42');
  });
});
