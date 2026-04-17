import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 },
    logOut: jest.fn(),
  };

  const mockUserService = {
    getById: jest
      .fn()
      .mockReturnValue(
        of({
          id: 1,
          admin: true,
          email: 'test@test.com',
          firstName: 'John',
          lastName: 'Doe',
          createdAt: new Date(),
          updatedAt: new Date(),
        }),
      ),
    delete: jest.fn().mockReturnValue(of(void 0)),
  };

  const mockRouter = { navigate: jest.fn() };

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [MeComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("ngOnInit() doit charger les données de l'utilisateur connecté", () => {
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toBeDefined();
  });

  it('back() doit appeler window.history.back()', () => {
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('delete() doit supprimer le compte, afficher un message, déconnecter et rediriger', () => {
    const snackBar = fixture.debugElement.injector.get(MatSnackBar);
    const openSpy = jest.spyOn(snackBar, 'open').mockReturnValue({} as any);

    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(openSpy).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 },
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
