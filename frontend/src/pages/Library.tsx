import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Download, Play, Wrench, Star } from 'lucide-react';
import { mockGames, mockUserLibrary } from '@/data/mockData';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { DashboardLayout } from '@/layouts/DashboardLayout';

const Library = () => {
  const navigate = useNavigate();
  const userGames = mockGames.filter((game) =>
    mockUserLibrary.includes(game.id),
  );

  const handleDownload = (gameTitle: string) => {
    toast.success(`Download de ${gameTitle} iniciado!`);
  };

  const handlePlay = (gameTitle: string) => {
    toast.success(`Iniciando ${gameTitle}...`);
  };

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16">
        {/* Header */}
        <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
          <div className="container mx-auto">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              Minha{' '}
              <span className="bg-gradient-primary bg-clip-text text-transparent">
                Biblioteca
              </span>
            </h1>
            <p className="text-lg text-muted-foreground">
              {userGames.length} {userGames.length === 1 ? 'jogo' : 'jogos'} na
              sua coleção
            </p>
          </div>
        </section>

        <div className="container mx-auto px-4 py-12">
          {userGames.length === 0 ? (
            <Card className="p-12 text-center bg-card/50 backdrop-blur-sm">
              <p className="text-xl text-muted-foreground mb-4">
                Sua biblioteca está vazia
              </p>
              <p className="text-muted-foreground mb-6">
                Explore o catálogo e adicione jogos incríveis à sua coleção!
              </p>
              <Button variant="hero" onClick={() => navigate('/catalogo')}>
                Explorar Catálogo
              </Button>
            </Card>
          ) : (
            <div className="space-y-4">
              {userGames.map((game) => (
                <Card
                  key={game.id}
                  className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/30 transition-smooth"
                >
                  <div className="grid md:grid-cols-[300px_1fr] gap-6 p-6">
                    {/* Game Cover */}
                    <div
                      className="relative h-48 md:h-auto rounded-lg overflow-hidden cursor-pointer group"
                      onClick={() => navigate(`/jogo/${game.slug}`)}
                    >
                      <img
                        src={game.coverImage}
                        alt={game.title}
                        className="w-full h-full object-cover group-hover:scale-105 transition-smooth"
                      />
                      <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-smooth flex items-center justify-center">
                        <Play className="w-12 h-12 text-white opacity-0 group-hover:opacity-100 transition-smooth" />
                      </div>
                    </div>

                    {/* Game Info */}
                    <div className="flex flex-col justify-between">
                      <div>
                        <div className="flex items-start justify-between mb-3">
                          <div>
                            <h2
                              className="text-2xl font-bold mb-1 cursor-pointer hover:text-primary transition-smooth"
                              onClick={() => navigate(`/jogo/${game.slug}`)}
                            >
                              {game.title}
                            </h2>
                            <p className="text-muted-foreground text-sm">
                              por {game.developerName}
                            </p>
                          </div>
                          <div className="flex items-center gap-1">
                            <Star className="w-5 h-5 fill-primary text-primary" />
                            <span className="font-semibold">{game.rating}</span>
                          </div>
                        </div>

                        <p className="text-muted-foreground mb-4 line-clamp-2">
                          {game.description}
                        </p>

                        {/* Tags */}
                        <div className="flex flex-wrap gap-2 mb-4">
                          {game.tags.slice(0, 4).map((tag) => (
                            <Badge key={tag} variant="outline">
                              {tag}
                            </Badge>
                          ))}
                        </div>
                      </div>

                      {/* Actions */}
                      <div className="flex flex-wrap gap-3">
                        <Button
                          variant="hero"
                          onClick={() => handlePlay(game.title)}
                        >
                          <Play className="w-4 h-4 mr-2" />
                          Jogar
                        </Button>
                        <Button
                          variant="outline"
                          onClick={() => handleDownload(game.title)}
                        >
                          <Download className="w-4 h-4 mr-2" />
                          Baixar
                        </Button>
                        {game.modsEnabled && (
                          <Button
                            variant="outline"
                            onClick={() => navigate('/mods')}
                          >
                            <Wrench className="w-4 h-4 mr-2" />
                            Mods
                          </Button>
                        )}
                        <Button
                          variant="ghost"
                          onClick={() => navigate(`/jogo/${game.slug}`)}
                        >
                          Ver página
                        </Button>
                      </div>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Library;
