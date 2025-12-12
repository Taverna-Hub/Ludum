import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Heart, Users, Clock, TrendingUp } from 'lucide-react';
import { mockCampaigns } from '@/data/mockData';
import { useNavigate } from 'react-router-dom';
import { DashboardLayout } from '@/layouts/DashboardLayout';

const Crowdfunding = () => {
  const navigate = useNavigate();

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16">
        {/* Header */}
        <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
          <div className="container mx-auto">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              <span className="bg-gradient-secondary bg-clip-text text-transparent">
                Crowdfunding
              </span>
            </h1>
            <p className="text-lg text-muted-foreground max-w-2xl">
              Apoie desenvolvedores indie e ajude a tornar jogos incríveis
              realidade. Se a meta não for atingida, seu dinheiro volta para sua
              carteira automaticamente.
            </p>
          </div>
        </section>

        <div className="container mx-auto px-4 py-12">
          {/* Stats */}
          <div className="grid md:grid-cols-3 gap-6 mb-12">
            <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
              <div className="text-3xl font-bold text-secondary mb-2">
                {mockCampaigns.length}
              </div>
              <div className="text-muted-foreground">Campanhas Ativas</div>
            </Card>
            <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
              <div className="text-3xl font-bold text-primary mb-2">
                R${' '}
                {mockCampaigns
                  .reduce((sum, c) => sum + c.currentAmount, 0)
                  .toLocaleString('pt-BR')}
              </div>
              <div className="text-muted-foreground">Arrecadado</div>
            </Card>
            <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
              <div className="text-3xl font-bold text-secondary mb-2">
                {mockCampaigns
                  .reduce((sum, c) => sum + c.backers, 0)
                  .toLocaleString('pt-BR')}
              </div>
              <div className="text-muted-foreground">Apoiadores</div>
            </Card>
          </div>

          {/* Campaigns Grid */}
          <div className="grid md:grid-cols-2 gap-8">
            {mockCampaigns.map((campaign) => {
              const progress = (campaign.currentAmount / campaign.goal) * 100;
              const isSuccessful = progress >= 100;

              return (
                <Card
                  key={campaign.id}
                  className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-secondary/30 transition-smooth"
                >
                  {/* Cover */}
                  <div className="relative h-64 overflow-hidden">
                    <img
                      src={campaign.coverImage}
                      alt={campaign.title}
                      className="w-full h-full object-cover"
                    />
                    {isSuccessful && (
                      <Badge className="absolute top-4 right-4 bg-gradient-secondary">
                        Meta Atingida! 🎉
                      </Badge>
                    )}
                    <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-4">
                      <h3 className="text-white text-2xl font-bold">
                        {campaign.title}
                      </h3>
                    </div>
                  </div>

                  {/* Content */}
                  <div className="p-6">
                    <p className="text-muted-foreground mb-6 line-clamp-3">
                      {campaign.description}
                    </p>

                    {/* Progress */}
                    <div className="mb-4">
                      <div className="flex justify-between text-sm mb-2">
                        <span className="font-semibold">
                          R$ {campaign.currentAmount.toLocaleString('pt-BR')}
                        </span>
                        <span className="text-muted-foreground">
                          Meta: R$ {campaign.goal.toLocaleString('pt-BR')}
                        </span>
                      </div>
                      <Progress value={progress} className="h-3" />
                      <p className="text-sm text-muted-foreground mt-2">
                        {progress.toFixed(0)}% da meta alcançada
                      </p>
                    </div>

                    {/* Stats */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Users className="w-5 h-5 text-secondary" />
                        <div>
                          <p className="font-semibold text-foreground">
                            {campaign.backers}
                          </p>
                          <p className="text-xs">Apoiadores</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Clock className="w-5 h-5 text-secondary" />
                        <div>
                          <p className="font-semibold text-foreground">
                            {campaign.daysLeft} dias
                          </p>
                          <p className="text-xs">Restantes</p>
                        </div>
                      </div>
                    </div>

                    {/* Rewards Preview */}
                    <div className="mb-6">
                      <p className="text-sm font-medium mb-3">
                        Recompensas disponíveis:
                      </p>
                      <div className="space-y-2">
                        {campaign.rewards.slice(0, 2).map((reward) => (
                          <div
                            key={reward.id}
                            className="flex items-center justify-between p-3 bg-muted/30 rounded-lg"
                          >
                            <div>
                              <p className="font-semibold text-sm">
                                {reward.title}
                              </p>
                              <p className="text-xs text-muted-foreground line-clamp-1">
                                {reward.description}
                              </p>
                            </div>
                            <div className="text-right">
                              <p className="font-bold text-secondary">
                                R$ {reward.amount}
                              </p>
                              <p className="text-xs text-muted-foreground">
                                {reward.backers} apoios
                              </p>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>

                    <Button
                      variant="accent"
                      className="w-full"
                      onClick={() => navigate(`/campanha/${campaign.id}`)}
                    >
                      <Heart className="w-4 h-4 mr-2" />
                      Apoiar Projeto
                    </Button>
                  </div>
                </Card>
              );
            })}
          </div>

          {/* Info Card */}
          <Card className="mt-12 p-6 bg-gradient-hero border-primary/20">
            <div className="flex items-start gap-4">
              <div className="w-12 h-12 rounded-full bg-secondary/20 flex items-center justify-center flex-shrink-0">
                <TrendingUp className="w-6 h-6 text-secondary" />
              </div>
              <div>
                <h3 className="font-bold text-lg mb-2">
                  Como funciona o Crowdfunding?
                </h3>
                <ul className="space-y-2 text-muted-foreground text-sm">
                  <li>• Escolha uma campanha e selecione uma recompensa</li>
                  <li>• Seu dinheiro fica bloqueado até o fim da campanha</li>
                  <li>
                    • Se a meta for atingida, o desenvolvedor recebe os fundos
                    em 24h
                  </li>
                  <li>
                    • Se a meta não for atingida, você recebe reembolso
                    automático na carteira
                  </li>
                  <li>• Pode solicitar reembolso em até 24h após o apoio</li>
                </ul>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Crowdfunding;
