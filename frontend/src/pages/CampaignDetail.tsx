import { useParams, useNavigate } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Separator } from "@/components/ui/separator";
import { Heart, Users, Clock, ArrowLeft, CheckCircle2, Target } from "lucide-react";
import { mockCampaigns, mockWallet } from "@/data/mockData";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";

const CampaignDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [selectedReward, setSelectedReward] = useState<string | null>(null);

  const campaign = mockCampaigns.find((c) => c.id === id);

  if (!campaign) {
    return (
      <div className="min-h-screen pt-16 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Campanha não encontrada</h1>
          <Button onClick={() => navigate("/crowdfunding")}>
            Voltar para Crowdfunding
          </Button>
        </div>
      </div>
    );
  }

  const progress = (campaign.currentAmount / campaign.goal) * 100;
  const isSuccessful = progress >= 100;
  const selectedRewardData = campaign.rewards.find((r) => r.id === selectedReward);

  const handleSupport = () => {
    if (!selectedReward) {
      toast({
        title: "Selecione uma recompensa",
        description: "Escolha uma das recompensas disponíveis para apoiar o projeto.",
        variant: "destructive",
      });
      return;
    }

    const reward = campaign.rewards.find((r) => r.id === selectedReward);
    if (!reward) return;

    if (mockWallet.availableBalance < reward.amount) {
      toast({
        title: "Saldo insuficiente",
        description: "Adicione saldo à sua carteira para apoiar este projeto.",
        variant: "destructive",
      });
      navigate("/carteira");
      return;
    }

    toast({
      title: "Apoio realizado com sucesso! 🎉",
      description: `Você apoiou ${campaign.title} com R$ ${reward.amount}. O valor ficará bloqueado até o fim da campanha.`,
    });

    setTimeout(() => {
      navigate("/crowdfunding");
    }, 2000);
  };

  return (
    <div className="min-h-screen pt-16 pb-12">
      {/* Back Button */}
      <div className="container mx-auto px-4 py-6">
        <Button
          variant="ghost"
          onClick={() => navigate("/crowdfunding")}
          className="mb-4"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          Voltar para Crowdfunding
        </Button>
      </div>

      {/* Hero Section */}
      <div className="relative h-[400px] overflow-hidden">
        <img
          src={campaign.coverImage}
          alt={campaign.title}
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black via-black/50 to-transparent">
          <div className="container mx-auto px-4 h-full flex flex-col justify-end pb-8">
            <div className="flex items-center gap-4 mb-4">
              {isSuccessful && (
                <Badge className="bg-gradient-secondary text-base px-4 py-2">
                  Meta Atingida! 🎉
                </Badge>
              )}
              <Badge variant="outline" className="text-base px-4 py-2 border-white/30 text-white">
                <Clock className="w-4 h-4 mr-2" />
                {campaign.daysLeft} dias restantes
              </Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold text-white mb-4">
              {campaign.title}
            </h1>
            <p className="text-lg text-white/90 max-w-3xl">
              {campaign.description}
            </p>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 mt-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Progress Section */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
              <div className="flex items-center gap-2 mb-4">
                <Target className="w-5 h-5 text-secondary" />
                <h2 className="text-2xl font-bold">Progresso da Campanha</h2>
              </div>
              <Separator className="mb-6" />
              
              <div className="space-y-4">
                <div className="flex justify-between items-baseline">
                  <div>
                    <span className="text-3xl font-bold text-primary">
                      R$ {campaign.currentAmount.toLocaleString('pt-BR')}
                    </span>
                    <span className="text-muted-foreground ml-2">arrecadados</span>
                  </div>
                  <span className="text-lg text-muted-foreground">
                    Meta: R$ {campaign.goal.toLocaleString('pt-BR')}
                  </span>
                </div>
                
                <Progress value={progress} className="h-4" />
                
                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-secondary/20 flex items-center justify-center">
                      <Users className="w-6 h-6 text-secondary" />
                    </div>
                    <div>
                      <p className="text-2xl font-bold">{campaign.backers}</p>
                      <p className="text-sm text-muted-foreground">Apoiadores</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-primary/20 flex items-center justify-center">
                      <CheckCircle2 className="w-6 h-6 text-primary" />
                    </div>
                    <div>
                      <p className="text-2xl font-bold">{progress.toFixed(0)}%</p>
                      <p className="text-sm text-muted-foreground">da meta</p>
                    </div>
                  </div>
                </div>
              </div>
            </Card>

            {/* About Section */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
              <h2 className="text-2xl font-bold mb-4">Sobre o Projeto</h2>
              <Separator className="mb-6" />
              <div className="prose prose-invert max-w-none">
                <p className="text-muted-foreground leading-relaxed">
                  {campaign.description}
                </p>
                <p className="text-muted-foreground leading-relaxed mt-4">
                  Este é um projeto ambicioso que busca trazer uma experiência única para os jogadores. 
                  Com seu apoio, poderemos finalizar o desenvolvimento e lançar um jogo que ficará marcado 
                  na memória de todos que jogarem.
                </p>
              </div>
            </Card>

            {/* How it Works */}
            <Card className="p-6 bg-gradient-hero border-primary/20">
              <h2 className="text-xl font-bold mb-4">Como Funciona?</h2>
              <ul className="space-y-3 text-sm text-muted-foreground">
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                  <span>Escolha uma recompensa e confirme seu apoio</span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                  <span>Seu dinheiro fica bloqueado na carteira até o fim da campanha</span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                  <span>Se a meta for atingida, o desenvolvedor recebe os fundos em 24h</span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                  <span>Se a meta não for atingida, você recebe reembolso automático</span>
                </li>
                <li className="flex items-start gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                  <span>Pode solicitar reembolso em até 24h após o apoio</span>
                </li>
              </ul>
            </Card>
          </div>

          {/* Sidebar - Rewards */}
          <div className="space-y-6">
            <Card className="p-6 bg-card/50 backdrop-blur-sm sticky top-20">
              <h2 className="text-2xl font-bold mb-6">Recompensas</h2>
              <div className="space-y-4">
                {campaign.rewards.map((reward) => (
                  <Card
                    key={reward.id}
                    className={`p-4 cursor-pointer transition-all ${
                      selectedReward === reward.id
                        ? 'border-secondary bg-secondary/10'
                        : 'border-border/50 hover:border-secondary/50'
                    }`}
                    onClick={() => setSelectedReward(reward.id)}
                  >
                    <div className="flex justify-between items-start mb-3">
                      <h3 className="font-bold text-lg">{reward.title}</h3>
                      <Badge variant="secondary" className="ml-2">
                        R$ {reward.amount}
                      </Badge>
                    </div>
                    <p className="text-sm text-muted-foreground mb-3">
                      {reward.description}
                    </p>
                    <div className="flex items-center gap-2 text-xs text-muted-foreground">
                      <Users className="w-4 h-4" />
                      <span>{reward.backers} apoiadores</span>
                    </div>
                  </Card>
                ))}
              </div>

              <Separator className="my-6" />

              {selectedRewardData && (
                <div className="mb-4 p-4 bg-primary/10 rounded-lg border border-primary/20">
                  <p className="text-sm text-muted-foreground mb-2">Você selecionou:</p>
                  <p className="font-bold">{selectedRewardData.title}</p>
                  <p className="text-2xl font-bold text-primary mt-2">
                    R$ {selectedRewardData.amount}
                  </p>
                </div>
              )}

              <Button
                variant="accent"
                className="w-full"
                size="lg"
                onClick={handleSupport}
              >
                <Heart className="w-5 h-5 mr-2" />
                Apoiar Projeto
              </Button>

              <p className="text-xs text-muted-foreground text-center mt-4">
                Seu saldo atual: R$ {mockWallet.availableBalance.toFixed(2)}
              </p>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CampaignDetail;