import { Card } from "@/components/ui/card";
import { Upload, TrendingUp, Wallet, Users } from "lucide-react";
import developersIcon from "@/assets/developers-icon.jpg";

export const ForDevelopers = () => {
  const features = [
    {
      icon: Upload,
      title: "Publicação Simplificada",
      description: "Faça upload do seu jogo com validação automática de manifesto e verificação de segurança integrada.",
    },
    {
      icon: TrendingUp,
      title: "Crowdfunding Integrado",
      description: "Crie campanhas para financiar seu projeto antes do lançamento. Alcance sua meta e realize seu sonho.",
    },
    {
      icon: Wallet,
      title: "Monetização Transparente",
      description: "Sistema de carteira com saque transparente. Valores de vendas disponíveis em 24h após compra confirmada.",
    },
    {
      icon: Users,
      title: "Comunidade Engajada",
      description: "Conecte-se diretamente com seus jogadores através de posts, reviews e suporte à Oficina de Mods.",
    },
  ];

  return (
    <section className="py-24 px-4 bg-muted/30">
      <div className="container mx-auto">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Content */}
          <div>
            <h2 className="text-4xl md:text-5xl font-bold mb-4">
              Para{" "}
              <span className="bg-gradient-primary bg-clip-text text-transparent">
                Desenvolvedores
              </span>
            </h2>
            <p className="text-lg text-muted-foreground mb-8">
              Todas as ferramentas que você precisa para criar, publicar, financiar e monetizar seus jogos.
            </p>

            <div className="grid gap-6">
              {features.map((feature, index) => (
                <Card key={index} className="p-6 bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/30 transition-smooth">
                  <div className="flex gap-4">
                    <div className="flex-shrink-0">
                      <div className="w-12 h-12 rounded-lg bg-gradient-primary flex items-center justify-center">
                        <feature.icon className="w-6 h-6 text-primary-foreground" />
                      </div>
                    </div>
                    <div>
                      <h3 className="font-semibold text-lg mb-2">{feature.title}</h3>
                      <p className="text-muted-foreground">{feature.description}</p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          </div>

          {/* Image */}
          <div>
            <img
              src={developersIcon}
              alt="Para Desenvolvedores"
              className="w-full max-w-md mx-auto rounded-2xl shadow-card"
            />
          </div>
        </div>
      </div>
    </section>
  );
};
