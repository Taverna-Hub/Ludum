import { Button } from "@/components/ui/button";
import { Rocket, Code } from "lucide-react";

export const CTA = () => {
  return (
    <section className="py-24 px-4">
      <div className="container mx-auto">
        <div className="relative overflow-hidden rounded-3xl bg-gradient-hero border border-primary/20 shadow-glow">
          <div className="absolute inset-0 bg-card/80 backdrop-blur-xl" />
          
          <div className="relative z-10 px-8 py-16 md:py-24 text-center">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Pronto para começar sua jornada na{" "}
              <span className="bg-gradient-primary bg-clip-text text-transparent">
                Ludum
              </span>
              ?
            </h2>
            
            <p className="text-lg text-muted-foreground mb-10 max-w-2xl mx-auto">
              Junte-se à nossa comunidade e descubra um universo de possibilidades, seja você um jogador apaixonado ou um desenvolvedor talentoso.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              <Button variant="hero" size="lg">
                <Rocket className="w-5 h-5" />
                Criar Conta de Jogador
              </Button>
              <Button variant="accent" size="lg">
                <Code className="w-5 h-5" />
                Criar Conta de Desenvolvedor
              </Button>
            </div>

            <p className="text-sm text-muted-foreground/70 mt-8">
              Gratuito para começar. Sem taxas ocultas.
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};
