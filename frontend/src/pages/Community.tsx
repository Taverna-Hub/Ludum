import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { listarJogos, Game } from "@/http/requests/jogoRequests";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { AVAILABLE_TAGS } from "@/constants/tags";
import {
  Heart,
  MessageSquare,
  Share2,
  Image as ImageIcon,
  UserPlus,
  UserMinus,
  Ban,
  Loader2,
} from "lucide-react";
import { toast } from "sonner";
import { DashboardLayout } from "@/layouts/DashboardLayout";
import { postRequests, PostResponse } from "@/http/requests/postRequests";
import { useAuth } from "@/hooks/useAuth";

const Community = () => {
  const { user, isAuthenticated } = useAuth();
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [newPost, setNewPost] = useState("");
  const [likedPosts, setLikedPosts] = useState<string[]>([]);
  const [followedUsers, setFollowedUsers] = useState<string[]>([]);
  const [blockedUsers, setBlockedUsers] = useState<string[]>([]);
  const [followedTags, setFollowedTags] = useState<string[]>([]);
  const [followedDevelopers, setFollowedDevelopers] = useState<string[]>([]);
  const [commentText, setCommentText] = useState<Record<string, string>>({});
  const [showComments, setShowComments] = useState<Record<string, boolean>>({});
  const [likingPost, setLikingPost] = useState<string | null>(null);

  // Novos estados para criar post
  const [jogosDisponiveis, setJogosDisponiveis] = useState<Game[]>([]);
  const [jogoSelecionado, setJogoSelecionado] = useState<string>("");
  const [tagsSelecionadas, setTagsSelecionadas] = useState<string[]>([]);
  const [loadingJogos, setLoadingJogos] = useState(false);
  const [postTitulo, setPostTitulo] = useState("");
  const [imagemUrl, setImagemUrl] = useState("");
  const [mostrarInputImagem, setMostrarInputImagem] = useState(false);

  // Log para debug
  useEffect(() => {
    console.log("Estado do usuário:", { user, isAuthenticated });
  }, [user, isAuthenticated]);

  // Carregar posts curtidos do localStorage
  useEffect(() => {
    if (user?.id) {
      const storedLikes = localStorage.getItem(`likedPosts_${user.id}`);
      if (storedLikes) {
        setLikedPosts(JSON.parse(storedLikes));
      }
    }
  }, [user?.id]);

  // Salvar posts curtidos no localStorage
  useEffect(() => {
    if (user?.id) {
      localStorage.setItem(`likedPosts_${user.id}`, JSON.stringify(likedPosts));
    }
  }, [likedPosts, user?.id]);

  // Carregar jogos do backend
  useEffect(() => {
    const carregarJogos = async () => {
      try {
        setLoadingJogos(true);
        const jogos = await listarJogos();
        setJogosDisponiveis(jogos);
        // Selecionar o primeiro jogo por padrão
        if (jogos.length > 0) {
          setJogoSelecionado(jogos[0].id);
        }
      } catch (error) {
        console.error("Erro ao carregar jogos:", error);
        toast.error("Erro ao carregar lista de jogos");
      } finally {
        setLoadingJogos(false);
      }
    };
    carregarJogos();
  }, []);

  // Carregar posts ao montar o componente
  useEffect(() => {
    carregarPosts();
  }, []);

  const carregarPosts = async () => {
    try {
      setLoading(true);
      const postsData = await postRequests.obterTodosOsPosts();
      // Filtrar apenas posts PUBLICADOS
      const postsFiltrados = postsData.filter(
        (post) => post.status === "PUBLICADO"
      );
      setPosts(postsFiltrados);
    } catch (error) {
      toast.error("Erro ao carregar posts");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleLike = async (postId: string) => {
    if (!user?.id) {
      toast.error("Você precisa estar logado para curtir posts");
      return;
    }

    if (likingPost === postId) return;

    const isLiked = likedPosts.includes(postId);
    setLikingPost(postId);

    try {
      console.log(
        `${isLiked ? "Descurtindo" : "Curtindo"} post:`,
        postId,
        "Usuario:",
        user.id
      );

      if (isLiked) {
        await postRequests.descurtirPost(postId, user.id);
        const newLikes = likedPosts.filter((id) => id !== postId);
        setLikedPosts(newLikes);
        toast.success("Curtida removida");
      } else {
        await postRequests.curtirPost(postId, user.id);
        const newLikes = [...likedPosts, postId];
        setLikedPosts(newLikes);
        toast.success("Post curtido!");
      }
      await carregarPosts();
    } catch (error: any) {
      console.error("Erro ao curtir/descurtir:", {
        error,
        response: error?.response,
        data: error?.response?.data,
        status: error?.response?.status,
        postId,
        userId: user.id,
        isLiked,
      });

      // Estado dessincronizado: corrigir baseado no erro do backend
      if (isLiked && error?.response?.status === 409) {
        // Tentou descurtir mas backend diz que não está curtido
        const newLikes = likedPosts.filter((id) => id !== postId);
        setLikedPosts(newLikes);
      } else if (!isLiked && error?.response?.status === 500) {
        // Tentou curtir mas backend diz que já está curtido
        const newLikes = [...likedPosts, postId];
        setLikedPosts(newLikes);
      } else {
        const errorMessage =
          error?.response?.data?.message ||
          error?.message ||
          "Erro ao processar curtida";
        toast.error(errorMessage);
      }
      await carregarPosts();
    } finally {
      setLikingPost(null);
    }
  };

  const handlePost = async () => {
    if (!newPost.trim()) {
      toast.error("Digite algo para publicar");
      return;
    }

    if (!user?.id) {
      toast.error("Você precisa estar logado para publicar");
      return;
    }

    if (!jogoSelecionado) {
      toast.error("Selecione um jogo para o post");
      return;
    }

    try {
      const postData = {
        jogoId: jogoSelecionado,
        autorId: user.id,
        titulo: postTitulo.trim() || "Post da Comunidade",
        conteudo: newPost,
        imagemUrl: imagemUrl.trim() || undefined,
        tagIds: tagsSelecionadas, // Backend espera nomes de tags, não IDs
      };

      console.log("Publicando post com dados:", postData);

      await postRequests.publicarPost(postData);
      toast.success("Post publicado com sucesso!");
      setNewPost("");
      setPostTitulo("");
      setImagemUrl("");
      setMostrarInputImagem(false);
      setTagsSelecionadas([]);
      await carregarPosts();
    } catch (error: any) {
      console.error("Erro completo ao publicar post:", {
        error,
        response: error?.response,
        data: error?.response?.data,
        status: error?.response?.status,
        message: error?.message,
      });

      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Erro ao publicar post";
      toast.error(errorMessage);
    }
  };

  const toggleTag = (tag: string) => {
    if (tagsSelecionadas.includes(tag)) {
      setTagsSelecionadas(tagsSelecionadas.filter((t) => t !== tag));
    } else {
      setTagsSelecionadas([...tagsSelecionadas, tag]);
    }
  };

  const handleFollowUser = (userId: string, userName: string) => {
    if (followedUsers.includes(userId)) {
      setFollowedUsers(followedUsers.filter((id) => id !== userId));
      toast.success(`Você deixou de seguir ${userName}`);
    } else {
      setFollowedUsers([...followedUsers, userId]);
      toast.success(`Você está seguindo ${userName}`);
    }
  };

  const handleBlockUser = (userId: string, userName: string) => {
    if (blockedUsers.includes(userId)) {
      setBlockedUsers(blockedUsers.filter((id) => id !== userId));
      toast.success(`${userName} foi desbloqueado`);
    } else {
      setBlockedUsers([...blockedUsers, userId]);
      toast.success(`${userName} foi bloqueado`);
    }
  };

  const handleFollowTag = (tag: string) => {
    if (followedTags.includes(tag)) {
      setFollowedTags(followedTags.filter((t) => t !== tag));
      toast.success(`Você deixou de seguir #${tag}`);
    } else {
      setFollowedTags([...followedTags, tag]);
      toast.success(`Você está seguindo #${tag}`);
    }
  };

  const handleFollowDeveloper = (devId: string, devName: string) => {
    if (followedDevelopers.includes(devId)) {
      setFollowedDevelopers(followedDevelopers.filter((id) => id !== devId));
      toast.success(`Você deixou de seguir ${devName}`);
    } else {
      setFollowedDevelopers([...followedDevelopers, devId]);
      toast.success(`Você está seguindo ${devName}`);
    }
  };

  const handleComment = async (postId: string) => {
    if (!user?.id) {
      toast.error("Você precisa estar logado para comentar");
      return;
    }

    const texto = commentText[postId]?.trim();
    if (!texto) {
      toast.error("Digite um comentário");
      return;
    }

    try {
      console.log(
        "Comentando post:",
        postId,
        "Usuario:",
        user.id,
        "Texto:",
        texto
      );
      await postRequests.comentarPost(postId, {
        autorId: user.id,
        texto: texto,
      });
      toast.success("Comentário adicionado!");
      setCommentText({ ...commentText, [postId]: "" });
      await carregarPosts();
    } catch (error: any) {
      const errorMessage =
        error?.response?.data?.message || error?.message || "Erro ao comentar";
      toast.error(errorMessage);
      console.error("Erro ao comentar:", error);
    }
  };

  const toggleComments = (postId: string) => {
    setShowComments({ ...showComments, [postId]: !showComments[postId] });
  };

  // Get popular tags from games reais do backend
  const allTags = jogosDisponiveis.flatMap((game) => game.tags);
  const tagCounts = allTags.reduce((acc, tag) => {
    acc[tag] = (acc[tag] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);
  const popularTags = Object.entries(tagCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
    .map(([tag]) => tag);

  // Get developers from games reais do backend (deduplicated by ID)
  const developersMap = new Map();
  jogosDisponiveis.forEach((game) => {
    if (!developersMap.has(game.developerId)) {
      developersMap.set(game.developerId, {
        id: game.developerId,
        name: game.developerName,
      });
    }
  });
  const developers = Array.from(developersMap.values()).slice(0, 5);

  // Filter blocked users' posts
  const visiblePosts = posts.filter(
    (post) => !blockedUsers.includes(post.autorId)
  );

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16">
        {/* Header */}
        <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
          <div className="container mx-auto">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              <span className="bg-gradient-secondary bg-clip-text text-transparent">
                Comunidade
              </span>
            </h1>
            <p className="text-lg text-muted-foreground">
              Compartilhe suas experiências, conquistas e conecte-se com outros
              jogadores
            </p>
          </div>
        </section>

        <div className="container mx-auto px-4 py-12">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Sidebar */}
            <div className="lg:col-span-1 space-y-6">
              {/* Tags Populares */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h3 className="font-bold text-lg mb-4">Tags Populares</h3>
                <div className="flex flex-wrap gap-2">
                  {popularTags.map((tag) => {
                    const isFollowed = followedTags.includes(tag);
                    return (
                      <button
                        key={tag}
                        onClick={() => handleFollowTag(tag)}
                        className={`px-3 py-1 rounded-full text-sm transition-smooth ${
                          isFollowed
                            ? "bg-gradient-primary text-white"
                            : "bg-muted text-muted-foreground hover:bg-muted/80"
                        }`}
                      >
                        #{tag}
                      </button>
                    );
                  })}
                </div>
              </Card>

              {/* Desenvolvedoras */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h3 className="font-bold text-lg mb-4">Desenvolvedoras</h3>
                <div className="space-y-3">
                  {developers.map((dev) => {
                    const isFollowed = followedDevelopers.includes(dev.id);
                    return (
                      <div
                        key={dev.id}
                        className="flex items-center justify-between"
                      >
                        <div className="flex items-center gap-2">
                          <div className="w-8 h-8 rounded-full bg-gradient-secondary" />
                          <span className="text-sm font-medium">
                            {dev.name}
                          </span>
                        </div>
                        <Button
                          size="sm"
                          variant={isFollowed ? "outline" : "hero"}
                          onClick={() =>
                            handleFollowDeveloper(dev.id, dev.name)
                          }
                        >
                          {isFollowed ? (
                            <>
                              <UserMinus className="w-3 h-3 mr-1" />
                              Seguindo
                            </>
                          ) : (
                            <>
                              <UserPlus className="w-3 h-3 mr-1" />
                              Seguir
                            </>
                          )}
                        </Button>
                      </div>
                    );
                  })}
                </div>
              </Card>
            </div>

            {/* Main Content */}
            <div className="lg:col-span-2">
              {loading ? (
                <div className="flex items-center justify-center py-12">
                  <Loader2 className="w-8 h-8 animate-spin text-primary" />
                </div>
              ) : (
                <>
                  {/* Create Post */}
                  <Card className="p-6 bg-card/50 backdrop-blur-sm mb-8">
                    <h2 className="font-bold text-lg mb-4">Criar novo post</h2>

                    {/* Input de Título */}
                    <div className="mb-4">
                      <label className="text-sm font-medium mb-2 block">
                        Título
                      </label>
                      <input
                        type="text"
                        placeholder="Dê um título ao seu post..."
                        value={postTitulo}
                        onChange={(e) => setPostTitulo(e.target.value)}
                        maxLength={100}
                        className="w-full px-3 py-2 bg-background border border-input rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-ring"
                      />
                      <p className="text-xs text-muted-foreground mt-1">
                        {postTitulo.length}/100
                      </p>
                    </div>

                    {/* Seletor de Jogo */}
                    <div className="mb-4">
                      <label className="text-sm font-medium mb-2 block">
                        Jogo relacionado *
                      </label>
                      <Select
                        value={jogoSelecionado}
                        onValueChange={setJogoSelecionado}
                        disabled={loadingJogos}
                      >
                        <SelectTrigger className="w-full">
                          <SelectValue placeholder="Selecione um jogo" />
                        </SelectTrigger>
                        <SelectContent>
                          {jogosDisponiveis.map((jogo) => (
                            <SelectItem key={jogo.id} value={jogo.id}>
                              {jogo.title}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    {/* Seletor de Tags */}
                    <div className="mb-4">
                      <label className="text-sm font-medium mb-2 block">
                        Tags (Selecione pelo menos uma)
                      </label>
                      <div className="flex flex-wrap gap-2">
                        {AVAILABLE_TAGS.slice(0, 10).map((tag) => (
                          <Badge
                            key={tag}
                            variant={
                              tagsSelecionadas.includes(tag)
                                ? "default"
                                : "outline"
                            }
                            className="cursor-pointer hover:bg-primary/80 transition-smooth"
                            onClick={() => toggleTag(tag)}
                          >
                            {tag}
                          </Badge>
                        ))}
                      </div>
                      {tagsSelecionadas.length > 0 && (
                        <div className="mt-2 text-xs text-muted-foreground">
                          Selecionadas: {tagsSelecionadas.join(", ")}
                        </div>
                      )}
                    </div>

                    <Textarea
                      placeholder="Compartilhe algo com a comunidade... (máx. 500 caracteres)"
                      value={newPost}
                      onChange={(e) => setNewPost(e.target.value)}
                      maxLength={500}
                      className="mb-3 min-h-[100px]"
                    />

                    {/* Input de URL de Imagem */}
                    {mostrarInputImagem && (
                      <div className="mb-3">
                        <label className="text-sm font-medium mb-2 block">
                          URL da imagem
                        </label>
                        <input
                          type="url"
                          placeholder="https://exemplo.com/imagem.jpg"
                          value={imagemUrl}
                          onChange={(e) => setImagemUrl(e.target.value)}
                          className="w-full px-3 py-2 bg-background border border-input rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-ring"
                        />
                        {imagemUrl && (
                          <div className="mt-2">
                            <img
                              src={imagemUrl}
                              alt="Preview"
                              className="max-h-40 rounded-md"
                              onError={(e) => {
                                e.currentTarget.style.display = "none";
                              }}
                            />
                          </div>
                        )}
                      </div>
                    )}

                    <div className="flex items-center justify-between">
                      <div className="flex gap-2">
                        <Button
                          variant={mostrarInputImagem ? "default" : "outline"}
                          size="sm"
                          type="button"
                          onClick={() =>
                            setMostrarInputImagem(!mostrarInputImagem)
                          }
                        >
                          <ImageIcon className="w-4 h-4 mr-2" />
                          {mostrarInputImagem ? "Esconder" : "Adicionar"} Imagem
                        </Button>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-sm text-muted-foreground">
                          {newPost.length}/500
                        </span>
                        <Button
                          variant="hero"
                          onClick={handlePost}
                          disabled={
                            !newPost.trim() || !jogoSelecionado || loadingJogos
                          }
                        >
                          Publicar
                        </Button>
                      </div>
                    </div>
                  </Card>

                  {/* Posts Feed */}
                  <div className="space-y-6">
                    {visiblePosts.map((post) => {
                      const isLiked = likedPosts.includes(post.id);

                      return (
                        <Card
                          key={post.id}
                          className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-border transition-smooth"
                        >
                          {/* Post Header */}
                          <div className="p-6 pb-4">
                            <div className="flex items-start justify-between mb-3">
                              <div className="flex items-center gap-3">
                                <div className="w-10 h-10 rounded-full bg-gradient-primary" />
                                <div>
                                  <p className="font-semibold">
                                    Autor #{post.autorId.substring(0, 8)}
                                  </p>
                                  <p className="text-sm text-muted-foreground">
                                    {post.dataPublicacao
                                      ? new Date(
                                          post.dataPublicacao
                                        ).toLocaleDateString("pt-BR")
                                      : "Sem data"}
                                  </p>
                                </div>
                              </div>
                              <div className="flex items-center gap-2">
                                <Button
                                  size="sm"
                                  variant={
                                    followedUsers.includes(post.autorId)
                                      ? "outline"
                                      : "secondary"
                                  }
                                  onClick={() =>
                                    handleFollowUser(
                                      post.autorId,
                                      `Autor #${post.autorId.substring(0, 8)}`
                                    )
                                  }
                                >
                                  {followedUsers.includes(post.autorId) ? (
                                    <UserMinus className="w-4 h-4" />
                                  ) : (
                                    <UserPlus className="w-4 h-4" />
                                  )}
                                </Button>
                                <Button
                                  size="sm"
                                  variant="destructive"
                                  onClick={() =>
                                    handleBlockUser(
                                      post.autorId,
                                      `Autor #${post.autorId.substring(0, 8)}`
                                    )
                                  }
                                >
                                  <Ban className="w-4 h-4" />
                                </Button>
                              </div>
                            </div>

                            {post.titulo && (
                              <h3 className="font-bold text-lg mb-2">
                                {post.titulo}
                              </h3>
                            )}

                            <p className="text-foreground mb-4">
                              {post.conteudo}
                            </p>

                            {/* Tags do Post */}
                            {post.tagIds && post.tagIds.length > 0 && (
                              <div className="flex flex-wrap gap-2 mb-4">
                                {post.tagIds.map((tag) => (
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

                            {/* Post Image */}
                            {post.imagemUrl && (
                              <div className="rounded-lg overflow-hidden mb-4">
                                <img
                                  src={post.imagemUrl}
                                  alt="Post"
                                  className="w-full h-auto"
                                />
                              </div>
                            )}

                            {/* Actions */}
                            <div className="flex items-center gap-6 pt-4 border-t border-border/50">
                              <button
                                className={`flex items-center gap-2 transition-smooth ${
                                  isLiked
                                    ? "text-red-500"
                                    : "text-muted-foreground hover:text-foreground"
                                } ${
                                  likingPost === post.id
                                    ? "opacity-50 cursor-not-allowed"
                                    : ""
                                }`}
                                onClick={() => handleLike(post.id)}
                                disabled={likingPost === post.id}
                              >
                                {likingPost === post.id ? (
                                  <Loader2 className="w-5 h-5 animate-spin" />
                                ) : (
                                  <Heart
                                    className={`w-5 h-5 ${
                                      isLiked ? "fill-current" : ""
                                    }`}
                                  />
                                )}
                                <span className="text-sm font-medium">
                                  {post.numeroCurtidas}
                                </span>
                              </button>

                              <button
                                className="flex items-center gap-2 text-muted-foreground hover:text-foreground transition-smooth"
                                onClick={() => toggleComments(post.id)}
                              >
                                <MessageSquare className="w-5 h-5" />
                                <span className="text-sm font-medium">
                                  {post.numeroComentarios}
                                </span>
                              </button>

                              <button className="flex items-center gap-2 text-muted-foreground hover:text-foreground transition-smooth ml-auto">
                                <Share2 className="w-5 h-5" />
                                <span className="text-sm font-medium">
                                  Compartilhar
                                </span>
                              </button>
                            </div>

                            {/* Comments Section */}
                            {showComments[post.id] && (
                              <div className="mt-4 pt-4 border-t border-border/50">
                                <div className="flex gap-2">
                                  <Textarea
                                    placeholder="Escreva um comentário..."
                                    value={commentText[post.id] || ""}
                                    onChange={(e) =>
                                      setCommentText({
                                        ...commentText,
                                        [post.id]: e.target.value,
                                      })
                                    }
                                    className="min-h-[60px]"
                                    maxLength={300}
                                  />
                                </div>
                                <div className="flex justify-between items-center mt-2">
                                  <span className="text-xs text-muted-foreground">
                                    {(commentText[post.id] || "").length}/300
                                  </span>
                                  <Button
                                    size="sm"
                                    variant="hero"
                                    onClick={() => handleComment(post.id)}
                                    disabled={!commentText[post.id]?.trim()}
                                  >
                                    Comentar
                                  </Button>
                                </div>
                              </div>
                            )}
                          </div>
                        </Card>
                      );
                    })}
                  </div>

                  {/* Load More */}
                  <div className="text-center mt-8">
                    <Button variant="outline" size="lg" onClick={carregarPosts}>
                      Carregar mais posts
                    </Button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Community;
