import axios from 'axios';
import { getAuthCookie } from '@/lib/cookies';

const api = axios.create({
  baseURL: 'http://localhost:8080/',
});

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
    if (error.response?.status === 401) {
      // Token inválido ou expirado - limpar cookies
      import('@/lib/cookies').then(({ clearAllAuthCookies }) => {
        clearAllAuthCookies();
        window.location.href = '/auth';
      });
    }
    return Promise.reject(error);
  }
);

export default api;