import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Upload, Megaphone, TrendingUp, Gamepad2, DollarSign, Users } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';

const DeveloperPanel = () => {
  const navigate = useNavigate();

  const quickActions = [
    {
      title: 'Upload de Jogo',
      description: 'Faça upload de um novo jogo para a plataforma',
      icon: Upload,
      color: 'text-secondary',
      bgColor: 'bg-secondary/10',
      action: () => navigate('/desenvolvedor/upload'),
    },
    {
      title: 'Criar Campanha',
      description: 'Inicie uma campanha de crowdfunding',
      icon: Megaphone,
      color: 'text-primary',
      bgColor: 'bg-primary/10',
      action: () => navigate('/desenvolvedor/criar-campanha'),
    },
  ];

  const stats = [
    { label: 'Jogos Publicados', value: '3', icon: Gamepad2, color: 'text-secondary' },
    { label: 'Campanhas Ativas', value: '1', icon: TrendingUp, color: 'text-primary' },
    { label: 'Receita Total', value: 'R$ 4.580,00', icon: DollarSign, color: 'text-secondary' },
    { label: 'Downloads', value: '1.2K', icon: Users, color: 'text-primary' },
  ];

  const recentActivity = [
    { type: 'sale', title: 'Venda: Cosmic Explorer', date: 'Hoje, 14:30', amount: '+R$ 29,90' },
    { type: 'download', title: 'Download: Pixel Warriors', date: 'Ontem, 18:45', amount: null },
    { type: 'campaign', title: 'Nova contribuição: RPG Medieval', date: '2 dias atrás', amount: '+R$ 50,00' },
  ];

  return (
    <DashboardLayout>
      <div className="p-6 md:p-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">
            <span className="bg-gradient-primary bg-clip-text text-transparent">Painel do Desenvolvedor</span>
          </h1>
          <p className="text-muted-foreground">Gerencie seus jogos e campanhas</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {stats.map((stat, index) => (
            <Card key={index} className="p-4 bg-card/50 backdrop-blur-sm border-border/50">
              <div className="flex items-center gap-3">
                <div className={`p-2 rounded-lg ${stat.color === 'text-secondary' ? 'bg-secondary/10' : 'bg-primary/10'}`}>
                  <stat.icon className={`w-5 h-5 ${stat.color}`} />
                </div>
                <div>
                  <p className="text-2xl font-bold">{stat.value}</p>
                  <p className="text-xs text-muted-foreground">{stat.label}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Quick Actions */}
        <div className="grid md:grid-cols-2 gap-6 mb-8">
          {quickActions.map((action, index) => (
            <Card
              key={index}
              className="p-6 bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/50 transition-all cursor-pointer group"
              onClick={action.action}
            >
              <div className="flex items-start gap-4">
                <div className={`p-3 rounded-lg ${action.bgColor}`}>
                  <action.icon className={`w-6 h-6 ${action.color}`} />
                </div>
                <div className="flex-1">
                  <h3 className="font-bold text-lg mb-1 group-hover:text-primary transition-colors">
                    {action.title}
                  </h3>
                  <p className="text-sm text-muted-foreground">{action.description}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Recent Activity */}
        <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
          <h2 className="font-bold text-lg mb-4">Atividade Recente</h2>
          <div className="space-y-4">
            {recentActivity.map((activity, index) => (
              <div
                key={index}
                className="flex items-center justify-between p-3 bg-muted/30 rounded-lg"
              >
                <div>
                  <p className="font-medium">{activity.title}</p>
                  <p className="text-sm text-muted-foreground">{activity.date}</p>
                </div>
                {activity.amount && (
                  <span className="text-secondary font-bold">{activity.amount}</span>
                )}
              </div>
            ))}
          </div>
        </Card>
      </div>
    </DashboardLayout>
  );
};

export default DeveloperPanel;
