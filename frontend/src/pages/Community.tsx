import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { listarJogos, Game } from "@/http/requests/jogoRequests";
import { listarDesenvolvedoras, DesenvolvedoraResumo } from "@/http/requests/desenvolvedoraRequests";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { AVAILABLE_TAGS } from "@/constants/tags";
import { parseTagIds, tagNamesToIds } from "@/types/tagMapping";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
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
import { useSeguimento } from "@/hooks/useSeguimento";

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
  const [mostrarRascunhos, setMostrarRascunhos] = useState(false);
  const [rascunhos, setRascunhos] = useState<PostResponse[]>([]);
  const [loadingRascunhos, setLoadingRascunhos] = useState(false);
  const [rascunhoEmEdicao, setRascunhoEmEdicao] = useState<string | null>(null);
  const [mostrarAgendamento, setMostrarAgendamento] = useState(false);
  const [rascunhoParaAgendar, setRascunhoParaAgendar] = useState<string | null>(
    null
  );
  const [dataAgendamento, setDataAgendamento] = useState("");

  // Estados para desenvolvedoras da API
  const [desenvolvedoras, setDesenvolvedoras] = useState<DesenvolvedoraResumo[]>([]);
  const [loadingDesenvolvedoras, setLoadingDesenvolvedoras] = useState(false);

  // Hook de seguimento
  const { 
    toggleSeguir, 
    verificarMultiplosSeguindo, 
    followingMap 
  } = useSeguimento();

  // Log para debug
  useEffect(() => {
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

  // Carregar desenvolvedoras da API
  useEffect(() => {
    const carregarDesenvolvedoras = async () => {
      setLoadingDesenvolvedoras(true);
      try {
        const data = await listarDesenvolvedoras();
        setDesenvolvedoras(data);
      } catch (error) {
        console.error("Erro ao carregar desenvolvedoras:", error);
      } finally {
        setLoadingDesenvolvedoras(false);
      }
    };
    carregarDesenvolvedoras();
  }, []);

  // Verificar se está seguindo as desenvolvedoras quando carregar
  useEffect(() => {
    if (desenvolvedoras.length > 0) {
      const devIds = desenvolvedoras.map(dev => dev.id);
      verificarMultiplosSeguindo(devIds);
    }
  }, [desenvolvedoras, verificarMultiplosSeguindo]);

  // Carregar posts ao montar o componente
  useEffect(() => {
    carregarPosts();
  }, []);

  // Carregar rascunhos quando usuário mudar
  useEffect(() => {
    if (user?.id) {
      carregarRascunhos();
    }
  }, [user?.id]);

  // Carregar rascunhos do backend
  const carregarRascunhos = async () => {
    if (!user?.id) return;

    try {
      setLoadingRascunhos(true);
      const todosPostsAutor = await postRequests.obterPostsPorAutor(user.id);
      const rascunhosAutor = todosPostsAutor.filter(
        (post) => post.status === "EM_RASCUNHO"
      );
      setRascunhos(rascunhosAutor);
    } catch (error) {
      console.error("Erro ao carregar rascunhos:", error);
    } finally {
      setLoadingRascunhos(false);
    }
  };

  const carregarPosts = async () => {
    try {
      setLoading(true);
      const postsData = await postRequests.obterTodosOsPosts();
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
        tagIds: tagsSelecionadas, // Converter nomes para IDs do backend
      };


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

  const handleSaveDraft = async () => {
    if (!user?.id) {
      toast.error("Você precisa estar logado para salvar rascunhos");
      return;
    }

    if (!newPost.trim() && !postTitulo.trim()) {
      toast.error("Digite algo para salvar como rascunho");
      return;
    }

    if (!jogoSelecionado) {
      toast.error("Selecione um jogo para o rascunho");
      return;
    }

    try {
      const draftData = {
        jogoId: jogoSelecionado,
        autorId: user.id,
        titulo: postTitulo.trim() || "Rascunho sem título",
        conteudo: newPost,
        imagemUrl: imagemUrl.trim() || undefined,
        tagIds: tagsSelecionadas, // Converter nomes para IDs do backend
      };

      if (rascunhoEmEdicao) {
        // Atualizar rascunho existente
        await postRequests.editarPost(rascunhoEmEdicao, user.id, {
          titulo: draftData.titulo,
          conteudo: draftData.conteudo,
        });
        toast.success("Rascunho atualizado com sucesso!");
      } else {
        // Criar novo rascunho
        await postRequests.criarRascunho(draftData);
        toast.success("Rascunho salvo com sucesso!");
      }

      setNewPost("");
      setPostTitulo("");
      setImagemUrl("");
      setMostrarInputImagem(false);
      setTagsSelecionadas([]);
      setRascunhoEmEdicao(null);
      await carregarRascunhos();
    } catch (error: any) {
      console.error("Erro ao salvar rascunho:", error);
      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Erro ao salvar rascunho";
      toast.error(errorMessage);
    }
  };

  const handleLoadDraft = (draft: PostResponse) => {
    setPostTitulo(draft.titulo);
    setNewPost(draft.conteudo);
    setImagemUrl(draft.imagemUrl || "");
    setJogoSelecionado(draft.jogoId);
    setTagsSelecionadas(parseTagIds(draft.tagIds)); // Converter IDs para nomes
    setRascunhoEmEdicao(draft.id);
    if (draft.imagemUrl) {
      setMostrarInputImagem(true);
    }
    setMostrarRascunhos(false);
    toast.success("Rascunho carregado para edição!");
  };

  const handleDeleteDraft = async (draftId: string) => {
    if (!user?.id) return;

    try {
      await postRequests.removerPost(draftId, user.id);
      toast.success("Rascunho excluído!");
      await carregarRascunhos();
    } catch (error: any) {
      console.error("Erro ao excluir rascunho:", error);
      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Erro ao excluir rascunho";
      toast.error(errorMessage);
    }
  };

  const handlePublishDraft = async (draftId: string) => {
    if (!user?.id) return;

    try {
      await postRequests.publicarRascunho(draftId, user.id);
      toast.success("Rascunho publicado com sucesso!");
      await carregarRascunhos();
      await carregarPosts();
    } catch (error: any) {
      console.error("Erro ao publicar rascunho:", error);
      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Erro ao publicar rascunho";
      toast.error(errorMessage);
    }
  };

  const handleOpenSchedule = (draftId: string) => {
    setRascunhoParaAgendar(draftId);
    setMostrarAgendamento(true);
    // Definir data mínima como 1 hora a partir de agora
    const minDate = new Date();
    minDate.setHours(minDate.getHours() + 1);
    const minDateStr = minDate.toISOString().slice(0, 16);
    setDataAgendamento(minDateStr);
  };

  const handleScheduleDraft = async () => {
    if (!rascunhoParaAgendar || !dataAgendamento) {
      toast.error("É necessário selecionar uma data e hora");
      return;
    }

    try {
      const dataAgendamentoISO = new Date(dataAgendamento).toISOString();
      await postRequests.agendarPost(rascunhoParaAgendar, {
        dataAgendamento: dataAgendamentoISO,
      });
      toast.success("Post agendado com sucesso!");
      setMostrarAgendamento(false);
      setRascunhoParaAgendar(null);
      setDataAgendamento("");
      await carregarRascunhos();
    } catch (error: any) {
      console.error("Erro ao agendar post:", error);
      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Erro ao agendar post";
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

  // Get popular tags from AVAILABLE_TAGS
  const popularTags = AVAILABLE_TAGS.slice(0, 10);

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
                {loadingDesenvolvedoras ? (
                  <div className="flex items-center justify-center py-4">
                    <Loader2 className="w-5 h-5 animate-spin text-primary" />
                  </div>
                ) : (
                  <div className="space-y-3">
                    {desenvolvedoras.slice(0, 5).map((dev) => {
                      const isFollowed = followingMap[dev.id];
                      return (
                        <div
                          key={dev.id}
                          className="flex items-center justify-between"
                        >
                          <div className="flex items-center gap-2">
                            <div className="w-8 h-8 rounded-full bg-gradient-secondary" />
                            <span className="text-sm font-medium">
                              {dev.nome}
                            </span>
                          </div>
                          <Button
                            size="sm"
                            variant={isFollowed ? "outline" : "hero"}
                            onClick={() => toggleSeguir(dev.id, 'DESENVOLVEDORA', dev.nome)}
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
                )}
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
                    <div className="flex items-center justify-between mb-4">
                      <h2 className="font-bold text-lg">Criar novo post</h2>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setMostrarRascunhos(true)}
                      >
                        Ver Rascunhos ({rascunhos.length})
                      </Button>
                    </div>

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
                        {AVAILABLE_TAGS.slice(0, 30).map((tag) => (
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
                          variant="outline"
                          onClick={handleSaveDraft}
                          disabled={!newPost.trim() && !postTitulo.trim()}
                        >
                          {rascunhoEmEdicao
                            ? "Atualizar Rascunho"
                            : "Criar Rascunho"}
                        </Button>
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
                                {parseTagIds(post.tagIds).map((tag, index) => (
                                  <Badge
                                    key={`${post.id}-${index}`}
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

      {/* Modal de Rascunhos */}
      <Dialog open={mostrarRascunhos} onOpenChange={setMostrarRascunhos}>
        <DialogContent className="max-w-3xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Seus Rascunhos</DialogTitle>
            <DialogDescription>
              Gerencie seus posts salvos como rascunho
            </DialogDescription>
          </DialogHeader>

          <div className="mt-4">
            {loadingRascunhos ? (
              <div className="flex items-center justify-center py-8">
                <Loader2 className="w-8 h-8 animate-spin text-primary" />
              </div>
            ) : rascunhos.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-muted-foreground">Nenhum rascunho salvo</p>
              </div>
            ) : (
              <div className="space-y-4">
                {rascunhos.map((draft) => (
                  <div
                    key={draft.id}
                    className="border border-border rounded-lg p-4 hover:border-primary/50 transition-colors"
                  >
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex-1 min-w-0">
                        <h4 className="font-semibold text-base mb-2">
                          {draft.titulo}
                        </h4>
                        <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
                          {draft.conteudo}
                        </p>
                        {draft.tagIds && draft.tagIds.length > 0 && (
                          <div className="flex flex-wrap gap-1 mb-2">
                            {parseTagIds(draft.tagIds).map((tag, index) => (
                              <Badge
                                key={`${draft.id}-${index}`}
                                variant="secondary"
                                className="text-xs"
                              >
                                {tag}
                              </Badge>
                            ))}
                          </div>
                        )}
                        <p className="text-xs text-muted-foreground">
                          Criado em:{" "}
                          {draft.dataPublicacao
                            ? new Date(draft.dataPublicacao).toLocaleDateString(
                                "pt-BR",
                                {
                                  day: "2-digit",
                                  month: "2-digit",
                                  year: "numeric",
                                  hour: "2-digit",
                                  minute: "2-digit",
                                }
                              )
                            : "Sem data"}
                        </p>
                      </div>
                      <div className="flex flex-col gap-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleLoadDraft(draft)}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="hero"
                          size="sm"
                          onClick={() => handlePublishDraft(draft.id)}
                        >
                          Publicar
                        </Button>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => handleOpenSchedule(draft.id)}
                        >
                          Agendar
                        </Button>
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => handleDeleteDraft(draft.id)}
                        >
                          Excluir
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {/* Modal de Agendamento */}
      <Dialog open={mostrarAgendamento} onOpenChange={setMostrarAgendamento}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Agendar Publicação</DialogTitle>
            <DialogDescription>
              Escolha a data e hora para publicar este post automaticamente
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4 mt-4">
            <div>
              <label className="text-sm font-medium mb-2 block">
                Data e Hora da Publicação
              </label>
              <input
                type="datetime-local"
                value={dataAgendamento}
                onChange={(e) => setDataAgendamento(e.target.value)}
                min={new Date(Date.now() + 60 * 60 * 1000)
                  .toISOString()
                  .slice(0, 16)}
                max={new Date(Date.now() + 24 * 60 * 60 * 1000)
                  .toISOString()
                  .slice(0, 16)}
                className="w-full px-3 py-2 bg-background border border-input rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-ring"
              />
              <p className="text-xs text-muted-foreground mt-2">
                O post será publicado entre 1 e 24 horas a partir de agora
              </p>
            </div>

            <div className="flex gap-2 justify-end">
              <Button
                variant="outline"
                onClick={() => {
                  setMostrarAgendamento(false);
                  setRascunhoParaAgendar(null);
                  setDataAgendamento("");
                }}
              >
                Cancelar
              </Button>
              <Button
                variant="hero"
                onClick={handleScheduleDraft}
                disabled={!dataAgendamento}
              >
                Confirmar Agendamento
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </DashboardLayout>
  );
};

export default Community;
