import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { AVAILABLE_TAGS } from "@/constants/tags";
import {
  jogoRequests,
  CriarJogoRequest,
} from "@/http/requests/publicacaoRequests";
import { useAuthContext } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Switch } from "@/components/ui/switch";
import {
  Upload,
  X,
  Image as ImageIcon,
  Plus,
  CheckCircle2,
  AlertCircle,
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { DashboardLayout } from "@/layouts/DashboardLayout";

const PublishGame = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();
  const { user } = useAuthContext();
  const [isLoading, setIsLoading] = useState(false);

  const [formData, setFormData] = useState({
    title: "",
    slug: "",
    description: "",
    price: "",
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
  });

  const [coverImage, setCoverImage] = useState<string>("");
  const [screenshots, setScreenshots] = useState<string[]>([]);
  const [videos, setVideos] = useState<string[]>([]);
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState("");
  const [screenshotInput, setScreenshotInput] = useState("");
  const [videoInput, setVideoInput] = useState("");

  const availableTags = AVAILABLE_TAGS;

  const handleInputChange = (field: string, value: string | boolean) => {
    setFormData((prev) => ({ ...prev, [field]: value }));

    // Gerar slug automaticamente a partir do título
    if (field === "title" && typeof value === "string") {
      const slug = value
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9]+/g, "-")
        .replace(/(^-|-$)/g, "");
      setFormData((prev) => ({ ...prev, slug }));
    }
  };

  const addScreenshot = () => {
    if (!screenshotInput.trim()) {
      toast({
        title: "URL inválida",
        description: "Digite uma URL válida para o screenshot.",
        variant: "destructive",
      });
      return;
    }
    setScreenshots((prev) => [...prev, screenshotInput.trim()]);
    setScreenshotInput("");
  };

  const removeScreenshot = (index: number) => {
    setScreenshots((prev) => prev.filter((_, i) => i !== index));
  };

  const addVideo = () => {
    if (!videoInput.trim()) {
      toast({
        title: "URL inválida",
        description: "Digite uma URL válida para o vídeo.",
        variant: "destructive",
      });
      return;
    }
    setVideos((prev) => [...prev, videoInput.trim()]);
    setVideoInput("");
  };

  const removeVideo = (index: number) => {
    setVideos((prev) => prev.filter((_, i) => i !== index));
  };

  const addTag = (tag: string) => {
    if (tags.length >= 15) {
      toast({
        title: "Limite atingido",
        description: "Você pode adicionar no máximo 15 tags.",
        variant: "destructive",
      });
      return;
    }
    if (!tags.includes(tag)) {
      setTags((prev) => [...prev, tag]);
    }
    setTagInput("");
  };

  const removeTag = (tag: string) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const validateForm = () => {
    if (!formData.title.trim()) {
      toast({
        title: "Título obrigatório",
        description: "O jogo precisa de um título.",
        variant: "destructive",
      });
      return false;
    }

    if (!formData.description.trim()) {
      toast({
        title: "Descrição obrigatória",
        description: "Adicione uma descrição para o jogo.",
        variant: "destructive",
      });
      return false;
    }

    if (!coverImage) {
      toast({
        title: "Capa obrigatória",
        description: "Adicione uma imagem de capa.",
        variant: "destructive",
      });
      return false;
    }

    if (screenshots.length === 0) {
      toast({
        title: "Screenshots obrigatórias",
        description: "Adicione pelo menos 1 screenshot do jogo.",
        variant: "destructive",
      });
      return false;
    }

    if (tags.length === 0) {
      toast({
        title: "Tags obrigatórias",
        description: "Adicione pelo menos 1 tag.",
        variant: "destructive",
      });
      return false;
    }

    if (formData.hasAdultContent && !tags.includes("+18")) {
      toast({
        title: "Tag +18 obrigatória",
        description: "Jogos com conteúdo adulto devem ter a tag +18.",
        variant: "destructive",
      });
      return false;
    }

    return true;
  };

  const handlePublish = async () => {
    if (!validateForm()) return;

    if (!user) {
      toast({
        title: "Não autenticado",
        description: "Você precisa estar logado para publicar um jogo.",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);

    try {
      const requestData: CriarJogoRequest = {
        desenvolvedoraId: user.id,
        titulo: formData.title,
        descricao: formData.description,
        capaOficial: coverImage,
        screenshots: screenshots,
        videos: videos,
        tags: tags,
        isNSFW: formData.hasAdultContent,
        dataDeLancamento: new Date().toISOString(),
      };

      const response = await jogoRequests.publicarJogo(requestData);

      toast({
        title: "Sucesso!",
        description: response.mensagem,
      });

      // Redirecionar para página de upload com o ID do jogo
      navigate(`/desenvolvedor/upload/${response.jogoId}`);
    } catch (error: any) {
      toast({
        title: "Erro ao publicar",
        description:
          error.response?.data?.mensagem ||
          "Ocorreu um erro ao publicar o jogo.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16 pb-12">
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-4xl mx-auto">
            <div className="mb-8">
              <h1 className="text-4xl font-bold mb-4">
                <span className="bg-gradient-secondary bg-clip-text text-transparent">
                  Publicar Jogo
                </span>
              </h1>
              <p className="text-muted-foreground">
                Adicione as informações do seu jogo para publicá-lo na Ludum.
              </p>
            </div>

            <div className="space-y-6">
              {/* Informações Básicas */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h2 className="text-xl font-bold mb-4">Informações Básicas</h2>
                <Separator className="mb-6" />

                <div className="space-y-4">
                  <div>
                    <Label htmlFor="title">Título do Jogo *</Label>
                    <Input
                      id="title"
                      placeholder="Nome do seu jogo"
                      value={formData.title}
                      onChange={(e) =>
                        handleInputChange("title", e.target.value)
                      }
                      className="mt-2"
                    />
                  </div>

                  <div>
                    <Label htmlFor="slug">Slug (URL) *</Label>
                    <Input
                      id="slug"
                      placeholder="nome-do-jogo"
                      value={formData.slug}
                      onChange={(e) =>
                        handleInputChange("slug", e.target.value)
                      }
                      className="mt-2"
                    />
                    <p className="text-xs text-muted-foreground mt-1">
                      URL: ludum.com/jogo/{formData.slug || "nome-do-jogo"}
                    </p>
                  </div>

                  <div>
                    <Label htmlFor="description">Descrição *</Label>
                    <Textarea
                      id="description"
                      placeholder="Descreva seu jogo..."
                      value={formData.description}
                      onChange={(e) =>
                        handleInputChange("description", e.target.value)
                      }
                      className="mt-2 min-h-32"
                    />
                  </div>

                  <div>
                    <Label htmlFor="price">Preço (R$)</Label>
                    <Input
                      id="price"
                      type="number"
                      placeholder="0.00"
                      value={formData.price}
                      onChange={(e) =>
                        handleInputChange("price", e.target.value)
                      }
                      className="mt-2"
                    />
                    <p className="text-xs text-muted-foreground mt-1">
                      Deixe em branco ou 0 para jogo gratuito
                    </p>
                  </div>
                </div>
              </Card>

              {/* Mídia */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h2 className="text-xl font-bold mb-4">Mídia</h2>
                <Separator className="mb-6" />

                {/* Capa */}
                <div className="mb-6">
                  <Label htmlFor="cover-url">URL da Capa Oficial *</Label>
                  <Input
                    id="cover-url"
                    placeholder="https://exemplo.com/capa.jpg"
                    value={coverImage}
                    onChange={(e) => setCoverImage(e.target.value)}
                    className="mt-2"
                  />
                  {coverImage && (
                    <div className="mt-4 relative">
                      <img
                        src={coverImage}
                        alt="Preview da capa"
                        className="w-full h-64 object-cover rounded-lg"
                        onError={(e) => {
                          e.currentTarget.src = "";
                          toast({
                            title: "Erro ao carregar imagem",
                            description: "Verifique se a URL está correta.",
                            variant: "destructive",
                          });
                        }}
                      />
                    </div>
                  )}
                  <p className="text-xs text-muted-foreground mt-1">
                    Recomendado: 1920x1080px
                  </p>
                </div>

                {/* Screenshots */}
                <div className="mb-6">
                  <Label>Screenshots *</Label>
                  <p className="text-xs text-muted-foreground mb-2">
                    Adicione pelo menos 1 screenshot
                  </p>
                  <div className="flex gap-2 mb-4">
                    <Input
                      placeholder="https://exemplo.com/screenshot.jpg"
                      value={screenshotInput}
                      onChange={(e) => setScreenshotInput(e.target.value)}
                      onKeyPress={(e) => e.key === "Enter" && addScreenshot()}
                    />
                    <Button onClick={addScreenshot} type="button">
                      <Plus className="w-4 h-4" />
                    </Button>
                  </div>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    {screenshots.map((screenshot, index) => (
                      <div key={index} className="relative group">
                        <img
                          src={screenshot}
                          alt={`Screenshot ${index + 1}`}
                          className="w-full h-32 object-cover rounded-lg"
                          onError={(e) => {
                            e.currentTarget.src = "";
                          }}
                        />
                        <Button
                          size="icon"
                          variant="destructive"
                          className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity"
                          onClick={() => removeScreenshot(index)}
                        >
                          <X className="w-4 h-4" />
                        </Button>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Vídeos */}
                <div>
                  <Label>Vídeos (Opcional)</Label>
                  <p className="text-xs text-muted-foreground mb-2">
                    Adicione URLs de vídeos (YouTube, Vimeo, etc.)
                  </p>
                  <div className="flex gap-2 mb-4">
                    <Input
                      placeholder="https://youtube.com/watch?v=..."
                      value={videoInput}
                      onChange={(e) => setVideoInput(e.target.value)}
                      onKeyPress={(e) => e.key === "Enter" && addVideo()}
                    />
                    <Button onClick={addVideo} type="button">
                      <Plus className="w-4 h-4" />
                    </Button>
                  </div>
                  {videos.length > 0 && (
                    <div className="space-y-2">
                      {videos.map((video, index) => (
                        <div
                          key={index}
                          className="flex items-center gap-2 p-2 border border-border rounded-lg"
                        >
                          <span className="text-sm flex-1 truncate">
                            {video}
                          </span>
                          <Button
                            size="icon"
                            variant="ghost"
                            onClick={() => removeVideo(index)}
                          >
                            <X className="w-4 h-4" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </Card>

              {/* Tags */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h2 className="text-xl font-bold mb-4">Tags</h2>
                <Separator className="mb-6" />

                <div className="mb-4">
                  <Label>Tags Selecionadas ({tags.length}/15)</Label>
                  <div className="flex flex-wrap gap-2 mt-2 min-h-[40px] p-3 border border-border rounded-lg">
                    {tags.length === 0 ? (
                      <span className="text-sm text-muted-foreground">
                        Nenhuma tag selecionada
                      </span>
                    ) : (
                      tags.map((tag) => (
                        <Badge
                          key={tag}
                          variant="secondary"
                          className="cursor-pointer hover:bg-destructive"
                          onClick={() => removeTag(tag)}
                        >
                          {tag} <X className="w-3 h-3 ml-1" />
                        </Badge>
                      ))
                    )}
                  </div>
                </div>

                <div>
                  <Label>Tags Disponíveis</Label>
                  <div className="flex flex-wrap gap-2 mt-2">
                    {availableTags.map((tag) => (
                      <Badge
                        key={tag}
                        variant="outline"
                        className={`cursor-pointer hover:bg-secondary ${
                          tags.includes(tag) ? "opacity-50" : ""
                        }`}
                        onClick={() => addTag(tag)}
                      >
                        <Plus className="w-3 h-3 mr-1" /> {tag}
                      </Badge>
                    ))}
                  </div>
                </div>
              </Card>

              {/* Configurações */}
              <Card className="p-6 bg-card/50 backdrop-blur-sm">
                <h2 className="text-xl font-bold mb-4">Configurações</h2>
                <Separator className="mb-6" />

                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <Label>Acesso Antecipado</Label>
                      <p className="text-xs text-muted-foreground">
                        Marque se o jogo ainda está em desenvolvimento
                      </p>
                    </div>
                    <Switch
                      checked={formData.isEarlyAccess}
                      onCheckedChange={(checked) =>
                        handleInputChange("isEarlyAccess", checked)
                      }
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div>
                      <Label>Conteúdo Adulto (+18)</Label>
                      <p className="text-xs text-muted-foreground">
                        Marque se contém conteúdo adulto
                      </p>
                    </div>
                    <Switch
                      checked={formData.hasAdultContent}
                      onCheckedChange={(checked) =>
                        handleInputChange("hasAdultContent", checked)
                      }
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div>
                      <Label>Oficina de Mods Habilitada</Label>
                      <p className="text-xs text-muted-foreground">
                        Permitir que jogadores criem mods
                      </p>
                    </div>
                    <Switch
                      checked={formData.modsEnabled}
                      onCheckedChange={(checked) =>
                        handleInputChange("modsEnabled", checked)
                      }
                    />
                  </div>
                </div>
              </Card>

              {/* Validação */}
              <Card className="p-6 bg-gradient-hero border-primary/20">
                <h3 className="font-bold mb-4 flex items-center gap-2">
                  <CheckCircle2 className="w-5 h-5 text-secondary" />
                  Checklist de Publicação
                </h3>
                <ul className="space-y-2 text-sm">
                  <li className="flex items-center gap-2">
                    {formData.title ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Título do jogo
                  </li>
                  <li className="flex items-center gap-2">
                    {formData.description ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Descrição
                  </li>
                  <li className="flex items-center gap-2">
                    {coverImage ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Capa oficial
                  </li>
                  <li className="flex items-center gap-2">
                    {screenshots.length > 0 ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Pelo menos 1 screenshot
                  </li>
                  <li className="flex items-center gap-2">
                    {tags.length > 0 ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Pelo menos 1 tag
                  </li>
                  <li className="flex items-center gap-2">
                    {!formData.hasAdultContent || tags.includes("+18") ? (
                      <CheckCircle2 className="w-4 h-4 text-secondary" />
                    ) : (
                      <AlertCircle className="w-4 h-4 text-yellow-500" />
                    )}
                    Tag +18 (se aplicável)
                  </li>
                </ul>
              </Card>

              {/* Actions */}
              <div className="flex gap-4">
                <Button
                  variant="outline"
                  onClick={() => navigate(-1)}
                  className="flex-1"
                  disabled={isLoading}
                >
                  Cancelar
                </Button>
                <Button
                  variant="accent"
                  onClick={handlePublish}
                  className="flex-1"
                  disabled={isLoading}
                >
                  <Upload className="w-4 h-4 mr-2" />
                  {isLoading ? "Publicando..." : "Publicar Jogo"}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default PublishGame;
