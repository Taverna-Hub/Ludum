import { Card } from "@/components/ui/card";
import { Library, Heart, MessageSquare, Download } from "lucide-react";
import playersIcon from "@/assets/players-icon.jpg";

export const ForPlayers = () => {
  const features = [
    {
      icon: Library,
      title: "Biblioteca Completa",
      description: "Organize e acesse todos os seus jogos em um só lugar. Sincronize progresso e conquistas.",
    },
    {
      icon: Heart,
      title: "Apoie Desenvolvedores",
      description: "Participe de campanhas de crowdfunding e ajude seus jogos favoritos a se tornarem realidade.",
    },
    {
      icon: MessageSquare,
      title: "Reviews e Comunidade",
      description: "Compartilhe suas opiniões, publique posts e conecte-se com outros jogadores apaixonados.",
    },
    {
      icon: Download,
      title: "Mods e Personalização",
      description: "Explore a Oficina de Mods e transforme a experiência dos seus jogos favoritos.",
    },
  ];

  return (
    <section className="py-24 px-4">
      <div className="container mx-auto">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Image */}
          <div className="order-2 lg:order-1">
            <img
              src={playersIcon}
              alt="Para Jogadores"
              className="w-full max-w-md mx-auto rounded-2xl shadow-card"
            />
          </div>

          {/* Content */}
          <div className="order-1 lg:order-2">
            <h2 className="text-4xl md:text-5xl font-bold mb-4">
              Para{" "}
              <span className="bg-gradient-secondary bg-clip-text text-transparent">
                Jogadores
              </span>
            </h2>
            <p className="text-lg text-muted-foreground mb-8">
              Uma experiência completa de descoberta, aquisição e engajamento com os melhores jogos indie.
            </p>

            <div className="grid gap-6">
              {features.map((feature, index) => (
                <Card key={index} className="p-6 bg-card/50 backdrop-blur-sm border-border/50 hover:border-secondary/30 transition-smooth">
                  <div className="flex gap-4">
                    <div className="flex-shrink-0">
                      <div className="w-12 h-12 rounded-lg bg-gradient-secondary flex items-center justify-center">
                        <feature.icon className="w-6 h-6 text-secondary-foreground" />
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
        </div>
      </div>
    </section>
  );
};
