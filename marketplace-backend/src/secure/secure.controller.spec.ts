import { Test, TestingModule } from '@nestjs/testing';
import { SecureController } from './secure.controller';

describe('SecureController', () => {
  let controller: SecureController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [SecureController],
    }).compile();

    controller = module.get<SecureController>(SecureController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
