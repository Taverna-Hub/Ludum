import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Progress } from '@/components/ui/progress';
import {
  Upload,
  FileArchive,
  CheckCircle2,
  AlertCircle,
  Loader2,
} from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { DashboardLayout } from '@/layouts/DashboardLayout';

const UploadGame = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadComplete, setUploadComplete] = useState(false);
  const [gameId, setGameId] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    // Validação: deve ser .zip
    if (!selectedFile.name.endsWith('.zip')) {
      toast({
        title: 'Arquivo inválido',
        description: 'O jogo deve ser enviado em um arquivo .zip único.',
        variant: 'destructive',
      });
      return;
    }

    // Validação: tamanho máximo (exemplo: 2GB)
    const maxSize = 2 * 1024 * 1024 * 1024; // 2GB
    if (selectedFile.size > maxSize) {
      toast({
        title: 'Arquivo muito grande',
        description: 'O arquivo não pode ter mais de 2GB.',
        variant: 'destructive',
      });
      return;
    }

    setFile(selectedFile);
  };

  const simulateUpload = () => {
    return new Promise<void>((resolve) => {
      let progress = 0;
      const interval = setInterval(() => {
        progress += 10;
        setUploadProgress(progress);

        if (progress >= 100) {
          clearInterval(interval);
          resolve();
        }
      }, 300);
    });
  };

  const handleUpload = async () => {
    if (!file) {
      toast({
        title: 'Nenhum arquivo selecionado',
        description: 'Selecione um arquivo .zip para fazer upload.',
        variant: 'destructive',
      });
      return;
    }

    setUploading(true);
    setUploadProgress(0);

    try {
      // Simular upload
      await simulateUpload();

      // Simular verificação de malware
      toast({
        title: 'Verificando segurança...',
        description: 'Analisando o arquivo em busca de malware.',
      });

      await new Promise((resolve) => setTimeout(resolve, 2000));

      // Gerar ID fictício
      const newGameId = `game-${Date.now()}`;
      setGameId(newGameId);
      setUploadComplete(true);

      toast({
        title: 'Upload concluído! ✅',
        description:
          'Arquivo verificado e salvo com sucesso. Agora você pode publicar o jogo.',
      });
    } catch (error) {
      toast({
        title: 'Erro no upload',
        description:
          'Ocorreu um erro ao fazer upload do arquivo. Tente novamente.',
        variant: 'destructive',
      });
    } finally {
      setUploading(false);
    }
  };

  const handlePublish = () => {
    if (gameId) {
      navigate(`/desenvolvedor/publicar/${gameId}`);
    }
  };

  return (
    <DashboardLayout>
      <div className="min-h-screen pt-16 pb-12">
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-3xl mx-auto">
            <div className="mb-8">
              <h1 className="text-4xl font-bold mb-4">
                <span className="bg-gradient-secondary bg-clip-text text-transparent">
                  Upload de Jogo
                </span>
              </h1>
              <p className="text-muted-foreground">
                Envie o arquivo .zip do seu jogo. Após a verificação de
                segurança, você poderá adicionar as informações e publicá-lo.
              </p>
            </div>

            <Card className="p-8 bg-card/50 backdrop-blur-sm">
              {!uploadComplete ? (
                <>
                  {/* Upload Area */}
                  <div className="mb-8">
                    <Label
                      htmlFor="game-file"
                      className="text-base font-semibold mb-4 block"
                    >
                      Arquivo do Jogo
                    </Label>
                    <div
                      className="border-2 border-dashed border-border rounded-lg p-12 text-center hover:border-secondary/50 transition-smooth cursor-pointer"
                      onClick={() =>
                        document.getElementById('game-file')?.click()
                      }
                    >
                      <Upload className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
                      <p className="text-lg font-medium mb-2">
                        {file ? file.name : 'Clique para selecionar o arquivo'}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        Apenas arquivos .zip (máximo 2GB)
                      </p>
                      <Input
                        id="game-file"
                        type="file"
                        accept=".zip"
                        className="hidden"
                        onChange={handleFileChange}
                      />
                    </div>
                  </div>

                  {/* File Info */}
                  {file && (
                    <Card className="p-4 bg-muted/30 mb-6">
                      <div className="flex items-center gap-4">
                        <FileArchive className="w-8 h-8 text-primary" />
                        <div className="flex-1">
                          <p className="font-semibold">{file.name}</p>
                          <p className="text-sm text-muted-foreground">
                            {(file.size / (1024 * 1024)).toFixed(2)} MB
                          </p>
                        </div>
                      </div>
                    </Card>
                  )}

                  {/* Upload Progress */}
                  {uploading && (
                    <div className="mb-6">
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-sm font-medium">
                          Fazendo upload...
                        </span>
                        <span className="text-sm text-muted-foreground">
                          {uploadProgress}%
                        </span>
                      </div>
                      <Progress value={uploadProgress} className="h-3" />
                    </div>
                  )}

                  {/* Requirements */}
                  <Card className="p-6 bg-gradient-hero border-primary/20 mb-6">
                    <h3 className="font-bold text-lg mb-4">
                      Requisitos do Pacote
                    </h3>
                    <ul className="space-y-2 text-sm text-muted-foreground">
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                        <span>
                          Arquivo .zip único contendo todos os arquivos do jogo
                        </span>
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                        <span>
                          Nome do arquivo seguindo a convenção:
                          game-name-version.zip
                        </span>
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                        <span>
                          Manifesto de estrutura incluído (manifest.json)
                        </span>
                      </li>
                      <li className="flex items-start gap-2">
                        <CheckCircle2 className="w-5 h-5 text-secondary flex-shrink-0 mt-0.5" />
                        <span>Tamanho máximo: 2GB</span>
                      </li>
                      <li className="flex items-start gap-2">
                        <AlertCircle className="w-5 h-5 text-yellow-500 flex-shrink-0 mt-0.5" />
                        <span>
                          Será realizada verificação automática de malware
                        </span>
                      </li>
                    </ul>
                  </Card>

                  {/* Actions */}
                  <div className="flex gap-4">
                    <Button
                      variant="accent"
                      onClick={handleUpload}
                      disabled={!file || uploading}
                      className="flex-1"
                    >
                      {uploading ? (
                        <>
                          <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                          Fazendo Upload...
                        </>
                      ) : (
                        <>
                          <Upload className="w-4 h-4 mr-2" />
                          Fazer Upload
                        </>
                      )}
                    </Button>
                  </div>
                </>
              ) : (
                /* Upload Success */
                <div className="text-center py-8">
                  <div className="w-20 h-20 rounded-full bg-secondary/20 flex items-center justify-center mx-auto mb-6">
                    <CheckCircle2 className="w-10 h-10 text-secondary" />
                  </div>
                  <h2 className="text-2xl font-bold mb-4">Upload Concluído!</h2>
                  <p className="text-muted-foreground mb-8 max-w-md mx-auto">
                    Seu jogo foi enviado e verificado com sucesso. Agora você
                    pode adicionar as informações e publicá-lo na plataforma.
                  </p>
                  <div className="flex flex-col sm:flex-row gap-4 justify-center">
                    <Button
                      variant="outline"
                      onClick={() => navigate('/desenvolvedor')}
                    >
                      Voltar ao Painel
                    </Button>
                    <Button variant="accent" onClick={handlePublish}>
                      Publicar Jogo Agora
                    </Button>
                  </div>
                </div>
              )}
            </Card>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default UploadGame;
