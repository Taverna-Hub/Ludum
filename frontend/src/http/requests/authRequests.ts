import api from '../axios';

export interface LoginPayload {
  nome: string;
  senha: string;
}

export interface RegistroPayload {
  nome: string;
  senha: string;
  tipo: 'JOGADOR' | 'DESENVOLVEDOR';
}

export interface AuthResponse {
  token: string;
  userId: string;
  nome: string;
}

export async function loginRequest(payload: LoginPayload): Promise<AuthResponse> {
  try {
    const response = await api.post('/auth/login', payload);
    return response.data;
  } catch (error) {
    console.error('Login request error:', error);
    throw error;
  }
}

export async function registroRequest(payload: RegistroPayload): Promise<AuthResponse> {
  try {
    const response = await api.post('/auth/registro', payload);
    return response.data;
  } catch (error) {
    console.error('Registro request error:', error);
    throw error;
  }
}

export async function logoutRequest(): Promise<void> {
  try {
    await api.post('/auth/logout');
  } catch (error) {
    console.error('Logout request error:', error);
    throw error;
  }
}
