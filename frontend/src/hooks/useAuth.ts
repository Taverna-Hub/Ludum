import { useState, useEffect, useCallback } from 'react';
import { loginRequest, registroRequest, logoutRequest } from '@/http/requests/authRequests';
import { 
  setAuthCookie, 
  getAuthCookie, 
  setUserCookie, 
  getUserCookie, 
  clearAllAuthCookies,
  StoredUser 
} from '@/lib/cookies';

export interface User {
  id: string;
  name: string;
  accountType: 'player' | 'developer';
  pixKey: string | null;
  createdAt: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  useEffect(() => {
    const token = getAuthCookie();
    const storedUser = getUserCookie();
    
    if (token && storedUser) {
      const user: User = {
        id: storedUser.id,
        name: storedUser.nome,
        accountType: storedUser.tipo === 'DESENVOLVEDOR' ? 'developer' : 'player',
        pixKey: null,
        createdAt: new Date().toISOString(),
      };
      setAuthState({ user, isAuthenticated: true, isLoading: false });
    } else {
      clearAllAuthCookies();
      setAuthState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  const login = useCallback(async (username: string, password: string): Promise<{ success: boolean; error?: string }> => {
    try {
      const response = await loginRequest({ nome: username, senha: password });
      
      setAuthCookie(response.token);
      
      const storedUser: StoredUser = {
        id: response.userId,
        nome: response.nome,
        tipo: 'JOGADOR',
      };
      setUserCookie(storedUser);
      
      const user: User = {
        id: response.userId,
        name: response.nome,
        accountType: 'player',
        pixKey: null,
        createdAt: new Date().toISOString(),
      };
      
      setAuthState({ user, isAuthenticated: true, isLoading: false });
      return { success: true };
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Erro ao fazer login';
      return { success: false, error: errorMessage };
    }
  }, []);

  const register = useCallback(async (name: string, _username: string, password: string): Promise<{ success: boolean; error?: string }> => {
    try {
      // O backend usa 'nome' como identificador de login
      const response = await registroRequest({ 
        nome: name, 
        senha: password, 
        tipo: 'JOGADOR' 
      });
      
      setAuthCookie(response.token);
      
      const storedUser: StoredUser = {
        id: response.userId,
        nome: response.nome,
        tipo: 'JOGADOR',
      };
      setUserCookie(storedUser);
      
      const user: User = {
        id: response.userId,
        name: response.nome,
        accountType: 'player',
        pixKey: null,
        createdAt: new Date().toISOString(),
      };
      
      setAuthState({ user, isAuthenticated: true, isLoading: false });
      return { success: true };
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Erro ao criar conta';
      return { success: false, error: errorMessage };
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await logoutRequest();
    } catch (error) {
      console.error('Erro ao fazer logout no servidor:', error);
    } finally {
      clearAllAuthCookies();
      setAuthState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  const updateProfile = useCallback((updates: Partial<Pick<User, 'name' | 'accountType' | 'pixKey'>>) => {
    if (!authState.user) return;

    const updatedUser = { ...authState.user, ...updates };
    
    // Atualiza o cookie com os novos dados
    const storedUser: StoredUser = {
      id: updatedUser.id,
      nome: updatedUser.name,
      tipo: updatedUser.accountType === 'developer' ? 'DESENVOLVEDOR' : 'JOGADOR',
    };
    setUserCookie(storedUser);
    
    setAuthState({ ...authState, user: updatedUser });
  }, [authState]);

  return {
    ...authState,
    login,
    register,
    logout,
    updateProfile,
  };
};
