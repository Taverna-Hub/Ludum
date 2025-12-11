import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { User, Save, Gamepad2, Code } from 'lucide-react';
import { useAuthContext } from '@/contexts/AuthContext';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { toast } from 'sonner';

const Profile = () => {
  const { user, updateProfile } = useAuthContext();
  const [formData, setFormData] = useState({
    name: user?.name || '',
    accountType: user?.accountType || 'player',
  });
  const [isSaving, setIsSaving] = useState(false);

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
          <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Avatar Placeholder */}
              <div className="flex items-center gap-4">
                <div className="w-20 h-20 rounded-full bg-primary/20 flex items-center justify-center">
                  <User className="w-10 h-10 text-primary" />
                </div>
                <div>
                  <p className="font-medium">{user?.name}</p>
                  <p className="text-sm text-muted-foreground">{user?.email}</p>
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
    </DashboardLayout>
  );
};

export default Profile;
