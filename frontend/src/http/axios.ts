import axios from 'axios';
import { getAuthCookie, clearAllAuthCookies } from '@/lib/cookies';

const api = axios.create({
  baseURL: 'http://localhost:8080/',
});

// Rotas de autenticação que não devem redirecionar em caso de 401
const AUTH_ROUTES = ['/auth/login', '/auth/registro', '/auth/logout'];

// Interceptor para adicionar o token em todas as requisições
api.interceptors.request.use(
  (config) => {
    const token = getAuthCookie();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const requestUrl = error.config?.url || '';
    const isAuthRoute = AUTH_ROUTES.some(route => requestUrl.includes(route));
    
    // Só redireciona para /auth se NÃO for uma rota de autenticação
    // Isso evita loop infinito quando login/registro falham com 401
    if (error.response?.status === 401 && !isAuthRoute) {
      // Token inválido ou expirado - limpar cookies e redirecionar
      clearAllAuthCookies();
      window.location.href = '/auth';
    }
    return Promise.reject(error);
  }
);

export default api;