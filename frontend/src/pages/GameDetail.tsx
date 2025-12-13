import { useParams, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  ShoppingCart,
  Download,
  Star,
  ThumbsUp,
  ThumbsDown,
  Calendar,
  Users,
  Wrench,
  ArrowLeft,
  Edit,
  Trash2,
  Plus,
  CheckCircle,
  Loader2,
  Heart,
} from "lucide-react";
import { mockUserLibrary } from "@/data/mockData";
import { Game } from "@/types/game";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import { ReviewForm } from "@/components/ReviewForm";
import { PurchaseConfirmModal } from "@/components/PurchaseConfirmModal";
import { InsufficientBalanceModal } from "@/components/InsufficientBalanceModal";
import { comprarJogo } from "@/http/requests/carteiraRequests";
import {
  criarReview,
  editarReview,
  removerReview,
  listarReviews,
  obterResumoReviews,
  ReviewFrontend,
  transformReviewResponse,
} from "@/http/requests/reviewRequests";
import { obterJogo } from "@/http/requests/jogoRequests";
import {
  adicionarJogo,
  verificarPosse,
} from "@/http/requests/bibliotecaRequests";
import { postRequests, PostResponse } from "@/http/requests/postRequests";
import { useAuthContext } from "@/contexts/AuthContext";
import { useSeguimento } from "@/hooks/useSeguimento";

// Tipo local para Review do frontend
interface Review extends ReviewFrontend {}

