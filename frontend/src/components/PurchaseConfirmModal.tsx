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
import { ShoppingCart } from 'lucide-react';

interface PurchaseConfirmModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  gameTitle: string;
  gamePrice: number;
  onConfirm: () => void;
  loading?: boolean;
}

export function PurchaseConfirmModal({
  open,
  onOpenChange,
  gameTitle,
  gamePrice,
  onConfirm,
  loading = false,
}: PurchaseConfirmModalProps) {
  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle className="flex items-center gap-2">
            <ShoppingCart className="w-5 h-5 text-primary" />
            Confirmar Compra
          </AlertDialogTitle>
          <AlertDialogDescription className="space-y-3 pt-4">
            <div className="text-foreground font-medium text-base">
              Você está prestes a comprar:
            </div>
            <div className="bg-muted p-4 rounded-lg space-y-2">
              <div className="font-semibold text-lg text-foreground">
                {gameTitle}
              </div>
              <div className="flex justify-between items-center border-t border-border pt-2">
                <span className="text-muted-foreground">Valor:</span>
                <span className="text-2xl font-bold text-primary">
                  R$ {gamePrice.toFixed(2)}
                </span>
              </div>
            </div>
            <div className="text-sm text-muted-foreground">
              O valor será debitado da sua carteira e o jogo será adicionado à
              sua biblioteca.
            </div>
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={loading}>Cancelar</AlertDialogCancel>
          <AlertDialogAction
            onClick={(e) => {
              e.preventDefault();
              onConfirm();
            }}
            disabled={loading}
            className="bg-primary hover:bg-primary/90"
          >
            {loading ? 'Processando...' : 'Confirmar Compra'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
