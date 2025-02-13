import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';

describe('AuthService', () => {
  let authService: AuthService;
  let jwtService: JwtService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: JwtService,
          useValue: {
            sign: jest.fn().mockReturnValue('mockJwtToken'),
          },
        },
      ],
    }).compile();

    authService = module.get<AuthService>(AuthService);
    jwtService = module.get<JwtService>(JwtService);
  });

  it('should be defined', () => {
    expect(authService).toBeDefined();
  });

  describe('register', () => {
    it('should register a new user', async () => {
      const username = 'testuser';
      const password = 'testpassword';

      (bcrypt.hash as jest.Mock).mockResolvedValue('hashedPassword');

      const result = await authService.register(username, password);

      expect(result).not.toBe(null);
      if (result != null) {
        expect(result.username).toBe(username);
        expect(result.password).toBe('hashedPassword');
      }
      expect(bcrypt.hash).toHaveBeenCalledWith(password, 10);
    });
  });

  describe('validateUser', () => {
    it('should return user if credentials are valid', async () => {
      const username = 'testuser';
      const password = 'testpassword';

      authService['users'] = [{ username, password: 'hashedPassword' }];
      (bcrypt.hash as jest.Mock).mockResolvedValue(true);

      const result = await authService.validateUser(username, password);
      expect(result).not.toBe(null);
      if (result != null) {
        expect(result.username).toBe(username);
      }
    });

    it('should return null if credentials are invalid', async () => {
      const username = 'testuser';
      const password = 'wrongpassword';

      authService['users'] = [{ username, password: 'hashedPassword' }];
      (bcrypt.hash as jest.Mock).mockResolvedValue(false);

      const result = await authService.validateUser(username, password);

      expect(result).toBeNull();
    });
  });

  describe('login', () => {
    it('should return a JWT token', () => {
      const user = { username: 'testuser', password: 'testpassword' };
      const mockJwtToken = 'mockJwtToken';

      (jwtService.sign as jest.Mock).mockImplementation(() => 'mockJwtToken');

      const result = authService.login(user);

      expect(result).toEqual({
        access_token: mockJwtToken,
      });
    });
  });
});
