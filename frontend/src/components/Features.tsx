import { Card } from "@/components/ui/card";
import { ShoppingCart, Gift, Wrench, Shield, Tag, Star } from "lucide-react";

export const Features = () => {
  const features = [
    {
      icon: ShoppingCart,
      title: "E-Commerce Completo",
      description: "Sistema de compra seguro com carteira digital, reembolso em 24h e histórico completo de transações.",
      gradient: "bg-gradient-primary",
    },
    {
      icon: Gift,
      title: "Crowdfunding",
      description: "Apoie projetos em desenvolvimento e receba recompensas exclusivas. Estorno automático se a meta não for atingida.",
      gradient: "bg-gradient-secondary",
    },
    {
      icon: Wrench,
      title: "Oficina de Mods",
      description: "Crie, compartilhe e instale modificações. Sistema de versionamento e notificações automáticas de atualizações.",
      gradient: "bg-gradient-primary",
    },
    {
      icon: Shield,
      title: "Segurança Avançada",
      description: "Verificação automática de malware em uploads e downloads. Proteção antifraude com trava de segurança em transações.",
      gradient: "bg-gradient-secondary",
    },
    {
      icon: Tag,
      title: "Sistema de Tags",
      description: "Descubra jogos através de tags personalizadas. Siga suas tags favoritas e receba recomendações relevantes.",
      gradient: "bg-gradient-primary",
    },
    {
      icon: Star,
      title: "Reviews Verificados",
      description: "Sistema de avaliação com notas de 0 a 5. Apenas quem jogou pode avaliar, garantindo reviews autênticos.",
      gradient: "bg-gradient-secondary",
    },
  ];

  return (
    <section className="py-24 px-4">
      <div className="container mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold mb-4">
            Recursos Poderosos
          </h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Um ecossistema completo pensado para oferecer a melhor experiência tanto para jogadores quanto para desenvolvedores.
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature, index) => (
            <Card key={index} className="p-6 bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/20 hover:shadow-card transition-smooth group">
              <div className={`w-14 h-14 rounded-xl ${feature.gradient} flex items-center justify-center mb-4 group-hover:scale-110 transition-smooth`}>
                <feature.icon className="w-7 h-7 text-primary-foreground" />
              </div>
              <h3 className="font-semibold text-xl mb-3">{feature.title}</h3>
              <p className="text-muted-foreground leading-relaxed">{feature.description}</p>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};
