import { useState, useEffect, useCallback } from 'react';

export interface User {
  id: string;
  name: string;
  email: string;
  accountType: 'player' | 'developer';
  pixKey: string | null;
  createdAt: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const STORAGE_KEY = 'ludum_auth';
const USERS_KEY = 'ludum_users';

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        const user = JSON.parse(stored);
        setAuthState({ user, isAuthenticated: true, isLoading: false });
      } catch {
        setAuthState({ user: null, isAuthenticated: false, isLoading: false });
      }
    } else {
      setAuthState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  const getUsers = (): User[] => {
    const stored = localStorage.getItem(USERS_KEY);
    return stored ? JSON.parse(stored) : [];
  };

  const saveUsers = (users: User[]) => {
    localStorage.setItem(USERS_KEY, JSON.stringify(users));
  };

  const login = useCallback(async (email: string, password: string): Promise<{ success: boolean; error?: string }> => {
    const users = getUsers();
    const user = users.find((u) => u.email === email);
    
    if (!user) {
      return { success: false, error: 'Usuário não encontrado' };
    }

    // Simulated password check (in real app, would be hashed)
    const passwords = JSON.parse(localStorage.getItem('ludum_passwords') || '{}');
    if (passwords[email] !== password) {
      return { success: false, error: 'Senha incorreta' };
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    setAuthState({ user, isAuthenticated: true, isLoading: false });
    return { success: true };
  }, []);

  const register = useCallback(async (name: string, email: string, password: string): Promise<{ success: boolean; error?: string }> => {
    const users = getUsers();
    
    if (users.some((u) => u.email === email)) {
      return { success: false, error: 'Este email já está cadastrado' };
    }

    const newUser: User = {
      id: crypto.randomUUID(),
      name,
      email,
      accountType: 'player',
      pixKey: null,
      createdAt: new Date().toISOString(),
    };

    const passwords = JSON.parse(localStorage.getItem('ludum_passwords') || '{}');
    passwords[email] = password;
    localStorage.setItem('ludum_passwords', JSON.stringify(passwords));

    saveUsers([...users, newUser]);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(newUser));
    setAuthState({ user: newUser, isAuthenticated: true, isLoading: false });
    return { success: true };
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setAuthState({ user: null, isAuthenticated: false, isLoading: false });
  }, []);

  const updateProfile = useCallback((updates: Partial<Pick<User, 'name' | 'accountType' | 'pixKey'>>) => {
    if (!authState.user) return;

    const updatedUser = { ...authState.user, ...updates };
    const users = getUsers();
    const updatedUsers = users.map((u) => (u.id === updatedUser.id ? updatedUser : u));
    
    saveUsers(updatedUsers);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updatedUser));
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
