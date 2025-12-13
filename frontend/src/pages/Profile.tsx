import { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { User, Save, Gamepad2, Code, Users, Heart, Loader2, X } from 'lucide-react';
import { useAuthContext } from '@/contexts/AuthContext';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { toast } from 'sonner';
import { 
  listarSeguindo, 
  listarSeguidores, 
  deixarDeSeguir,
  SeguimentoResponse 
} from '@/http/requests/seguimentoRequests';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

const Profile = () => {
  const { user, updateProfile } = useAuthContext();
  const [formData, setFormData] = useState({
    name: user?.name || '',
    accountType: user?.accountType || 'player',
  });
  const [isSaving, setIsSaving] = useState(false);
  
  // Estados para seguimentos
  const [seguindo, setSeguindo] = useState<SeguimentoResponse[]>([]);
  const [seguidores, setSeguidores] = useState<SeguimentoResponse[]>([]);
  const [loadingSeguimentos, setLoadingSeguimentos] = useState(true);
  const [showSeguindoModal, setShowSeguindoModal] = useState(false);
  const [showSeguidoresModal, setShowSeguidoresModal] = useState(false);
  const [unfollowingId, setUnfollowingId] = useState<string | null>(null);

  // Carregar seguimentos
  useEffect(() => {
    const carregarSeguimentos = async () => {
      if (!user) return;
      
      setLoadingSeguimentos(true);
      try {
        const [seguindoData, seguidoresData] = await Promise.all([
          listarSeguindo(),
          listarSeguidores()
        ]);
        setSeguindo(seguindoData);
        setSeguidores(seguidoresData);
      } catch (error) {
        console.error('Erro ao carregar seguimentos:', error);
      } finally {
        setLoadingSeguimentos(false);
      }
    };
    
    carregarSeguimentos();
  }, [user]);

  const handleUnfollow = async (alvoId: string, alvoNome: string) => {
    setUnfollowingId(alvoId);
    try {
      await deixarDeSeguir(alvoId);
      setSeguindo(prev => prev.filter(s => s.alvoId !== alvoId));
      toast.success(`Você deixou de seguir ${alvoNome}`);
    } catch (error) {
      console.error('Erro ao deixar de seguir:', error);
      toast.error('Erro ao deixar de seguir');
    } finally {
      setUnfollowingId(null);
    }
  };

  const getTipoAlvoLabel = (tipo: string) => {
    switch (tipo) {
      case 'CONTA': return 'Usuário';
      case 'JOGO': return 'Jogo';
      case 'TAG': return 'Tag';
      case 'DESENVOLVEDORA': return 'Desenvolvedora';
      default: return tipo;
    }
  };

  const getTipoAlvoColor = (tipo: string) => {
    switch (tipo) {
      case 'CONTA': return 'text-blue-400';
      case 'JOGO': return 'text-green-400';
      case 'TAG': return 'text-purple-400';
      case 'DESENVOLVEDORA': return 'text-orange-400';
      default: return 'text-muted-foreground';
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);

    try {
      updateProfile({
        name: formData.name,
        accountType: formData.accountType as 'player' | 'developer',
      });
      toast.success('Perfil atualizado com sucesso!');
    } catch {
      toast.error('Erro ao atualizar perfil');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <DashboardLayout>
      <div className="p-6 md:p-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">
            <span className="bg-gradient-primary bg-clip-text text-transparent">Meu Perfil</span>
          </h1>
          <p className="text-muted-foreground">Gerencie suas informações pessoais</p>
        </div>

        <div className="max-w-2xl">
          {/* Card de Estatísticas de Seguimentos */}
          <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50 mb-6">
            <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
              <Heart className="w-5 h-5 text-primary" />
              Conexões
            </h2>
            
            {loadingSeguimentos ? (
              <div className="flex items-center justify-center py-4">
                <Loader2 className="w-6 h-6 animate-spin text-primary" />
              </div>
            ) : (
              <div className="grid grid-cols-2 gap-4">
                <button
                  onClick={() => setShowSeguindoModal(true)}
                  className="p-4 rounded-lg bg-muted/30 hover:bg-muted/50 transition-all text-center group"
                >
                  <p className="text-3xl font-bold text-primary group-hover:scale-110 transition-transform">
                    {seguindo.length}
                  </p>
                  <p className="text-sm text-muted-foreground">Seguindo</p>
                </button>
                
                <button
                  onClick={() => setShowSeguidoresModal(true)}
                  className="p-4 rounded-lg bg-muted/30 hover:bg-muted/50 transition-all text-center group"
                >
                  <p className="text-3xl font-bold text-secondary group-hover:scale-110 transition-transform">
                    {seguidores.length}
                  </p>
                  <p className="text-sm text-muted-foreground">Seguidores</p>
                </button>
              </div>
            )}
          </Card>

          <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Avatar Placeholder */}
              <div className="flex items-center gap-4">
                <div className="w-20 h-20 rounded-full bg-primary/20 flex items-center justify-center">
                  <User className="w-10 h-10 text-primary" />
                </div>
                <div>
                  <p className="font-medium">{user?.name}</p>
                  <p className="text-sm text-muted-foreground">
                    {user?.accountType === 'developer' ? 'Desenvolvedor' : 'Jogador'}
                  </p>
                </div>
              </div>

              {/* Name */}
              <div className="space-y-2">
                <Label htmlFor="name">Nome</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Seu nome"
                />
              </div>

              {/* Account Type */}
              <div className="space-y-3">
                <Label>Tipo de Conta</Label>
              <RadioGroup
                  value={formData.accountType}
                  onValueChange={(value: 'player' | 'developer') => setFormData({ ...formData, accountType: value })}
                  className="grid grid-cols-1 md:grid-cols-2 gap-4"
                >
                  <Label
                    htmlFor="player"
                    className={`flex items-center gap-3 p-4 rounded-lg border cursor-pointer transition-all ${
                      formData.accountType === 'player'
                        ? 'border-primary bg-primary/10'
                        : 'border-border hover:border-primary/50'
                    }`}
                  >
                    <RadioGroupItem value="player" id="player" />
                    <Gamepad2 className="w-5 h-5 text-secondary" />
                    <div>
                      <p className="font-medium">Jogador</p>
                      <p className="text-xs text-muted-foreground">Compre e jogue games</p>
                    </div>
                  </Label>

                  <Label
                    htmlFor="developer"
                    className={`flex items-center gap-3 p-4 rounded-lg border cursor-pointer transition-all ${
                      formData.accountType === 'developer'
                        ? 'border-primary bg-primary/10'
                        : 'border-border hover:border-primary/50'
                    }`}
                  >
                    <RadioGroupItem value="developer" id="developer" />
                    <Code className="w-5 h-5 text-primary" />
                    <div>
                      <p className="font-medium">Desenvolvedor</p>
                      <p className="text-xs text-muted-foreground">Publique e venda seus jogos</p>
                    </div>
                  </Label>
                </RadioGroup>
              </div>

              {/* Member Since */}
              <div className="p-4 bg-muted/30 rounded-lg">
                <p className="text-sm text-muted-foreground">
                  Membro desde:{' '}
                  <span className="text-foreground">
                    {user?.createdAt
                      ? new Date(user.createdAt).toLocaleDateString('pt-BR', {
                          day: '2-digit',
                          month: 'long',
                          year: 'numeric',
                        })
                      : 'N/A'}
                  </span>
                </p>
              </div>

              <Button type="submit" disabled={isSaving} className="w-full md:w-auto">
                <Save className="w-4 h-4 mr-2" />
                {isSaving ? 'Salvando...' : 'Salvar Alterações'}
              </Button>
            </form>
          </Card>
        </div>
      </div>

      {/* Modal Seguindo */}
      <Dialog open={showSeguindoModal} onOpenChange={setShowSeguindoModal}>
        <DialogContent className="max-w-md max-h-[80vh] overflow-hidden flex flex-col">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Users className="w-5 h-5" />
              Seguindo ({seguindo.length})
            </DialogTitle>
          </DialogHeader>
          
          <div className="overflow-y-auto flex-1 -mx-6 px-6">
            {seguindo.length === 0 ? (
              <p className="text-center text-muted-foreground py-8">
                Você ainda não está seguindo ninguém
              </p>
            ) : (
              <div className="space-y-3">
                {seguindo.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-3 rounded-lg bg-muted/30 hover:bg-muted/50 transition-all"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-gradient-primary flex items-center justify-center">
                        <User className="w-5 h-5 text-white" />
                      </div>
                      <div>
                        <p className="font-medium">{item.alvoNome}</p>
                        <p className={`text-xs ${getTipoAlvoColor(item.tipoAlvo)}`}>
                          {getTipoAlvoLabel(item.tipoAlvo)}
                        </p>
                      </div>
                    </div>
                    
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => handleUnfollow(item.alvoId, item.alvoNome)}
                      disabled={unfollowingId === item.alvoId}
                    >
                      {unfollowingId === item.alvoId ? (
                        <Loader2 className="w-4 h-4 animate-spin" />
                      ) : (
                        <X className="w-4 h-4" />
                      )}
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {/* Modal Seguidores */}
      <Dialog open={showSeguidoresModal} onOpenChange={setShowSeguidoresModal}>
        <DialogContent className="max-w-md max-h-[80vh] overflow-hidden flex flex-col">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Heart className="w-5 h-5" />
              Seguidores ({seguidores.length})
            </DialogTitle>
          </DialogHeader>
          
          <div className="overflow-y-auto flex-1 -mx-6 px-6">
            {seguidores.length === 0 ? (
              <p className="text-center text-muted-foreground py-8">
                Você ainda não tem seguidores
              </p>
            ) : (
              <div className="space-y-3">
                {seguidores.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center gap-3 p-3 rounded-lg bg-muted/30"
                  >
                    <div className="w-10 h-10 rounded-full bg-gradient-secondary flex items-center justify-center">
                      <User className="w-5 h-5 text-white" />
                    </div>
                    <div>
                      <p className="font-medium">{item.alvoNome}</p>
                      <p className="text-xs text-muted-foreground">
                        Seguindo desde {new Date(item.dataSeguimento).toLocaleDateString('pt-BR')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </DashboardLayout>
  );
};

export default Profile;