const GameDetail = () => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { user } = useAuthContext();

  // Estado para o jogo carregado da API
  const [game, setGame] = useState<Game | null>(null);
  const [gameLoading, setGameLoading] = useState(true);
  const [gameError, setGameError] = useState<string | null>(null);

  const [selectedImage, setSelectedImage] = useState(0);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [editingReview, setEditingReview] = useState<Review | null>(null);
  const [ratingFilter, setRatingFilter] = useState<string>("all");
  const [sortBy, setSortBy] = useState<string>("recent");
  const [showPurchaseModal, setShowPurchaseModal] = useState(false);
  const [purchaseLoading, setPurchaseLoading] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [showInsufficientBalanceModal, setShowInsufficientBalanceModal] =
    useState(false);
  const [balanceInfo, setBalanceInfo] = useState({ current: 0, missing: 0 });

  // Estados para reviews da API
  const [reviewsLoading, setReviewsLoading] = useState(true);
  const [reviewResumo, setReviewResumo] = useState({
    mediaEstrelas: 0,
    totalRecomendacoes: 0,
    porcentagemRecomendacoes: 0,
  });
  const [submittingReview, setSubmittingReview] = useState(false);

  // Estados para posts da comunidade
  const [gamePosts, setGamePosts] = useState<PostResponse[]>([]);
  const [postsLoading, setPostsLoading] = useState(true);

  // Hook de seguimento
  const {
    loading: seguimentoLoading,
    verificarSeguindo,
    buscarContadorSeguidores,
    toggleSeguir,
    isSeguindo,
    getContadorSeguidores,
  } = useSeguimento();

  // Verificar se o jogo é possuído (depois de carregar o game)
  const [isOwned, setIsOwned] = useState(false);

  useEffect(() => {
    const checkOwnership = async () => {
      if (game && user) {
        try {
          const owned = await verificarPosse(game.id, user.id);
          setIsOwned(owned);
        } catch (error) {
          console.error("Erro ao verificar posse:", error);
        }
      }
    };
    checkOwnership();
  }, [game, user]);

  // Carregar jogo da API
  useEffect(() => {
    const carregarJogo = async () => {
      if (!slug) return;

      setGameLoading(true);
      setGameError(null);

      try {
        const jogoData = await obterJogo(slug);
        setGame(jogoData);
      } catch (error) {
        console.error("Erro ao carregar jogo:", error);
        setGameError("Jogo não encontrado");
      } finally {
        setGameLoading(false);
      }
    };

    carregarJogo();
  }, [slug]);

  // Verificar se está seguindo o jogo e buscar contador de seguidores
  useEffect(() => {
    if (game?.id && user) {
      verificarSeguindo(game.id);
      buscarContadorSeguidores(game.id);
    } else if (game?.id) {
      buscarContadorSeguidores(game.id);
    }
  }, [game?.id, user]);

  // Carregar reviews da API
  const carregarReviews = async () => {
    if (!game) return;

    setReviewsLoading(true);
    try {
      const [reviewsData, resumoData] = await Promise.all([
        listarReviews(game.id, {
          ordenarPorData: true,
          maisRecentes: sortBy === "recent",
        }),
        obterResumoReviews(game.id),
      ]);

      const transformedReviews = reviewsData.map((r) =>
        transformReviewResponse(r)
      );
      setReviews(transformedReviews);
      setReviewResumo(resumoData);
    } catch (error) {
      console.error("Erro ao carregar reviews:", error);
      // Fallback para lista vazia se houver erro
      setReviews([]);
    } finally {
      setReviewsLoading(false);
    }
  };

  useEffect(() => {
    carregarReviews();
  }, [game?.id, sortBy]);

  useEffect(() => {
    const carregarPosts = async () => {
      if (!game?.id) return;

      setPostsLoading(true);
      try {
        const postsPublicados = await postRequests.obterPostsPorStatus(
          "PUBLICADO"
        );
        const postsFiltrados = postsPublicados.filter(
          (post) => post.jogoId === game.id
        );
        setGamePosts(postsFiltrados);
      } catch (error) {
        console.error("Erro ao carregar posts:", error);
        setGamePosts([]);
      } finally {
        setPostsLoading(false);
      }
    };

    carregarPosts();
  }, [game?.id]);

  // Loading state
  if (gameLoading) {
    return (
      <div className="min-h-screen pt-16 flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-ludum-blue" />
        <span className="ml-2 text-white">Carregando jogo...</span>
      </div>
    );
  }

  // Error state
  if (gameError || !game) {
    return (
      <div className="min-h-screen pt-16 flex items-center justify-center">
        <p className="text-red-400">{gameError || "Jogo não encontrado"}</p>
      </div>
    );
  }

  // Filter reviews: exclude deleted ones and filter by game
  const gameReviews = reviews.filter((r) => r.gameId === game.id && !r.deleted);

  // Check if current user already has a review for this game
  const userReview = user
    ? gameReviews.find((r) => r.userId === user.id)
    : null;

  // Use dados do resumo da API
  const totalReviews = gameReviews.length;
  const averageRating = reviewResumo.mediaEstrelas;
  const recommendedCount = reviewResumo.totalRecomendacoes;
  const recommendationPercentage = reviewResumo.porcentagemRecomendacoes;

  // Filter and sort reviews
  let filteredReviews = [...gameReviews];

  if (ratingFilter !== "all") {
    const filterRating = parseInt(ratingFilter);
    filteredReviews = filteredReviews.filter((r) => r.rating === filterRating);
  }

  if (sortBy === "recent") {
    filteredReviews.sort(
      (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
    );
  } else if (sortBy === "oldest") {
    filteredReviews.sort(
      (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
    );
  } else if (sortBy === "helpful") {
    filteredReviews.sort((a, b) => b.helpful - a.helpful);
  }

  const handlePurchase = async () => {
    if (!isOwned) {
      if (game.price === 0) {
        if (!user) {
          toast.error("Faça login para adicionar jogos à biblioteca");
          return;
        }
        try {
          await adicionarJogo({
            jogoId: game.id,
            contaId: user.id,
            modeloDeAcesso: "GRATUITO",
          });
          toast.success(`${game.title} adicionado à sua biblioteca!`);
          setIsOwned(true);
        } catch (e) {
          toast.error("Erro ao adicionar jogo à biblioteca");
          console.error(e);
        }
      } else {
        setShowPurchaseModal(true);
      }
    }
  };

  const handleConfirmPurchase = async () => {
    if (!user?.id || !game) return;

    setPurchaseLoading(true);

    try {
      const response = await comprarJogo({
        jogoId: game.id,
        compradorId: user.id,
        desenvolvedoraId: game.developerId,
        valor: game.price,
      });

      if (response.sucesso) {
        mockUserLibrary.push(game.id);
        setShowPurchaseModal(false);
        setShowSuccessModal(true);
      } else {
        toast.error(response.mensagem || "Erro ao processar compra");
      }
    } catch (error) {
      const errorData = error.response.data;
      if (errorData.valorFaltante !== undefined) {
        // Saldo insuficiente
        setBalanceInfo({
          current: errorData.saldoAtual,
          missing: errorData.valorFaltante,
        });
        setShowPurchaseModal(false);
        setShowInsufficientBalanceModal(true);
      }
    } finally {
      setPurchaseLoading(false);
    }
  };

  const handleAddFunds = () => {
    setShowInsufficientBalanceModal(false);
    // Redirecionar para página de pagamento com o valor faltante
    navigate(
      `/painel/carteira/adicionar?amount=${balanceInfo.missing}&returnTo=/jogo/${slug}`
    );
  };

  const handleDownload = () => {
    toast.success(`Download de ${game.title} iniciado!`);
  };

  const handleSubmitReview = async (reviewData: {
    rating: number;
    title: string;
    comment: string;
    recommended: boolean;
  }) => {
    if (!isOwned) {
      toast.error("Você precisa ter o jogo na sua biblioteca para avaliar!");
      return;
    }

    if (!user) {
      toast.error("Você precisa estar logado para avaliar!");
      return;
    }

    setSubmittingReview(true);

    try {
      if (editingReview) {
        // Editar review existente
        await editarReview(editingReview.id, {
          nota: reviewData.rating,
          titulo: reviewData.title,
          texto: reviewData.comment,
          recomenda: reviewData.recommended,
        });
        toast.success("Review atualizada com sucesso!");
        setEditingReview(null);
      } else {
        // Criar nova review
        await criarReview(game.id, {
          nota: reviewData.rating,
          titulo: reviewData.title,
          texto: reviewData.comment,
          recomenda: reviewData.recommended,
        });
        toast.success("Review publicada com sucesso!");
      }

      setShowReviewForm(false);
      // Recarregar reviews
      await carregarReviews();
    } catch (error: any) {
      console.error("Erro ao enviar review:", error);
      const mensagem =
        error.response?.data?.message ||
        error.response?.data ||
        "Erro ao enviar review";
      toast.error(
        typeof mensagem === "string" ? mensagem : "Erro ao enviar review"
      );
    } finally {
      setSubmittingReview(false);
    }
  };

  const handleEditReview = () => {
    if (userReview) {
      setEditingReview(userReview);
      setShowReviewForm(true);
    }
  };

  const handleDeleteReview = async () => {
    if (!userReview) return;

    try {
      await removerReview(userReview.id);
      toast.success("Review removida com sucesso!");
      await carregarReviews();
    } catch (error: any) {
      console.error("Erro ao remover review:", error);
      toast.error("Erro ao remover review");
    }
  };

  const allImages = [game.coverImage, ...game.screenshots];

  return (
    <div className="min-h-screen pt-16">
      {/* Back Button */}
      <div className="container mx-auto px-4 py-4">
        <Button variant="ghost" onClick={() => navigate("/catalogo")}>
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
                    selectedImage === idx
                      ? "border-primary"
                      : "border-transparent"
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
                <p className="text-muted-foreground">
                  por {game.developerName}
                </p>
              </div>

              {isOwned ? (
                <div className="space-y-3">
                  <Badge className="w-full justify-center py-2 bg-gradient-secondary">
                    Na sua biblioteca
                  </Badge>
                  <Button
                    variant="hero"
                    className="w-full"
                    onClick={handleDownload}
                  >
                    <Download className="w-5 h-5 mr-2" />
                    Baixar
                  </Button>
                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => navigate("/mods")}
                  >
                    <Wrench className="w-5 h-5 mr-2" />
                    Ver Mods
                  </Button>

                  {/* Botão Seguir Jogo - também disponível para jogos na biblioteca */}
                  <Button
                    variant={isSeguindo(game.id) ? "outline" : "secondary"}
                    className="w-full"
                    onClick={() => {
                      if (!user) {
                        toast.error("Faça login para seguir jogos");
                        return;
                      }
                      toggleSeguir(game.id, "JOGO", game.title);
                    }}
                    disabled={seguimentoLoading}
                  >
                    {seguimentoLoading ? (
                      <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                    ) : (
                      <Heart
                        className={`w-5 h-5 mr-2 ${
                          isSeguindo(game.id) ? "fill-current text-red-500" : ""
                        }`}
                      />
                    )}
                    {isSeguindo(game.id) ? "Seguindo" : "Seguir"}
                    <span className="ml-1 text-xs text-muted-foreground">
                      ({getContadorSeguidores(game.id)})
                    </span>
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
                      <p className="text-4xl font-bold text-secondary">
                        Gratuito
                      </p>
                    </div>
                  )}

                  <Button
                    variant="hero"
                    className="w-full"
                    onClick={handlePurchase}
                  >
                    <ShoppingCart className="w-5 h-5 mr-2" />
                    {game.price > 0 ? "Comprar" : "Adicionar à Biblioteca"}
                  </Button>

                  {/* Botão Seguir Jogo */}
                  <Button
                    variant={isSeguindo(game.id) ? "outline" : "secondary"}
                    className="w-full"
                    onClick={() => {
                      if (!user) {
                        toast.error("Faça login para seguir jogos");
                        return;
                      }
                      toggleSeguir(game.id, "JOGO", game.title);
                    }}
                    disabled={seguimentoLoading}
                  >
                    {seguimentoLoading ? (
                      <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                    ) : (
                      <Heart
                        className={`w-5 h-5 mr-2 ${
                          isSeguindo(game.id) ? "fill-current text-red-500" : ""
                        }`}
                      />
                    )}
                    {isSeguindo(game.id) ? "Seguindo" : "Seguir"}
                    <span className="ml-1 text-xs text-muted-foreground">
                      ({getContadorSeguidores(game.id)})
                    </span>
                  </Button>
                </div>
              )}

              {/* Stats */}
              <div className="mt-6 pt-6 border-t border-border/50 space-y-3">
                <div className="flex items-center gap-2">
                  <Star className="w-5 h-5 fill-primary text-primary" />
                  <span className="font-semibold">
                    {averageRating.toFixed(1)}
                  </span>
                  <span className="text-muted-foreground text-sm">
                    ({totalReviews} reviews)
                  </span>
                </div>
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Calendar className="w-5 h-5" />
                  <span className="text-sm">
                    Lançamento:{" "}
                    {new Date(game.releaseDate).toLocaleDateString("pt-BR")}
                  </span>
                </div>
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Users className="w-5 h-5" />
                  <span className="text-sm">
                    {game.downloadCount.toLocaleString("pt-BR")} downloads
                  </span>
                </div>
              </div>

              {/* Tags */}
              <div className="mt-6 pt-6 border-t border-border/50">
                <p className="text-sm font-medium mb-3">Tags</p>
                <div className="flex flex-wrap gap-2">
                  {game.tags.map((tag) => (
                    <Badge key={tag} variant="outline">
                      {tag}
                    </Badge>
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
                    Este jogo está em desenvolvimento ativo. Recursos e conteúdo
                    podem mudar.
                  </p>
                </div>
              )}

              {game.modsEnabled && (
                <div className="bg-primary/10 border border-primary/20 rounded-lg p-4">
                  <p className="font-semibold mb-2">🎮 Suporte a Mods</p>
                  <p className="text-sm text-muted-foreground">
                    Este jogo tem oficina de mods ativa! Personalize sua
                    experiência.
                  </p>
                </div>
              )}
            </Card>
          </TabsContent>

          <TabsContent value="reviews" className="mt-6">
            <div className="space-y-4">
              {/* Rating Summary */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                {reviewsLoading ? (
                  <div className="flex items-center justify-center py-8">
                    <Loader2 className="w-8 h-8 animate-spin text-primary" />
                  </div>
                ) : (
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
                      <p className="text-sm text-muted-foreground">
                        {totalReviews} avaliações
                      </p>
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
                          {recommendedCount} de {totalReviews} jogadores
                          recomendam este jogo
                        </p>
                      </div>
                      {[5, 4, 3, 2, 1].map((rating) => {
                        const count = gameReviews.filter(
                          (r) => r.rating === rating
                        ).length;
                        const percentage =
                          totalReviews > 0 ? (count / totalReviews) * 100 : 0;
                        return (
                          <div
                            key={rating}
                            className="flex items-center gap-2 mb-2"
                          >
                            <span className="text-sm w-12">{rating} ★</span>
                            <Progress value={percentage} className="flex-1" />
                            <span className="text-sm text-muted-foreground w-12">
                              {count}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}
              </Card>

              {/* User's Review Management */}
              {isOwned && user && (
                <Card className="p-6 bg-card/50 backdrop-blur-sm">
                  {userReview && !showReviewForm ? (
                    <div>
                      <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-semibold">Sua Review</h3>
                        <div className="flex gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={handleEditReview}
                          >
                            <Edit className="w-4 h-4 mr-1" />
                            Editar
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={handleDeleteReview}
                          >
                            <Trash2 className="w-4 h-4 mr-1" />
                            Remover
                          </Button>
                        </div>
                      </div>
                      <h4 className="font-medium text-lg mb-2">
                        {userReview.title}
                      </h4>
                      <div className="flex gap-1 mb-2">
                        {[...Array(5)].map((_, i) => (
                          <Star
                            key={i}
                            className={`w-5 h-5 ${
                              i < userReview.rating
                                ? "fill-primary text-primary"
                                : "text-muted"
                            }`}
                          />
                        ))}
                      </div>
                      <Badge
                        variant={
                          userReview.recommended ? "default" : "destructive"
                        }
                        className="mb-3"
                      >
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
                      <p className="text-muted-foreground">
                        {userReview.comment}
                      </p>
                      {userReview.updatedAt && (
                        <p className="text-xs text-muted-foreground mt-2">
                          Editado em:{" "}
                          {new Date(userReview.updatedAt).toLocaleDateString(
                            "pt-BR"
                          )}
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
                        existingReview={
                          editingReview
                            ? {
                                id: editingReview.id,
                                rating: editingReview.rating,
                                title: editingReview.title || "",
                                comment: editingReview.comment,
                                recommended: editingReview.recommended,
                              }
                            : undefined
                        }
                        onSubmit={handleSubmitReview}
                        onCancel={() => {
                          setShowReviewForm(false);
                          setEditingReview(null);
                        }}
                      />
                      {submittingReview && (
                        <div className="flex items-center justify-center mt-4">
                          <Loader2 className="w-6 h-6 animate-spin text-primary mr-2" />
                          <span>Enviando...</span>
                        </div>
                      )}
                    </div>
                  ) : (
                    <Button
                      onClick={() => setShowReviewForm(true)}
                      className="w-full"
                    >
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
                    <label className="text-sm font-medium mb-2 block">
                      Filtrar por nota
                    </label>
                    <Select
                      value={ratingFilter}
                      onValueChange={setRatingFilter}
                    >
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
                    <label className="text-sm font-medium mb-2 block">
                      Ordenar por
                    </label>
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
              {reviewsLoading ? (
                <Card className="p-6 bg-card/50 backdrop-blur-sm">
                  <div className="flex items-center justify-center py-8">
                    <Loader2 className="w-8 h-8 animate-spin text-primary" />
                    <span className="ml-2">Carregando reviews...</span>
                  </div>
                </Card>
              ) : filteredReviews.length > 0 ? (
                filteredReviews.map((review) => (
                  <Card
                    key={review.id}
                    className="p-6 bg-card/50 backdrop-blur-sm"
                  >
                    <div className="flex items-start justify-between mb-3">
                      <div>
                        <p className="font-semibold">{review.userName}</p>
                        <p className="text-sm text-muted-foreground">
                          {new Date(review.date).toLocaleDateString("pt-BR")}
                          {review.updatedAt && " (editado)"}
                        </p>
                      </div>
                      <Badge
                        variant={review.recommended ? "default" : "destructive"}
                      >
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

                    {review.title && (
                      <h4 className="font-medium text-lg mb-2">
                        {review.title}
                      </h4>
                    )}

                    <div className="flex gap-1 mb-3">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-4 h-4 ${
                            i < review.rating
                              ? "fill-primary text-primary"
                              : "text-muted"
                          }`}
                        />
                      ))}
                    </div>

                    <p className="text-muted-foreground mb-3">
                      {review.comment}
                    </p>

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
            {postsLoading ? (
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="w-8 h-8 animate-spin text-primary" />
                  <span className="ml-2">Carregando posts...</span>
                </div>
              </Card>
            ) : gamePosts.length > 0 ? (
              <div className="space-y-4">
                {gamePosts.map((post) => (
                  <Card
                    key={post.id}
                    className="p-6 bg-card/50 backdrop-blur-sm hover:bg-card/70 transition-smooth cursor-pointer"
                    onClick={() => navigate("/comunidade")}
                  >
                    <div className="mb-3">
                      <p className="text-sm text-muted-foreground">
                        {post.autorNome ||
                          `Autor #${post.autorId.substring(0, 8)}`}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {new Date(post.dataPublicacao).toLocaleDateString(
                          "pt-BR"
                        )}
                      </p>
                    </div>

                    <h4 className="font-semibold text-lg mb-2">
                      {post.titulo}
                    </h4>
                    <p className="text-muted-foreground line-clamp-3">
                      {post.texto}
                    </p>

                    {post.tags && post.tags.length > 0 && (
                      <div className="flex flex-wrap gap-2 mt-3">
                        {post.tags.map((tag) => (
                          <Badge
                            key={tag}
                            variant="secondary"
                            className="text-xs"
                          >
                            {tag}
                          </Badge>
                        ))}
                      </div>
                    )}
                  </Card>
                ))}
              </div>
            ) : (
              <Card className="p-6 bg-card/50 backdrop-blur-sm text-center">
                <p className="text-muted-foreground">
                  Nenhum post sobre este jogo ainda. Visite a página de{" "}
                  <span
                    className="text-primary cursor-pointer hover:underline"
                    onClick={() => navigate("/comunidade")}
                  >
                    Comunidade
                  </span>{" "}
                  para criar uma discussão.
                </p>
              </Card>
            )}
          </TabsContent>
        </Tabs>
      </section>

      {/* Purchase Confirmation Modal */}
      <PurchaseConfirmModal
        open={showPurchaseModal}
        onOpenChange={setShowPurchaseModal}
        gameTitle={game.title}
        gamePrice={game.price}
        onConfirm={handleConfirmPurchase}
        loading={purchaseLoading}
      />

      {/* Insufficient Balance Modal */}
      <InsufficientBalanceModal
        open={showInsufficientBalanceModal}
        onOpenChange={setShowInsufficientBalanceModal}
        gameTitle={game.title}
        gamePrice={game.price}
        currentBalance={balanceInfo.current}
        missingAmount={balanceInfo.missing}
        onAddFunds={handleAddFunds}
      />

      {/* Success Modal */}
      <AlertDialog open={showSuccessModal} onOpenChange={setShowSuccessModal}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle className="flex items-center gap-2 text-secondary">
              <CheckCircle className="w-6 h-6" />
              Compra Realizada com Sucesso!
            </AlertDialogTitle>
            <AlertDialogDescription className="space-y-4 pt-4">
              <div className="text-center">
                <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-secondary/20 flex items-center justify-center">
                  <CheckCircle className="w-10 h-10 text-secondary" />
                </div>
                <p className="text-foreground font-medium text-lg mb-2">
                  {game.title} foi adicionado à sua biblioteca!
                </p>
                <p className="text-sm text-muted-foreground">
                  Você já pode fazer o download e começar a jogar.
                </p>
              </div>
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter className="flex gap-2">
            <AlertDialogCancel onClick={() => setShowSuccessModal(false)}>
              Continuar Navegando
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                setShowSuccessModal(false);
                navigate("/biblioteca");
              }}
              className="bg-secondary hover:bg-secondary/90"
            >
              Ir para Biblioteca
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default GameDetail;
