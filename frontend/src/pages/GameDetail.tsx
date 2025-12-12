import { useParams, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  ShoppingCart, Download, Star, ThumbsUp, ThumbsDown, 
  Calendar, Users, Wrench, ArrowLeft, Edit, Trash2, Plus
} from "lucide-react";
import { mockGames, mockReviews, mockUserLibrary, mockCurrentUser, Review } from "@/data/mockData";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import { ReviewForm } from "@/components/ReviewForm";

const GameDetail = () => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const game = mockGames.find(g => g.slug === slug);
  const isOwned = game && mockUserLibrary.includes(game.id);
  
  const [selectedImage, setSelectedImage] = useState(0);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [editingReview, setEditingReview] = useState<Review | null>(null);
  const [ratingFilter, setRatingFilter] = useState<string>("all");
  const [sortBy, setSortBy] = useState<string>("recent");

  // Load reviews from localStorage on mount
  useEffect(() => {
    const storedReviews = localStorage.getItem('gameReviews');
    if (storedReviews) {
      setReviews(JSON.parse(storedReviews));
    } else {
      setReviews(mockReviews);
      localStorage.setItem('gameReviews', JSON.stringify(mockReviews));
    }
  }, []);

  // Save reviews to localStorage whenever they change
  useEffect(() => {
    if (reviews.length > 0) {
      localStorage.setItem('gameReviews', JSON.stringify(reviews));
    }
  }, [reviews]);

  if (!game) {
    return (
      <div className="min-h-screen pt-16 flex items-center justify-center">
        <p>Jogo não encontrado</p>
      </div>
    );
  }

  // Filter reviews: exclude deleted ones and filter by game
  const gameReviews = reviews.filter(r => r.gameId === game.id && !r.deleted);
  
  // Check if current user already has a review for this game
  const userReview = gameReviews.find(r => r.userId === mockCurrentUser.id);

  // Calculate review statistics
  const totalReviews = gameReviews.length;
  const averageRating = totalReviews > 0
    ? gameReviews.reduce((sum, r) => sum + r.rating, 0) / totalReviews
    : 0;
  const recommendedCount = gameReviews.filter(r => r.recommended).length;
  const recommendationPercentage = totalReviews > 0
    ? (recommendedCount / totalReviews) * 100
    : 0;

  // Filter and sort reviews
  let filteredReviews = [...gameReviews];
  
  if (ratingFilter !== "all") {
    const filterRating = parseInt(ratingFilter);
    filteredReviews = filteredReviews.filter(r => r.rating === filterRating);
  }

  if (sortBy === "recent") {
    filteredReviews.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  } else if (sortBy === "oldest") {
    filteredReviews.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  } else if (sortBy === "helpful") {
    filteredReviews.sort((a, b) => b.helpful - a.helpful);
  }

  const handlePurchase = () => {
    if (!isOwned) {
      if (game.price === 0) {
        toast.success(`${game.title} adicionado à sua biblioteca!`);
      } else {
        toast.success(`Compra de ${game.title} realizada com sucesso!`);
      }
      mockUserLibrary.push(game.id);
    }
  };

  const handleDownload = () => {
    toast.success(`Download de ${game.title} iniciado!`);
  };

  const handleSubmitReview = (reviewData: { rating: number; comment: string; recommended: boolean }) => {
    if (!isOwned) {
      toast.error("Você precisa ter o jogo na sua biblioteca para avaliar!");
      return;
    }

    if (editingReview) {
      // Update existing review
      const updatedReviews = reviews.map(r =>
        r.id === editingReview.id
          ? {
              ...r,
              ...reviewData,
              updatedAt: new Date().toISOString().split('T')[0],
            }
          : r
      );
      setReviews(updatedReviews);
      toast.success("Review atualizada com sucesso!");
      setEditingReview(null);
    } else {
      // Create new review
      const newReview: Review = {
        id: `r${Date.now()}`,
        gameId: game.id,
        userId: mockCurrentUser.id,
        userName: mockCurrentUser.name,
        ...reviewData,
        date: new Date().toISOString().split('T')[0],
        helpful: 0,
      };
      setReviews([...reviews, newReview]);
      toast.success("Review publicada com sucesso!");
    }
    
    setShowReviewForm(false);
  };

  const handleEditReview = () => {
    if (userReview) {
      setEditingReview(userReview);
      setShowReviewForm(true);
    }
  };

  const handleDeleteReview = () => {
    if (userReview) {
      // Soft delete - just mark as deleted
      const updatedReviews = reviews.map(r =>
        r.id === userReview.id ? { ...r, deleted: true } : r
      );
      setReviews(updatedReviews);
      toast.success("Review removida com sucesso!");
    }
  };

  const allImages = [game.coverImage, ...game.screenshots];

  return (
    <div className="min-h-screen pt-16">
      {/* Back Button */}
      <div className="container mx-auto px-4 py-4">
        <Button variant="ghost" onClick={() => navigate('/catalogo')}>
          <ArrowLeft className="w-4 h-4 mr-2" />
          Voltar ao catálogo
        </Button>
      </div>

      {/* Hero Section */}
      <section className="container mx-auto px-4 pb-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Media */}
          <div className="lg:col-span-2">
            <div className="rounded-xl overflow-hidden mb-4">
              <img
                src={allImages[selectedImage]}
                alt={game.title}
                className="w-full h-96 object-cover"
              />
            </div>
            <div className="grid grid-cols-4 gap-2">
              {allImages.map((img, idx) => (
                <div
                  key={idx}
                  className={`rounded-lg overflow-hidden cursor-pointer border-2 transition-smooth ${
                    selectedImage === idx ? 'border-primary' : 'border-transparent'
                  }`}
                  onClick={() => setSelectedImage(idx)}
                >
                  <img
                    src={img}
                    alt={`Screenshot ${idx + 1}`}
                    className="w-full h-20 object-cover"
                  />
                </div>
              ))}
            </div>
          </div>

          {/* Purchase Card */}
          <div className="lg:col-span-1">
            <Card className="p-6 bg-card/50 backdrop-blur-sm sticky top-20">
              <div className="mb-4">
                <h1 className="text-3xl font-bold mb-2">{game.title}</h1>
                <p className="text-muted-foreground">por {game.developerName}</p>
              </div>

              {isOwned ? (
                <div className="space-y-3">
                  <Badge className="w-full justify-center py-2 bg-gradient-secondary">
                    Na sua biblioteca
                  </Badge>
                  <Button variant="hero" className="w-full" onClick={handleDownload}>
                    <Download className="w-5 h-5 mr-2" />
                    Baixar
                  </Button>
                  <Button variant="outline" className="w-full" onClick={() => navigate('/mods')}>
                    <Wrench className="w-5 h-5 mr-2" />
                    Ver Mods
                  </Button>
                </div>
              ) : (
                <div className="space-y-3">
                  {game.price > 0 ? (
                    <div className="text-center py-4">
                      {game.originalPrice && (
                        <p className="text-muted-foreground line-through mb-1">
                          R$ {game.originalPrice.toFixed(2)}
                        </p>
                      )}
                      <p className="text-4xl font-bold text-primary">
                        R$ {game.price.toFixed(2)}
                      </p>
                    </div>
                  ) : (
                    <div className="text-center py-4">
                      <p className="text-4xl font-bold text-secondary">Gratuito</p>
                    </div>
                  )}
                  
                  <Button variant="hero" className="w-full" onClick={handlePurchase}>
                    <ShoppingCart className="w-5 h-5 mr-2" />
                    {game.price > 0 ? 'Comprar' : 'Adicionar à Biblioteca'}
                  </Button>
                </div>
              )}

              {/* Stats */}
              <div className="mt-6 pt-6 border-t border-border/50 space-y-3">
                <div className="flex items-center gap-2">
                  <Star className="w-5 h-5 fill-primary text-primary" />
                  <span className="font-semibold">{averageRating.toFixed(1)}</span>
                  <span className="text-muted-foreground text-sm">
                    ({totalReviews} reviews)
                  </span>
                </div>
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Calendar className="w-5 h-5" />
                  <span className="text-sm">
                    Lançamento: {new Date(game.releaseDate).toLocaleDateString('pt-BR')}
                  </span>
                </div>
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Users className="w-5 h-5" />
                  <span className="text-sm">{game.downloadCount.toLocaleString('pt-BR')} downloads</span>
                </div>
              </div>

              {/* Tags */}
              <div className="mt-6 pt-6 border-t border-border/50">
                <p className="text-sm font-medium mb-3">Tags</p>
                <div className="flex flex-wrap gap-2">
                  {game.tags.map((tag) => (
                    <Badge key={tag} variant="outline">{tag}</Badge>
                  ))}
                </div>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* Details Tabs */}
      <section className="container mx-auto px-4 py-8">
        <Tabs defaultValue="about" className="w-full">
          <TabsList className="grid w-full grid-cols-3 max-w-md">
            <TabsTrigger value="about">Sobre</TabsTrigger>
            <TabsTrigger value="reviews">Reviews ({totalReviews})</TabsTrigger>
            <TabsTrigger value="community">Comunidade</TabsTrigger>
          </TabsList>

          <TabsContent value="about" className="mt-6">
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
              <h2 className="text-2xl font-bold mb-4">Sobre o jogo</h2>
              <p className="text-muted-foreground leading-relaxed mb-6">
                {game.description}
              </p>

              {game.isEarlyAccess && (
                <div className="bg-muted/50 border border-border/50 rounded-lg p-4 mb-6">
                  <p className="font-semibold mb-2">⚠️ Acesso Antecipado</p>
                  <p className="text-sm text-muted-foreground">
                    Este jogo está em desenvolvimento ativo. Recursos e conteúdo podem mudar.
                  </p>
                </div>
              )}

              {game.modsEnabled && (
                <div className="bg-primary/10 border border-primary/20 rounded-lg p-4">
                  <p className="font-semibold mb-2">🎮 Suporte a Mods</p>
                  <p className="text-sm text-muted-foreground">
                    Este jogo tem oficina de mods ativa! Personalize sua experiência.
                  </p>
                </div>
              )}
            </Card>
          </TabsContent>

          <TabsContent value="reviews" className="mt-6">
            <div className="space-y-4">
              {/* Rating Summary */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <div className="flex items-start gap-6">
                  <div className="text-center">
                    <div className="text-5xl font-bold text-primary mb-2">
                      {averageRating.toFixed(1)}
                    </div>
                    <div className="flex gap-1 mb-2">
                      {[...Array(5)].map((_, i) => (
                        <Star 
                          key={i} 
                          className={`w-4 h-4 ${
                            i < Math.round(averageRating) 
                              ? "fill-primary text-primary" 
                              : "text-muted"
                          }`}
                        />
                      ))}
                    </div>
                    <p className="text-sm text-muted-foreground">{totalReviews} avaliações</p>
                  </div>
                  <div className="flex-1">
                    <div className="mb-4">
                      <div className="flex items-center gap-2 mb-2">
                        <ThumbsUp className="w-5 h-5 text-primary" />
                        <span className="text-lg font-semibold">
                          {recommendationPercentage.toFixed(0)}% recomendam
                        </span>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {recommendedCount} de {totalReviews} jogadores recomendam este jogo
                      </p>
                    </div>
                    {[5, 4, 3, 2, 1].map((rating) => {
                      const count = gameReviews.filter(r => r.rating === rating).length;
                      const percentage = totalReviews > 0 ? (count / totalReviews) * 100 : 0;
                      return (
                        <div key={rating} className="flex items-center gap-2 mb-2">
                          <span className="text-sm w-12">{rating} ★</span>
                          <Progress value={percentage} className="flex-1" />
                          <span className="text-sm text-muted-foreground w-12">{count}</span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </Card>

              {/* User's Review Management */}
              {isOwned && (
                <Card className="p-6 bg-card/50 backdrop-blur-sm">
                  {userReview && !showReviewForm ? (
                    <div>
                      <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-semibold">Sua Review</h3>
                        <div className="flex gap-2">
                          <Button variant="outline" size="sm" onClick={handleEditReview}>
                            <Edit className="w-4 h-4 mr-1" />
                            Editar
                          </Button>
                          <Button variant="outline" size="sm" onClick={handleDeleteReview}>
                            <Trash2 className="w-4 h-4 mr-1" />
                            Remover
                          </Button>
                        </div>
                      </div>
                      <div className="flex gap-1 mb-2">
                        {[...Array(5)].map((_, i) => (
                          <Star
                            key={i}
                            className={`w-5 h-5 ${
                              i < userReview.rating ? 'fill-primary text-primary' : 'text-muted'
                            }`}
                          />
                        ))}
                      </div>
                      <Badge variant={userReview.recommended ? "default" : "destructive"} className="mb-3">
                        {userReview.recommended ? (
                          <>
                            <ThumbsUp className="w-3 h-3 mr-1" />
                            Recomendo
                          </>
                        ) : (
                          <>
                            <ThumbsDown className="w-3 h-3 mr-1" />
                            Não recomendo
                          </>
                        )}
                      </Badge>
                      <p className="text-muted-foreground">{userReview.comment}</p>
                      {userReview.updatedAt && (
                        <p className="text-xs text-muted-foreground mt-2">
                          Editado em: {new Date(userReview.updatedAt).toLocaleDateString('pt-BR')}
                        </p>
                      )}
                    </div>
                  ) : showReviewForm ? (
                    <div>
                      <h3 className="text-lg font-semibold mb-4">
                        {editingReview ? "Editar Review" : "Escrever Review"}
                      </h3>
                      <ReviewForm
                        gameId={game.id}
                        existingReview={editingReview ? {
                          id: editingReview.id,
                          rating: editingReview.rating,
                          comment: editingReview.comment,
                          recommended: editingReview.recommended,
                        } : undefined}
                        onSubmit={handleSubmitReview}
                        onCancel={() => {
                          setShowReviewForm(false);
                          setEditingReview(null);
                        }}
                      />
                    </div>
                  ) : (
                    <Button onClick={() => setShowReviewForm(true)} className="w-full">
                      <Plus className="w-4 h-4 mr-2" />
                      Escrever Review
                    </Button>
                  )}
                </Card>
              )}

              {/* Filters */}
              <Card className="p-4 bg-card/50 backdrop-blur-sm">
                <div className="flex flex-wrap gap-4">
                  <div className="flex-1 min-w-[200px]">
                    <label className="text-sm font-medium mb-2 block">Filtrar por nota</label>
                    <Select value={ratingFilter} onValueChange={setRatingFilter}>
                      <SelectTrigger>
                        <SelectValue placeholder="Todas as notas" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">Todas as notas</SelectItem>
                        <SelectItem value="5">5 estrelas</SelectItem>
                        <SelectItem value="4">4 estrelas</SelectItem>
                        <SelectItem value="3">3 estrelas</SelectItem>
                        <SelectItem value="2">2 estrelas</SelectItem>
                        <SelectItem value="1">1 estrela</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="flex-1 min-w-[200px]">
                    <label className="text-sm font-medium mb-2 block">Ordenar por</label>
                    <Select value={sortBy} onValueChange={setSortBy}>
                      <SelectTrigger>
                        <SelectValue placeholder="Mais recentes" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="recent">Mais recentes</SelectItem>
                        <SelectItem value="oldest">Mais antigas</SelectItem>
                        <SelectItem value="helpful">Mais úteis</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </Card>

              {/* Reviews List */}
              {filteredReviews.length > 0 ? (
                filteredReviews.map((review) => (
                  <Card key={review.id} className="p-6 bg-card/50 backdrop-blur-sm">
                    <div className="flex items-start justify-between mb-3">
                      <div>
                        <p className="font-semibold">{review.userName}</p>
                        <p className="text-sm text-muted-foreground">
                          {new Date(review.date).toLocaleDateString('pt-BR')}
                          {review.updatedAt && " (editado)"}
                        </p>
                      </div>
                      <Badge variant={review.recommended ? "default" : "destructive"}>
                        {review.recommended ? (
                          <>
                            <ThumbsUp className="w-3 h-3 mr-1" />
                            Recomendo
                          </>
                        ) : (
                          <>
                            <ThumbsDown className="w-3 h-3 mr-1" />
                            Não recomendo
                          </>
                        )}
                      </Badge>
                    </div>
                    
                    <div className="flex gap-1 mb-3">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-4 h-4 ${
                            i < review.rating ? 'fill-primary text-primary' : 'text-muted'
                          }`}
                        />
                      ))}
                    </div>

                    <p className="text-muted-foreground mb-3">{review.comment}</p>

                    <div className="flex items-center gap-4 text-sm text-muted-foreground">
                      <button className="flex items-center gap-1 hover:text-foreground transition-smooth">
                        <ThumbsUp className="w-4 h-4" />
                        <span>Útil ({review.helpful})</span>
                      </button>
                    </div>
                  </Card>
                ))
              ) : (
                <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
                  <p className="text-muted-foreground">
                    Nenhuma review encontrada com os filtros selecionados.
                  </p>
                </Card>
              )}
            </div>
          </TabsContent>

          <TabsContent value="community" className="mt-6">
            <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
              <p className="text-muted-foreground">
                Posts da comunidade serão exibidos aqui. Visite a página de{" "}
                <span
                  className="text-primary cursor-pointer hover:underline"
                  onClick={() => navigate('/comunidade')}
                >
                  Comunidade
                </span>{" "}
                para ver todas as discussões.
              </p>
            </Card>
          </TabsContent>
        </Tabs>
      </section>
    </div>
  );
};

export default GameDetail;
