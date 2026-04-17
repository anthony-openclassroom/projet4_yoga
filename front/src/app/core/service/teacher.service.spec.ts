import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TeacherService } from './teacher.service';
import { Teacher } from '../models/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpClient: jest.Mocked<Pick<HttpClient, 'get'>>;

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Smith',
    firstName: 'Jane',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    httpClient = { get: jest.fn() };
    service = new TeacherService(httpClient as unknown as HttpClient);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all should call GET api/teacher and return teachers', (done) => {
    (httpClient.get as jest.Mock).mockReturnValue(of([mockTeacher]));

    service.all().subscribe((result) => {
      expect(result).toEqual([mockTeacher]);
      done();
    });

    expect(httpClient.get).toHaveBeenCalledWith('api/teacher');
  });

  it('detail should call GET api/teacher/:id and return teacher', (done) => {
    (httpClient.get as jest.Mock).mockReturnValue(of(mockTeacher));

    service.detail('1').subscribe((result) => {
      expect(result).toEqual(mockTeacher);
      done();
    });

    expect(httpClient.get).toHaveBeenCalledWith('api/teacher/1');
  });
});
