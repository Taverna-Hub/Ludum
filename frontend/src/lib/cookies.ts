const TOKEN_COOKIE_NAME = 'ludum_auth_token';
const USER_COOKIE_NAME = 'ludum_user';

export function setAuthCookie(token: string, days: number = 7): void {
  const expires = new Date();
  expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
  document.cookie = `${TOKEN_COOKIE_NAME}=${token}; expires=${expires.toUTCString()}; path=/; SameSite=Strict`;
}

export function getAuthCookie(): string | null {
  const cookies = document.cookie.split(';');
  for (const cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === TOKEN_COOKIE_NAME) {
      return value;
    }
  }
  return null;
}

export function removeAuthCookie(): void {
  document.cookie = `${TOKEN_COOKIE_NAME}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

export interface StoredUser {
  id: string;
  nome: string;
  tipo: 'JOGADOR' | 'DESENVOLVEDOR';
}

export function setUserCookie(user: StoredUser, days: number = 7): void {
  const expires = new Date();
  expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
  const userJson = encodeURIComponent(JSON.stringify(user));
  document.cookie = `${USER_COOKIE_NAME}=${userJson}; expires=${expires.toUTCString()}; path=/; SameSite=Strict`;
}

export function getUserCookie(): StoredUser | null {
  const cookies = document.cookie.split(';');
  for (const cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === USER_COOKIE_NAME) {
      try {
        return JSON.parse(decodeURIComponent(value));
      } catch {
        return null;
      }
    }
  }
  return null;
}

export function removeUserCookie(): void {
  document.cookie = `${USER_COOKIE_NAME}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

export function clearAllAuthCookies(): void {
  removeAuthCookie();
  removeUserCookie();
}
