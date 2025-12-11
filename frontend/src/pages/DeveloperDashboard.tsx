import { useNavigate } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Upload, Rocket, Target, BarChart3 } from "lucide-react";

const DeveloperDashboard = () => {
  const navigate = useNavigate();

  const quickActions = [
    {
      title: "Upload de Jogo",
      description: "Envie o arquivo .zip do seu jogo para a plataforma",
      icon: Upload,
      color: "text-primary",
      bgColor: "bg-primary/20",
      action: () => navigate("/desenvolvedor/upload"),
    },
    {
      title: "Criar Crowdfunding",
      description: "Lance uma campanha para financiar seu projeto",
      icon: Target,
      color: "text-secondary",
      bgColor: "bg-secondary/20",
      action: () => navigate("/desenvolvedor/criar-campanha"),
    },
  ];

  return (
    <div className="min-h-screen pt-16 pb-12">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-4">
            <span className="bg-gradient-secondary bg-clip-text text-transparent">
              Painel do Desenvolvedor
            </span>
          </h1>
          <p className="text-muted-foreground">
            Gerencie seus jogos, campanhas e vendas em um só lugar.
          </p>
        </div>

        {/* Quick Actions */}
        <div className="grid md:grid-cols-2 gap-6 mb-12">
          {quickActions.map((action) => (
            <Card
              key={action.title}
              className="p-6 bg-card/50 backdrop-blur-sm hover:border-secondary/50 transition-smooth cursor-pointer"
              onClick={action.action}
            >
              <div className="flex items-start gap-4">
                <div className={`w-12 h-12 rounded-lg ${action.bgColor} flex items-center justify-center flex-shrink-0`}>
                  <action.icon className={`w-6 h-6 ${action.color}`} />
                </div>
                <div className="flex-1">
                  <h3 className="font-bold text-lg mb-2">{action.title}</h3>
                  <p className="text-sm text-muted-foreground mb-4">
                    {action.description}
                  </p>
                  <Button variant="outline" size="sm">
                    Acessar
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Stats */}
        <div className="grid md:grid-cols-3 gap-6 mb-12">
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-primary mb-2">2</div>
            <div className="text-muted-foreground">Jogos Publicados</div>
          </Card>
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-secondary mb-2">1</div>
            <div className="text-muted-foreground">Campanhas Ativas</div>
          </Card>
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-primary mb-2">
              R$ 2.450
            </div>
            <div className="text-muted-foreground">Receita Total</div>
          </Card>
        </div>

        {/* Recent Activity */}
        <Card className="p-6 bg-card/50 backdrop-blur-sm">
          <h2 className="text-xl font-bold mb-6">Atividade Recente</h2>
          <div className="space-y-4">
            <div className="flex items-center gap-4 p-4 bg-muted/30 rounded-lg">
              <div className="w-10 h-10 rounded-full bg-secondary/20 flex items-center justify-center">
                <BarChart3 className="w-5 h-5 text-secondary" />
              </div>
              <div className="flex-1">
                <p className="font-semibold">Nova venda: Cyber Knights</p>
                <p className="text-sm text-muted-foreground">Há 2 horas</p>
              </div>
              <div className="text-right">
                <p className="font-bold text-primary">+ R$ 59,90</p>
              </div>
            </div>
            <div className="flex items-center gap-4 p-4 bg-muted/30 rounded-lg">
              <div className="w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center">
                <Rocket className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1">
                <p className="font-semibold">Jogo publicado: Shadow Tactics</p>
                <p className="text-sm text-muted-foreground">Há 1 dia</p>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default DeveloperDashboard;