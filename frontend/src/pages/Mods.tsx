import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Search, Download, Star, Bell, Check } from "lucide-react";
import { mockMods } from "@/data/mockData";
import { toast } from "sonner";

const Mods = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [subscribedMods, setSubscribedMods] = useState<string[]>(['m1']);

  const filteredMods = mockMods.filter(mod =>
    mod.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    mod.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
    mod.gameName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSubscribe = (modId: string, modTitle: string) => {
    if (subscribedMods.includes(modId)) {
      setSubscribedMods(subscribedMods.filter(id => id !== modId));
      toast.info(`Inscrição em ${modTitle} cancelada`);
    } else {
      setSubscribedMods([...subscribedMods, modId]);
      toast.success(`Inscrito em ${modTitle}! Você receberá notificações de atualizações.`);
    }
  };

  const handleDownload = (modTitle: string) => {
    toast.success(`Download de ${modTitle} iniciado!`);
  };

  return (
    <div className="min-h-screen pt-16">
      {/* Header */}
      <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
        <div className="container mx-auto">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            Oficina de{" "}
            <span className="bg-gradient-primary bg-clip-text text-transparent">
              Mods
            </span>
          </h1>
          <p className="text-lg text-muted-foreground mb-8 max-w-2xl">
            Personalize seus jogos favoritos com mods criados pela comunidade. 
            Inscreva-se para receber notificações de novas versões.
          </p>

          {/* Search */}
          <div className="relative max-w-xl">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
            <Input
              type="text"
              placeholder="Buscar mods por nome ou jogo..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>
      </section>

      <div className="container mx-auto px-4 py-12">
        {/* Stats */}
        <div className="grid md:grid-cols-3 gap-6 mb-12">
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-primary mb-2">
              {mockMods.length}
            </div>
            <div className="text-muted-foreground">Mods Disponíveis</div>
          </Card>
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-secondary mb-2">
              {mockMods.reduce((sum, m) => sum + m.downloads, 0).toLocaleString('pt-BR')}
            </div>
            <div className="text-muted-foreground">Downloads Totais</div>
          </Card>
          <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
            <div className="text-3xl font-bold text-primary mb-2">
              {subscribedMods.length}
            </div>
            <div className="text-muted-foreground">Suas Inscrições</div>
          </Card>
        </div>

        {/* Mods Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredMods.map((mod) => {
            const isSubscribed = subscribedMods.includes(mod.id);
            
            return (
              <Card
                key={mod.id}
                className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/30 transition-smooth group"
              >
                {/* Cover */}
                <div className="relative h-48 overflow-hidden">
                  <img
                    src={mod.coverImage}
                    alt={mod.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-smooth"
                  />
                  {isSubscribed && (
                    <Badge className="absolute top-3 right-3 bg-gradient-secondary">
                      <Bell className="w-3 h-3 mr-1" />
                      Inscrito
                    </Badge>
                  )}
                </div>

                {/* Content */}
                <div className="p-4">
                  <Badge variant="outline" className="mb-2">
                    {mod.gameName}
                  </Badge>
                  
                  <h3 className="font-bold text-lg mb-2 group-hover:text-primary transition-smooth">
                    {mod.title}
                  </h3>
                  
                  <p className="text-muted-foreground text-sm mb-3 line-clamp-2">
                    {mod.description}
                  </p>

                  {/* Meta Info */}
                  <div className="flex items-center justify-between text-xs text-muted-foreground mb-3">
                    <span>por {mod.creatorName}</span>
                    <span>v{mod.version}</span>
                  </div>

                  {/* Stats */}
                  <div className="flex items-center gap-4 text-sm mb-4">
                    <div className="flex items-center gap-1">
                      <Star className="w-4 h-4 fill-primary text-primary" />
                      <span>{mod.rating}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Download className="w-4 h-4" />
                      <span>{(mod.downloads / 1000).toFixed(1)}k</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Bell className="w-4 h-4" />
                      <span>{(mod.subscribers / 1000).toFixed(1)}k</span>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex gap-2">
                    <Button
                      variant="hero"
                      size="sm"
                      className="flex-1"
                      onClick={() => handleDownload(mod.title)}
                    >
                      <Download className="w-4 h-4 mr-1" />
                      Baixar
                    </Button>
                    <Button
                      variant={isSubscribed ? "default" : "outline"}
                      size="sm"
                      onClick={() => handleSubscribe(mod.id, mod.title)}
                    >
                      {isSubscribed ? (
                        <Check className="w-4 h-4" />
                      ) : (
                        <Bell className="w-4 h-4" />
                      )}
                    </Button>
                  </div>

                  <p className="text-xs text-muted-foreground mt-3">
                    Atualizado em {new Date(mod.lastUpdate).toLocaleDateString('pt-BR')}
                  </p>
                </div>
              </Card>
            );
          })}
        </div>

        {/* Info Card */}
        <Card className="mt-12 p-6 bg-primary/10 border-primary/20">
          <h3 className="font-bold text-lg mb-3">💡 Sobre a Oficina de Mods</h3>
          <ul className="space-y-2 text-muted-foreground text-sm">
            <li>• Apenas jogos que você possui podem ter mods instalados</li>
            <li>• Inscreva-se em mods para receber notificações de novas versões</li>
            <li>• Desenvolvedores podem moderar e remover mods que violem diretrizes</li>
            <li>• Avalie mods após instalá-los para ajudar outros jogadores</li>
            <li>• Criadores de mods podem publicar novas versões a qualquer momento</li>
          </ul>
        </Card>
      </div>
    </div>
  );
};

export default Mods;
