import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../models/teacher.interface';

describe('TeacherService — Integration', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Dupont',
    firstName: 'Marie',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-01'),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('all() doit envoyer un GET sur api/teacher et retourner la liste', () => {
    service.all().subscribe(teachers => {
      expect(teachers.length).toBe(1);
      expect(teachers[0].firstName).toBe('Marie');
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush([mockTeacher]);
  });

  it('detail() doit envoyer un GET sur api/teacher/:id', () => {
    service.detail('1').subscribe(teacher => {
      expect(teacher.id).toBe(1);
      expect(teacher.lastName).toBe('Dupont');
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeacher);
  });
});
