import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { 
  Wallet, Plus, Save, TrendingUp, TrendingDown, 
  Clock, CheckCircle, AlertCircle, DollarSign, ArrowDownToLine, Key 
} from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { useAuthContext } from '@/contexts/AuthContext';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { mockWallet } from '@/data/mockData';
import { toast } from 'sonner';

const WalletSettings = () => {
  const navigate = useNavigate();
  const { user, updateProfile } = useAuthContext();
  const [pixKey, setPixKey] = useState(user?.pixKey || '');
  const [depositAmount, setDepositAmount] = useState('');
  const [withdrawAmount, setWithdrawAmount] = useState('');
  const [isSaving, setIsSaving] = useState(false);

  const handleSavePixKey = async () => {
    setIsSaving(true);
    try {
      updateProfile({ pixKey });
      toast.success('Chave PIX salva com sucesso!');
    } catch {
      toast.error('Erro ao salvar chave PIX');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDeposit = () => {
    const amount = parseFloat(depositAmount);
    if (amount && amount > 0) {
      navigate(`/painel/pagamento?amount=${amount}`);
    }
  };

  const handleWithdraw = () => {
    const amount = parseFloat(withdrawAmount);
    
    if (!user?.pixKey) {
      toast.error('Configure sua chave PIX antes de sacar');
      return;
    }

    if (!amount || amount <= 0) {
      toast.error('Por favor, insira um valor válido');
      return;
    }

    if (amount < 10) {
      toast.error('Valor mínimo para saque: R$ 10,00');
      return;
    }

    if (amount > mockWallet.availableBalance) {
      toast.error('Saldo insuficiente');
      return;
    }

    toast.success(`Saque de R$ ${amount.toFixed(2)} solicitado! Será transferido em até 2 dias úteis.`);
    setWithdrawAmount('');
  };

  const getTransactionIcon = (type: string) => {
    switch (type) {
      case 'deposit':
        return <TrendingUp className="w-5 h-5 text-secondary" />;
      case 'withdraw':
        return <ArrowDownToLine className="w-5 h-5 text-primary" />;
      case 'purchase':
      case 'crowdfunding':
        return <TrendingDown className="w-5 h-5 text-destructive" />;
      case 'refund':
        return <TrendingUp className="w-5 h-5 text-secondary" />;
      default:
        return <DollarSign className="w-5 h-5" />;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'completed':
        return (
          <Badge className="bg-secondary/20 text-secondary border-secondary/30">
            <CheckCircle className="w-3 h-3 mr-1" />
            Concluído
          </Badge>
        );
      case 'pending':
        return (
          <Badge className="bg-primary/20 text-primary border-primary/30">
            <Clock className="w-3 h-3 mr-1" />
            Pendente
          </Badge>
        );
      case 'blocked':
        return (
          <Badge className="bg-destructive/20 text-destructive border-destructive/30">
            <AlertCircle className="w-3 h-3 mr-1" />
            Bloqueado
          </Badge>
        );
      default:
        return null;
    }
  };

  return (
    <DashboardLayout>
      <div className="p-6 md:p-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">
            <span className="bg-gradient-primary bg-clip-text text-transparent">Carteira</span>
          </h1>
          <p className="text-muted-foreground">Gerencie seu saldo e configure sua chave PIX</p>
        </div>

        <div className="max-w-4xl space-y-6">
          {/* Balance Cards */}
          <div className="grid md:grid-cols-2 gap-6">
            <Card className="p-6 bg-gradient-primary relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-foreground/10 rounded-full -mr-16 -mt-16" />
              <div className="relative z-10">
                <div className="flex items-center gap-2 mb-2">
                  <Wallet className="w-5 h-5 text-primary-foreground/80" />
                  <p className="text-primary-foreground/80 text-sm font-medium">Saldo Disponível</p>
                </div>
                <p className="text-4xl font-bold text-primary-foreground mb-1">
                  R$ {mockWallet.availableBalance.toFixed(2)}
                </p>
                <p className="text-primary-foreground/60 text-sm">Pode ser usado imediatamente</p>
              </div>
            </Card>

            <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
              <div className="flex items-center gap-2 mb-2">
                <Clock className="w-5 h-5 text-muted-foreground" />
                <p className="text-muted-foreground text-sm font-medium">Saldo Bloqueado</p>
              </div>
              <p className="text-4xl font-bold mb-1">R$ {mockWallet.blockedBalance.toFixed(2)}</p>
              <p className="text-muted-foreground text-sm">Aguardando confirmação</p>
            </Card>
          </div>

          {/* PIX Key Configuration */}
          <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
            <h2 className="font-bold text-lg mb-4 flex items-center gap-2">
              <Key className="w-5 h-5 text-secondary" />
              Chave PIX para Saque
            </h2>
            <div className="flex gap-3">
              <div className="flex-1">
                <Label htmlFor="pixKey" className="sr-only">Chave PIX</Label>
                <Input
                  id="pixKey"
                  placeholder="CPF, Email, Telefone ou Chave Aleatória"
                  value={pixKey}
                  onChange={(e) => setPixKey(e.target.value)}
                />
              </div>
              <Button onClick={handleSavePixKey} disabled={isSaving}>
                <Save className="w-4 h-4 mr-2" />
                {isSaving ? 'Salvando...' : 'Salvar'}
              </Button>
            </div>
            {!user?.pixKey && (
              <p className="mt-2 text-xs text-muted-foreground">
                Configure sua chave PIX para poder realizar saques
              </p>
            )}
          </Card>

          {/* Actions Grid */}
          <div className="grid md:grid-cols-2 gap-6">
            {/* Add Funds */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
              <h2 className="font-bold text-lg mb-4 flex items-center gap-2">
                <Plus className="w-5 h-5 text-secondary" />
                Adicionar Saldo
              </h2>
              <div className="flex gap-3">
                <div className="flex-1">
                  <Input
                    type="number"
                    placeholder="Valor (R$)"
                    value={depositAmount}
                    onChange={(e) => setDepositAmount(e.target.value)}
                    min="1"
                    step="0.01"
                  />
                </div>
                <Button
                  variant="accent"
                  onClick={handleDeposit}
                  disabled={!depositAmount || parseFloat(depositAmount) <= 0}
                >
                  <Plus className="w-4 h-4 mr-2" />
                  Adicionar
                </Button>
              </div>
            </Card>

            {/* Withdraw */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
              <h2 className="font-bold text-lg mb-4 flex items-center gap-2">
                <ArrowDownToLine className="w-5 h-5 text-primary" />
                Sacar Saldo
              </h2>
              <div className="flex gap-3">
                <div className="flex-1">
                  <Input
                    type="number"
                    placeholder="Valor (R$)"
                    value={withdrawAmount}
                    onChange={(e) => setWithdrawAmount(e.target.value)}
                    min="10"
                    step="0.01"
                  />
                </div>
                <Button variant="outline" onClick={handleWithdraw}>
                  <ArrowDownToLine className="w-4 h-4 mr-2" />
                  Sacar
                </Button>
              </div>
              <p className="mt-2 text-xs text-muted-foreground">
                Mínimo: R$ 10,00 | Disponível: R$ {mockWallet.availableBalance.toFixed(2)}
              </p>
            </Card>
          </div>

          {/* Transaction History */}
          <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
            <h2 className="font-bold text-lg mb-6">Histórico de Transações</h2>
            
            <div className="space-y-4">
              {mockWallet.transactions.map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex items-center gap-4 p-4 bg-muted/30 rounded-lg hover:bg-muted/50 transition-all"
                >
                  <div className="flex-shrink-0">{getTransactionIcon(transaction.type)}</div>
                  <div className="flex-1 min-w-0">
                    <p className="font-medium mb-1">{transaction.description}</p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(transaction.date).toLocaleDateString('pt-BR', {
                        day: '2-digit',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </p>
                  </div>
                  <div className="text-right flex-shrink-0">
                    <p
                      className={`font-bold text-lg ${
                        transaction.amount > 0 ? 'text-secondary' : 'text-destructive'
                      }`}
                    >
                      {transaction.amount > 0 ? '+' : ''}
                      R$ {Math.abs(transaction.amount).toFixed(2)}
                    </p>
                    {getStatusBadge(transaction.status)}
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default WalletSettings;
