import { Button } from "@/components/ui/button";
import { Gamepad2, Sparkles } from "lucide-react";
import heroBanner from "@/assets/hero-banner.jpg";
import { useNavigate } from "react-router-dom";

export const Hero = () => {
  const navigate = useNavigate();
  
  return (
    <section className="relative min-h-[80vh] flex items-center justify-center overflow-hidden">
      {/* Background Image with Overlay */}
      <div className="absolute inset-0 z-0">
        <img
          src={heroBanner}
          alt="Ludum - Plataforma de Jogos"
          className="w-full h-full object-cover opacity-40"
        />
        <div className="absolute inset-0 bg-gradient-hero" />
      </div>

      {/* Content */}
      <div className="container relative z-10 mx-auto px-4 text-center">
        <div className="flex items-center justify-center gap-2 mb-6">
          <Gamepad2 className="w-12 h-12 text-primary-glow" />
          <h1 className="text-6xl md:text-7xl font-bold bg-gradient-primary bg-clip-text text-transparent">
            Ludum
          </h1>
        </div>
        
        <p className="text-xl md:text-2xl text-muted-foreground mb-4 max-w-2xl mx-auto">
          A plataforma que conecta jogadores e desenvolvedores em um ecossistema completo de jogos digitais
        </p>
        
        <p className="text-lg text-muted-foreground/80 mb-8 max-w-3xl mx-auto">
          Descubra, jogue, compartilhe e crie. Apoie desenvolvedores indie através de crowdfunding, 
          personalize com mods e faça parte de uma comunidade vibrante.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Button variant="hero" size="lg" onClick={() => navigate('/catalogo')}>
            <Sparkles className="w-5 h-5" />
            Explorar Catálogo
          </Button>
          <Button variant="outline" size="lg" onClick={() => navigate('/catalogo')}>
            Sou Desenvolvedor
          </Button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-8 mt-16 max-w-2xl mx-auto">
          <div className="text-center">
            <div className="text-3xl font-bold text-primary-glow mb-1">10K+</div>
            <div className="text-sm text-muted-foreground">Jogos</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-secondary mb-1">50K+</div>
            <div className="text-sm text-muted-foreground">Jogadores</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-primary-glow mb-1">2K+</div>
            <div className="text-sm text-muted-foreground">Desenvolvedores</div>
          </div>
        </div>
      </div>
    </section>
  );
};
