import { useState, useCallback } from 'react';
import { 
  seguir, 
  deixarDeSeguir, 
  estaSeguindo,
  contarSeguidores,
  TipoAlvo 
} from '@/http/requests/seguimentoRequests';
import { toast } from 'sonner';

interface UseSeguimentoOptions {
  onFollowSuccess?: () => void;
  onUnfollowSuccess?: () => void;
  onError?: (error: Error) => void;
}

export function useSeguimento(options: UseSeguimentoOptions = {}) {
  const [loading, setLoading] = useState(false);
  const [followingMap, setFollowingMap] = useState<Record<string, boolean>>({});
  const [followersCountMap, setFollowersCountMap] = useState<Record<string, number>>({});

  /**
   * Verifica se o usuário está seguindo um alvo
   */
  const verificarSeguindo = useCallback(async (alvoId: string): Promise<boolean> => {
    try {
      const resultado = await estaSeguindo(alvoId);
      setFollowingMap(prev => ({ ...prev, [alvoId]: resultado }));
      return resultado;
    } catch (error) {
      console.error('Erro ao verificar seguimento:', error);
      return false;
    }
  }, []);

  /**
   * Verifica múltiplos alvos de uma vez
   */
  const verificarMultiplosSeguindo = useCallback(async (alvoIds: string[]): Promise<Record<string, boolean>> => {
    const results: Record<string, boolean> = {};
    await Promise.all(
      alvoIds.map(async (id) => {
        try {
          results[id] = await estaSeguindo(id);
        } catch {
          results[id] = false;
        }
      })
    );
    setFollowingMap(prev => ({ ...prev, ...results }));
    return results;
  }, []);

  /**
   * Busca a contagem de seguidores de um alvo
   */
  const buscarContadorSeguidores = useCallback(async (alvoId: string): Promise<number> => {
    try {
      const count = await contarSeguidores(alvoId);
      setFollowersCountMap(prev => ({ ...prev, [alvoId]: count }));
      return count;
    } catch (error) {
      console.error('Erro ao contar seguidores:', error);
      return 0;
    }
  }, []);

  /**
   * Seguir um alvo
   */
  const handleSeguir = useCallback(async (
    alvoId: string, 
    tipoAlvo: TipoAlvo, 
    alvoNome?: string
  ): Promise<boolean> => {
    setLoading(true);
    try {
      await seguir({ alvoId, tipoAlvo });
      setFollowingMap(prev => ({ ...prev, [alvoId]: true }));
      setFollowersCountMap(prev => ({ 
        ...prev, 
        [alvoId]: (prev[alvoId] || 0) + 1 
      }));
      
      const nome = alvoNome || 'este conteúdo';
      toast.success(`Você está seguindo ${nome}`);
      options.onFollowSuccess?.();
      return true;
    } catch (error) {
      console.error('Erro ao seguir:', error);
      toast.error('Erro ao seguir. Tente novamente.');
      options.onError?.(error as Error);
      return false;
    } finally {
      setLoading(false);
    }
  }, [options]);

  /**
   * Deixar de seguir um alvo
   */
  const handleDeixarDeSeguir = useCallback(async (
    alvoId: string, 
    alvoNome?: string
  ): Promise<boolean> => {
    setLoading(true);
    try {
      await deixarDeSeguir(alvoId);
      setFollowingMap(prev => ({ ...prev, [alvoId]: false }));
      setFollowersCountMap(prev => ({ 
        ...prev, 
        [alvoId]: Math.max((prev[alvoId] || 1) - 1, 0) 
      }));
      
      const nome = alvoNome || 'este conteúdo';
      toast.success(`Você deixou de seguir ${nome}`);
      options.onUnfollowSuccess?.();
      return true;
    } catch (error) {
      console.error('Erro ao deixar de seguir:', error);
      toast.error('Erro ao deixar de seguir. Tente novamente.');
      options.onError?.(error as Error);
      return false;
    } finally {
      setLoading(false);
    }
  }, [options]);

  /**
   * Toggle de seguir/deixar de seguir
   */
  const toggleSeguir = useCallback(async (
    alvoId: string, 
    tipoAlvo: TipoAlvo, 
    alvoNome?: string
  ): Promise<boolean> => {
    const estaSeguindoAtualmente = followingMap[alvoId];
    
    if (estaSeguindoAtualmente) {
      return handleDeixarDeSeguir(alvoId, alvoNome);
    } else {
      return handleSeguir(alvoId, tipoAlvo, alvoNome);
    }
  }, [followingMap, handleSeguir, handleDeixarDeSeguir]);

  /**
   * Verifica se está seguindo (do cache local)
   */
  const isSeguindo = useCallback((alvoId: string): boolean => {
    return followingMap[alvoId] ?? false;
  }, [followingMap]);

  /**
   * Obtém contagem de seguidores (do cache local)
   */
  const getContadorSeguidores = useCallback((alvoId: string): number => {
    return followersCountMap[alvoId] ?? 0;
  }, [followersCountMap]);

  return {
    loading,
    followingMap,
    followersCountMap,
    verificarSeguindo,
    verificarMultiplosSeguindo,
    buscarContadorSeguidores,
    handleSeguir,
    handleDeixarDeSeguir,
    toggleSeguir,
    isSeguindo,
    getContadorSeguidores,
  };
}
