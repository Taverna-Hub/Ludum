import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Search, Star, TrendingUp, Filter } from 'lucide-react';
import { mockGames } from '@/data/mockData';
import { useNavigate } from 'react-router-dom';
import { DashboardLayout } from '@/layouts/DashboardLayout';

const Catalog = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTag, setSelectedTag] = useState<string | null>(null);

  // Get all unique tags
  const allTags = Array.from(new Set(mockGames.flatMap((game) => game.tags)));

  // Filter games
  const filteredGames = mockGames.filter((game) => {
    const matchesSearch =
      game.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      game.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesTag = !selectedTag || game.tags.includes(selectedTag);
    return matchesSearch && matchesTag;
  });

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16">
        {/* Header */}
        <section className="bg-muted/30 py-12 px-4">
          <div className="container mx-auto">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              Catálogo de{' '}
              <span className="bg-gradient-primary bg-clip-text text-transparent">
                Jogos
              </span>
            </h1>
            <p className="text-lg text-muted-foreground mb-8 max-w-2xl">
              Descubra jogos indie incríveis criados por desenvolvedores
              talentosos
            </p>

            {/* Search */}
            <div className="relative max-w-xl">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
              <Input
                type="text"
                placeholder="Buscar jogos..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </section>

        <div className="container mx-auto px-4 py-12">
          <div className="grid lg:grid-cols-4 gap-8">
            {/* Filters Sidebar */}
            <aside className="lg:col-span-1">
              <Card className="p-6 bg-card/50 backdrop-blur-sm sticky top-20">
                <div className="flex items-center gap-2 mb-4">
                  <Filter className="w-5 h-5 text-primary" />
                  <h2 className="font-semibold text-lg">Filtros</h2>
                </div>

                <div className="space-y-4">
                  <div>
                    <h3 className="text-sm font-medium mb-3">Tags</h3>
                    <div className="flex flex-wrap gap-2">
                      <Badge
                        variant={!selectedTag ? 'default' : 'outline'}
                        className="cursor-pointer"
                        onClick={() => setSelectedTag(null)}
                      >
                        Todas
                      </Badge>
                      {allTags.map((tag) => (
                        <Badge
                          key={tag}
                          variant={selectedTag === tag ? 'default' : 'outline'}
                          className="cursor-pointer"
                          onClick={() => setSelectedTag(tag)}
                        >
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </div>
              </Card>
            </aside>

            {/* Games Grid */}
            <div className="lg:col-span-3">
              <div className="flex items-center justify-between mb-6">
                <p className="text-muted-foreground">
                  {filteredGames.length}{' '}
                  {filteredGames.length === 1
                    ? 'jogo encontrado'
                    : 'jogos encontrados'}
                </p>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                {filteredGames.map((game) => (
                  <Card
                    key={game.id}
                    className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-primary/30 transition-smooth cursor-pointer group"
                    onClick={() => navigate(`/jogo/${game.slug}`)}
                  >
                    {/* Cover Image */}
                    <div className="relative h-48 overflow-hidden">
                      <img
                        src={game.coverImage}
                        alt={game.title}
                        className="w-full h-full object-cover group-hover:scale-105 transition-smooth"
                      />
                      {game.isEarlyAccess && (
                        <Badge className="absolute top-3 right-3 bg-secondary">
                          Acesso Antecipado
                        </Badge>
                      )}
                      {game.price === 0 && (
                        <Badge className="absolute top-3 left-3 bg-gradient-secondary">
                          Grátis
                        </Badge>
                      )}
                    </div>

                    {/* Content */}
                    <div className="p-4">
                      <h3 className="font-bold text-xl mb-2 group-hover:text-primary transition-smooth">
                        {game.title}
                      </h3>

                      <p className="text-muted-foreground text-sm mb-3 line-clamp-2">
                        {game.description}
                      </p>

                      {/* Tags */}
                      <div className="flex flex-wrap gap-1 mb-3">
                        {game.tags.slice(0, 3).map((tag) => (
                          <Badge
                            key={tag}
                            variant="outline"
                            className="text-xs"
                          >
                            {tag}
                          </Badge>
                        ))}
                      </div>

                      {/* Stats */}
                      <div className="flex items-center gap-4 text-sm text-muted-foreground mb-3">
                        <div className="flex items-center gap-1">
                          <Star className="w-4 h-4 fill-primary text-primary" />
                          <span>{game.rating}</span>
                          <span className="text-xs">({game.reviewCount})</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <TrendingUp className="w-4 h-4" />
                          <span>
                            {(game.downloadCount / 1000).toFixed(1)}k downloads
                          </span>
                        </div>
                      </div>

                      {/* Price & Developer */}
                      <div className="flex items-center justify-between pt-3 border-t border-border/50">
                        <div>
                          <p className="text-xs text-muted-foreground mb-1">
                            por {game.developerName}
                          </p>
                          {game.price > 0 ? (
                            <div className="flex items-center gap-2">
                              {game.originalPrice && (
                                <span className="text-sm text-muted-foreground line-through">
                                  R$ {game.originalPrice.toFixed(2)}
                                </span>
                              )}
                              <span className="font-bold text-lg text-primary">
                                R$ {game.price.toFixed(2)}
                              </span>
                            </div>
                          ) : (
                            <span className="font-bold text-lg text-secondary">
                              Gratuito
                            </span>
                          )}
                        </div>
                        <Button variant="hero" size="sm">
                          Ver mais
                        </Button>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Catalog;
