import { useState, useEffect } from "react";
import { DashboardLayout } from "@/layouts/DashboardLayout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { listarJogadores, JogadorResumo } from "@/http/requests/jogadorRequests";
import { useSeguimento } from "@/hooks/useSeguimento";
import { useAuthContext } from "@/contexts/AuthContext";
import { UserPlus, UserMinus, Loader2, Users } from "lucide-react";
import { toast } from "sonner";

const Players = () => {
  const [jogadores, setJogadores] = useState<JogadorResumo[]>([]);
  const [loading, setLoading] = useState(true);
  const [seguindoLoading, setSeguindoLoading] = useState<string | null>(null);

  const { toggleSeguir, verificarMultiplosSeguindo, followingMap } = useSeguimento();
  const { user } = useAuthContext();

  useEffect(() => {
    carregarJogadores();
  }, []);

  const carregarJogadores = async () => {
    try {
      setLoading(true);
      const data = await listarJogadores();
      setJogadores(data);

      // Verificar quais jogadores já estão sendo seguidos
      if (data.length > 0) {
        const ids = data.map((j) => j.id);
        await verificarMultiplosSeguindo(ids);
      }
    } catch (error) {
      console.error("Erro ao carregar jogadores:", error);
      toast.error("Erro ao carregar jogadores");
    } finally {
      setLoading(false);
    }
  };

  const handleToggleSeguir = async (jogador: JogadorResumo) => {
    try {
      setSeguindoLoading(jogador.id);
      const estaSeguindo = followingMap[jogador.id];
      
      await toggleSeguir(
        jogador.id,
        "CONTA",
        jogador.nome,
        !estaSeguindo
      );
    } catch (error) {
      console.error("Erro ao seguir/deixar de seguir:", error);
    } finally {
      setSeguindoLoading(null);
    }
  };

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex items-center justify-center min-h-[400px]">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout>
      <div className="container mx-auto py-8 px-4">
        <Card>
          <CardHeader>
            <div className="flex items-center gap-3">
              <Users className="h-8 w-8 text-primary" />
              <div>
                <CardTitle className="text-3xl">Jogadores</CardTitle>
                <CardDescription className="text-base mt-1">
                  Siga outros jogadores da plataforma para acompanhar suas atividades
                </CardDescription>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {jogadores.filter(j => j.id !== user?.id).length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">
                <Users className="h-16 w-16 mx-auto mb-4 opacity-50" />
                <p className="text-lg">Nenhum jogador encontrado</p>
              </div>
            ) : (
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="font-semibold">Nome do Jogador</TableHead>
                      <TableHead className="text-right font-semibold w-[200px]">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {jogadores.filter(j => j.id !== user?.id).map((jogador) => {
                      const estaSeguindo = followingMap[jogador.id];
                      const isLoading = seguindoLoading === jogador.id;

                      return (
                        <TableRow key={jogador.id}>
                          <TableCell className="font-medium">
                            {jogador.nome}
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant={estaSeguindo ? "outline" : "default"}
                              size="sm"
                              onClick={() => handleToggleSeguir(jogador)}
                              disabled={isLoading}
                              className="gap-2"
                            >
                              {isLoading ? (
                                <>
                                  <Loader2 className="h-4 w-4 animate-spin" />
                                  Processando...
                                </>
                              ) : estaSeguindo ? (
                                <>
                                  <UserMinus className="h-4 w-4" />
                                  Deixar de Seguir
                                </>
                              ) : (
                                <>
                                  <UserPlus className="h-4 w-4" />
                                  Seguir
                                </>
                              )}
                            </Button>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
};

export default Players;
