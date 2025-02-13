import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { LocalAuthGuard } from './local-auth.guard';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';

describe('AuthController', () => {
  let authController: AuthController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuthController],
      providers: [
        {
          provide: AuthService,
          useValue: {
            register: jest.fn().mockResolvedValue({
              username: 'testuser',
              password: 'hashedPassword',
            }),
            login: jest
              .fn()
              .mockResolvedValue({ access_token: 'mockJwtToken' }),
          },
        },
        {
          provide: JwtService,
          useValue: {
            sign: jest.fn().mockReturnValue('mockJwtToken'),
          },
        },
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn().mockReturnValue('testSecret'),
          },
        },
      ],
    })
      .overrideGuard(LocalAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();

    authController = module.get<AuthController>(AuthController);
  });

  it('should be defined', () => {
    expect(authController).toBeDefined();
  });

  describe('register', () => {
    it('should register a new user', async () => {
      const result = await authController.register('testuser', 'testpassword');

      expect(result).toEqual({
        username: 'testuser',
        password: 'hashedPassword',
      });
    });
  });

  describe('login', () => {
    it('should return a JWT token', () => {
      const req = { user: { username: 'testuser', password: 'testpassword' } };

      const result = authController.login(req);

      expect(result).toEqual({ access_token: 'mockJwtToken' });
    });
  });
});
