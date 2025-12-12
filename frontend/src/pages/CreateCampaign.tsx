import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import {
  Plus,
  X,
  Trash2,
  ImageIcon,
  Target,
  Gift,
  CheckCircle2,
  AlertCircle,
} from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { DashboardLayout } from '@/layouts/DashboardLayout';

interface Reward {
  id: string;
  amount: string;
  title: string;
  description: string;
}

const CreateCampaign = () => {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    goal: '',
    durationDays: '30',
  });

  const [coverImage, setCoverImage] = useState<string | null>(null);
  const [rewards, setRewards] = useState<Reward[]>([
    { id: '1', amount: '', title: '', description: '' },
  ]);

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleCoverUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setCoverImage(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const addReward = () => {
    const newReward: Reward = {
      id: Date.now().toString(),
      amount: '',
      title: '',
      description: '',
    };
    setRewards((prev) => [...prev, newReward]);
  };

  const removeReward = (id: string) => {
    if (rewards.length === 1) {
      toast({
        title: 'Mínimo necessário',
        description: 'É necessário ter pelo menos 1 recompensa.',
        variant: 'destructive',
      });
      return;
    }
    setRewards((prev) => prev.filter((r) => r.id !== id));
  };

  const updateReward = (id: string, field: keyof Reward, value: string) => {
    setRewards((prev) =>
      prev.map((r) => (r.id === id ? { ...r, [field]: value } : r)),
    );
  };

  const validateForm = () => {
    if (!formData.title.trim()) {
      toast({
        title: 'Título obrigatório',
        description: 'A campanha precisa de um título.',
        variant: 'destructive',
      });
      return false;
    }

    if (!formData.description.trim()) {
      toast({
        title: 'Descrição obrigatória',
        description: 'Adicione uma descrição para a campanha.',
        variant: 'destructive',
      });
      return false;
    }

    if (!formData.goal || parseFloat(formData.goal) <= 0) {
      toast({
        title: 'Meta inválida',
        description: 'Defina uma meta mínima válida.',
        variant: 'destructive',
      });
      return false;
    }

    if (!coverImage) {
      toast({
        title: 'Capa obrigatória',
        description: 'Adicione uma imagem de capa.',
        variant: 'destructive',
      });
      return false;
    }

    const validRewards = rewards.filter(
      (r) => r.title.trim() && r.description.trim() && parseFloat(r.amount) > 0,
    );

    if (validRewards.length === 0) {
      toast({
        title: 'Recompensas incompletas',
        description: 'Preencha pelo menos 1 recompensa completa.',
        variant: 'destructive',
      });
      return false;
    }

    return true;
  };

  const handleCreateCampaign = () => {
    if (!validateForm()) return;

    toast({
      title: 'Campanha criada com sucesso! 🎉',
      description: `${formData.title} está agora disponível para apoios.`,
    });

    setTimeout(() => {
      navigate('/crowdfunding');
    }, 2000);
  };

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16 pb-12">
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-4xl mx-auto">
            <div className="mb-8">
              <h1 className="text-4xl font-bold mb-4">
                <span className="bg-gradient-secondary bg-clip-text text-transparent">
                  Criar Campanha de Crowdfunding
                </span>
              </h1>
              <p className="text-muted-foreground">
                Crie uma campanha para financiar o desenvolvimento do seu jogo.
              </p>
            </div>

            <div className="space-y-6">
              {/* Informações Básicas */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <div className="flex items-center gap-2 mb-4">
                  <Target className="w-5 h-5 text-secondary" />
                  <h2 className="text-xl font-bold">Informações da Campanha</h2>
                </div>
                <Separator className="mb-6" />

                <div className="space-y-4">
                  <div>
                    <Label htmlFor="title">Título da Campanha *</Label>
                    <Input
                      id="title"
                      placeholder="Nome do seu projeto"
                      value={formData.title}
                      onChange={(e) =>
                        handleInputChange('title', e.target.value)
                      }
                      className="mt-2"
                    />
                  </div>

                  <div>
                    <Label htmlFor="description">Descrição *</Label>
                    <Textarea
                      id="description"
                      placeholder="Descreva seu projeto e como os fundos serão utilizados..."
                      value={formData.description}
                      onChange={(e) =>
                        handleInputChange('description', e.target.value)
                      }
                      className="mt-2 min-h-32"
                    />
                  </div>

                  <div className="grid md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="goal">Meta Mínima (R$) *</Label>
                      <Input
                        id="goal"
                        type="number"
                        placeholder="50000"
                        value={formData.goal}
                        onChange={(e) =>
                          handleInputChange('goal', e.target.value)
                        }
                        className="mt-2"
                      />
                      <p className="text-xs text-muted-foreground mt-1">
                        Se não atingir, há reembolso automático
                      </p>
                    </div>

                    <div>
                      <Label htmlFor="duration">Duração (dias)</Label>
                      <Input
                        id="duration"
                        type="number"
                        placeholder="30"
                        value={formData.durationDays}
                        onChange={(e) =>
                          handleInputChange('durationDays', e.target.value)
                        }
                        className="mt-2"
                      />
                    </div>
                  </div>
                </div>
              </Card>

              {/* Capa */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h2 className="text-xl font-bold mb-4">Imagem de Capa *</h2>
                <Separator className="mb-6" />

                {!coverImage ? (
                  <div
                    className="border-2 border-dashed border-border rounded-lg p-12 text-center hover:border-secondary/50 transition-smooth cursor-pointer"
                    onClick={() =>
                      document.getElementById('cover-upload')?.click()
                    }
                  >
                    <ImageIcon className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
                    <p className="text-lg font-medium mb-2">Adicionar Capa</p>
                    <p className="text-sm text-muted-foreground">
                      Recomendado: 1920x1080px
                    </p>
                  </div>
                ) : (
                  <div className="relative">
                    <img
                      src={coverImage}
                      alt="Capa"
                      className="w-full h-80 object-cover rounded-lg"
                    />
                    <Button
                      size="icon"
                      variant="destructive"
                      className="absolute top-4 right-4"
                      onClick={() => setCoverImage(null)}
                    >
                      <X className="w-4 h-4" />
                    </Button>
                  </div>
                )}
                <Input
                  id="cover-upload"
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={handleCoverUpload}
                />
              </Card>

              {/* Recompensas */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <Gift className="w-5 h-5 text-secondary" />
                    <h2 className="text-xl font-bold">Recompensas</h2>
                  </div>
                  <Button variant="outline" size="sm" onClick={addReward}>
                    <Plus className="w-4 h-4 mr-2" />
                    Adicionar
                  </Button>
                </div>
                <Separator className="mb-6" />

                <div className="space-y-4">
                  {rewards.map((reward, index) => (
                    <Card key={reward.id} className="p-4 bg-muted/30">
                      <div className="flex items-center justify-between mb-4">
                        <Badge variant="secondary">
                          Recompensa {index + 1}
                        </Badge>
                        {rewards.length > 1 && (
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => removeReward(reward.id)}
                          >
                            <Trash2 className="w-4 h-4 text-destructive" />
                          </Button>
                        )}
                      </div>

                      <div className="space-y-3">
                        <div className="grid md:grid-cols-2 gap-3">
                          <div>
                            <Label>Valor (R$) *</Label>
                            <Input
                              type="number"
                              placeholder="25"
                              value={reward.amount}
                              onChange={(e) =>
                                updateReward(
                                  reward.id,
                                  'amount',
                                  e.target.value,
                                )
                              }
                              className="mt-1"
                            />
                          </div>
                          <div>
                            <Label>Título *</Label>
                            <Input
                              placeholder="Apoiador"
                              value={reward.title}
                              onChange={(e) =>
                                updateReward(reward.id, 'title', e.target.value)
                              }
                              className="mt-1"
                            />
                          </div>
                        </div>
                        <div>
                          <Label>Descrição *</Label>
                          <Textarea
                            placeholder="Cópia digital do jogo + créditos"
                            value={reward.description}
                            onChange={(e) =>
                              updateReward(
                                reward.id,
                                'description',
                                e.target.value,
                              )
                            }
                            className="mt-1"
                          />
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>
              </Card>

              {/* Como Funciona */}
              <Card className="p-6 bg-gradient-hero border-primary/20">
                <h3 className="font-bold mb-4 flex items-center gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary" />
                  Como Funciona o Crowdfunding
                </h3>
                <ul className="space-y-2 text-sm text-muted-foreground">
                  <li className="flex items-start gap-2">
                    <CheckCircle2 className="w-4 h-4 text-secondary flex-shrink-0 mt-0.5" />
                    <span>Jogadores apoiam escolhendo uma recompensa</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <CheckCircle2 className="w-4 h-4 text-secondary flex-shrink-0 mt-0.5" />
                    <span>O valor fica bloqueado até o fim da campanha</span>
                  </li>
                  <li className="flex items-start gap-2">
                    <CheckCircle2 className="w-4 h-4 text-secondary flex-shrink-0 mt-0.5" />
                    <span>
                      Se atingir a meta, você recebe os fundos em 24h após o
                      término
                    </span>
                  </li>
                  <li className="flex items-start gap-2">
                    <AlertCircle className="w-4 h-4 text-yellow-500 flex-shrink-0 mt-0.5" />
                    <span>
                      Se não atingir, há estorno automático para os apoiadores
                    </span>
                  </li>
                  <li className="flex items-start gap-2">
                    <CheckCircle2 className="w-4 h-4 text-secondary flex-shrink-0 mt-0.5" />
                    <span>
                      Apoiadores podem pedir reembolso em até 24h após o apoio
                    </span>
                  </li>
                  <li className="flex items-start gap-2">
                    <CheckCircle2 className="w-4 h-4 text-secondary flex-shrink-0 mt-0.5" />
                    <span>
                      Saque liberado 1 dia após encerramento bem-sucedido
                    </span>
                  </li>
                </ul>
              </Card>

              {/* Validação */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h3 className="font-bold mb-4">Checklist</h3>
                <ul className="space-y-2 text-sm">
                  <li className="flex items-center gap-2">
                    {formData.title ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Título da campanha
                  </li>
                  <li className="flex items-center gap-2">
                    {formData.description ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Descrição
                  </li>
                  <li className="flex items-center gap-2">
                    {formData.goal && parseFloat(formData.goal) > 0 ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Meta mínima
                  </li>
                  <li className="flex items-center gap-2">
                    {coverImage ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Imagem de capa
                  </li>
                  <li className="flex items-center gap-2">
                    {rewards.some(
                      (r) =>
                        r.title && r.description && parseFloat(r.amount) > 0,
                    ) ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Pelo menos 1 recompensa completa
                  </li>
                </ul>
              </Card>

              <div className="flex gap-4">
                <Button
                  variant="accent"
                  onClick={handleCreateCampaign}
                  className="flex-1"
                >
                  <Target className="w-4 h-4 mr-2" />
                  Criar Campanha
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default CreateCampaign;
