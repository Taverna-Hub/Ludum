import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Wallet, AlertTriangle, Clock } from 'lucide-react';

interface InsufficientBalanceModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  gameTitle: string;
  gamePrice: number;
  currentBalance: number;
  missingAmount: number;
  onAddFunds: () => void;
}

export function InsufficientBalanceModal({
  open,
  onOpenChange,
  gameTitle,
  gamePrice,
  currentBalance,
  missingAmount,
  onAddFunds,
}: InsufficientBalanceModalProps) {
  const needsWaitTime = gamePrice > 100;

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle className="flex items-center gap-2 text-destructive">
            <AlertTriangle className="w-5 h-5" />
            Saldo Insuficiente
          </AlertDialogTitle>
          <AlertDialogDescription className="space-y-4 pt-4">
            <div className="text-foreground">
              <p className="mb-4">
                Você não tem saldo suficiente para comprar{' '}
                <span className="font-semibold">{gameTitle}</span>.
              </p>

              <div className="bg-muted p-4 rounded-lg space-y-2 mb-4">
                <div className="flex justify-between items-center">
                  <span className="text-muted-foreground">Preço do jogo:</span>
                  <span className="font-semibold">R$ {gamePrice.toFixed(2)}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-muted-foreground">Seu saldo atual:</span>
                  <span className="font-semibold">R$ {currentBalance.toFixed(2)}</span>
                </div>
                <div className="flex justify-between items-center border-t border-border pt-2">
                  <span className="text-muted-foreground">Falta adicionar:</span>
                  <span className="font-bold text-destructive text-lg">
                    R$ {missingAmount.toFixed(2)}
                  </span>
                </div>
              </div>

              {needsWaitTime && (
                <div className="bg-primary/10 border border-primary/20 rounded-lg p-3 flex items-start gap-2">
                  <Clock className="w-5 h-5 text-primary flex-shrink-0 mt-0.5" />
                  <div className="text-sm">
                    <p className="font-medium text-primary mb-1">
                      Atenção: Trava de segurança
                    </p>
                    <p className="text-muted-foreground">
                      Como o valor é superior a R$ 100,00, o saldo adicionado
                      ficará bloqueado por 24 horas após a confirmação do
                      pagamento.
                    </p>
                  </div>
                </div>
              )}

              <p className="text-sm text-muted-foreground mt-4">
                Deseja adicionar saldo agora usando cartão de crédito?
              </p>
            </div>
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancelar</AlertDialogCancel>
          <AlertDialogAction
            onClick={(e) => {
              e.preventDefault();
              onAddFunds();
            }}
            className="bg-primary hover:bg-primary/90"
          >
            <Wallet className="w-4 h-4 mr-2" />
            Adicionar Saldo
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
