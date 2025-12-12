import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Wallet as WalletIcon,
  Plus,
  TrendingUp,
  TrendingDown,
  Clock,
  CheckCircle,
  AlertCircle,
  DollarSign,
  ArrowDownToLine,
} from 'lucide-react';
import { mockWallet } from '@/data/mockData';
import { toast } from 'sonner';

const Wallet = () => {
  const navigate = useNavigate();
  const [depositAmount, setDepositAmount] = useState('');
  const [withdrawAmount, setWithdrawAmount] = useState('');

  const handleDeposit = () => {
    const amount = parseFloat(depositAmount);
    if (amount && amount > 0) {
      if (amount < 5) {
        toast.error('Valor mínimo é R$ 5,00');
        return;
      }
      navigate(`/payment?amount=${amount}`);
    }
  };

  const handleWithdraw = () => {
    const amount = parseFloat(withdrawAmount);

    if (!amount || amount <= 0) {
      toast.error('Por favor, insira um valor válido para saque.');
      return;
    }

    if (amount < 10) {
      toast.error('O valor mínimo para saque é R$ 10,00');
      return;
    }

    if (amount > mockWallet.availableBalance) {
      toast.error('Saldo insuficiente para realizar o saque.');
      return;
    }

    toast.success(
      `Saque de R$ ${amount.toFixed(
        2,
      )} solicitado com sucesso! O valor será transferido em até 2 dias úteis.`,
    );
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
    <div className="min-h-screen pt-16">
      {/* Header */}
      <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
        <div className="container mx-auto">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            <span className="bg-gradient-primary bg-clip-text text-transparent">
              Carteira
            </span>
          </h1>
          <p className="text-lg text-muted-foreground">
            Gerencie seu saldo e acompanhe todas as suas transações
          </p>
        </div>
      </section>

      <div className="container mx-auto px-4 py-12">
        <div className="max-w-5xl mx-auto">
          {/* Balance Cards */}
          <div className="grid md:grid-cols-2 gap-6 mb-8">
            {/* Available Balance */}
            <Card className="p-6 bg-gradient-primary relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-16 -mt-16" />
              <div className="relative z-10">
                <div className="flex items-center gap-2 mb-2">
                  <WalletIcon className="w-5 h-5 text-primary-foreground/80" />
                  <p className="text-primary-foreground/80 text-sm font-medium">
                    Saldo Disponível
                  </p>
                </div>
                <p className="text-4xl font-bold text-primary-foreground mb-1">
                  R$ {mockWallet.availableBalance.toFixed(2)}
                </p>
                <p className="text-primary-foreground/60 text-sm">
                  Pode ser usado imediatamente
                </p>
              </div>
            </Card>

            {/* Blocked Balance */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm border-border/50">
              <div className="flex items-center gap-2 mb-2">
                <Clock className="w-5 h-5 text-muted-foreground" />
                <p className="text-muted-foreground text-sm font-medium">
                  Saldo Bloqueado
                </p>
              </div>
              <p className="text-4xl font-bold mb-1">
                R$ {mockWallet.blockedBalance.toFixed(2)}
              </p>
              <p className="text-muted-foreground text-sm">
                Aguardando confirmação ou trava antifraude
              </p>
            </Card>
          </div>

          {/* Actions Grid */}
          <div className="grid md:grid-cols-2 gap-6 mb-8">
            {/* Add Funds */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
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
                    min="0"
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
              <div className="mt-4 p-3 bg-primary/10 border border-primary/20 rounded-lg">
                <p className="text-xs text-muted-foreground">
                  ⏰ Valores acima de R$ 100 terão trava de 24h após
                  confirmação.
                </p>
              </div>
            </Card>

            {/* Withdraw Funds */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
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
                    max={mockWallet.availableBalance}
                    step="0.01"
                  />
                </div>
                <Button
                  variant="outline"
                  onClick={handleWithdraw}
                  disabled={
                    !withdrawAmount ||
                    parseFloat(withdrawAmount) < 10 ||
                    parseFloat(withdrawAmount) > mockWallet.availableBalance
                  }
                >
                  <ArrowDownToLine className="w-4 h-4 mr-2" />
                  Sacar
                </Button>
              </div>
              <div className="mt-4 p-3 bg-secondary/10 border border-secondary/20 rounded-lg">
                <p className="text-xs text-muted-foreground">
                  💰 Valor mínimo: R$ 10,00 | Disponível: R${' '}
                  {mockWallet.availableBalance.toFixed(2)}
                </p>
              </div>
            </Card>
          </div>

          {/* Transaction History */}
          <Card className="p-6 bg-card/50 backdrop-blur-sm">
            <h2 className="font-bold text-lg mb-6">Histórico de Transações</h2>

            <div className="space-y-4">
              {mockWallet.transactions.map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex items-center gap-4 p-4 bg-muted/30 rounded-lg hover:bg-muted/50 transition-smooth"
                >
                  {/* Icon */}
                  <div className="flex-shrink-0">
                    {getTransactionIcon(transaction.type)}
                  </div>

                  {/* Details */}
                  <div className="flex-1 min-w-0">
                    <p className="font-medium mb-1">
                      {transaction.description}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(transaction.date).toLocaleDateString('pt-BR', {
                        day: '2-digit',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </p>
                  </div>

                  {/* Amount */}
                  <div className="text-right flex-shrink-0">
                    <p
                      className={`font-bold text-lg ${
                        transaction.amount > 0
                          ? 'text-secondary'
                          : 'text-destructive'
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

            {/* Load More */}
            <div className="text-center mt-6">
              <Button variant="outline">Ver todas as transações</Button>
            </div>
          </Card>

          {/* Info Card */}
          <Card className="mt-8 p-6 bg-secondary/10 border-secondary/20">
            <h3 className="font-bold text-lg mb-3">💡 Sobre a Carteira</h3>
            <ul className="space-y-2 text-muted-foreground text-sm">
              <li>
                • Saldo disponível pode ser usado para compras e crowdfunding
              </li>
              <li>
                • Reembolsos de compras retornam para a carteira em até 24h
              </li>
              <li>
                • Valores bloqueados liberam após confirmação de pagamento
              </li>
              <li>
                • Trava antifraude de 24h se aplica a depósitos acima de R$ 100
              </li>
              <li>
                • Saques têm valor mínimo de R$ 10,00 e são processados em até 2
                dias úteis
              </li>
              <li>
                • Histórico completo com idempotência e trilha de auditoria
              </li>
              <li>• Desenvolvedores podem sacar vendas 24h após a compra</li>
            </ul>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Wallet;
