import { Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';

@Injectable()
export class AuthService {
  constructor(private jwtService: JwtService) {}

  private users: Array<{ username: string; password: string }> = [];

  async register(
    username: string,
    password: string,
  ): Promise<{ username: string; password: string } | null> {
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = { username, password: hashedPassword };
    if (!this.users.find((u) => u.username === username)) {
      this.users.push(user);
      return user;
    }
    return null;
  }

  async validateUser(
    username: string,
    password: string,
  ): Promise<{ username: string } | null> {
    const user = this.users.find((u) => u.username === username);
    if (user && (await bcrypt.compare(password, user.password))) {
      const { password, ...result } = user;
      console.log(password);
      return result;
    }
    return null;
  }

  existsUser(username: string): boolean {
    if (this.users.find((u) => u.username === username)) {
      return true;
    }
    return false;
  }

  login(user: { username: string; password: string }) {
    const payload = { username: user.username };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }
}
