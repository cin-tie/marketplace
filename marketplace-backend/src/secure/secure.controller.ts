import { Controller, Get, UseGuards } from '@nestjs/common';
import { JwtAuthGuard } from './jwt-auth.guard';

@Controller('protected')
export class SecureController {
  @UseGuards(JwtAuthGuard)
  @Get()
  getProtectedResource() {
    return 'This is a protected resource';
  }
}
