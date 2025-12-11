import { Link, useLocation, useNavigate } from 'react-router-dom';
import { 
  Gamepad2, User, Wallet, ShoppingBag, Library, Heart, 
  Code, Upload, Megaphone, LogOut, ChevronLeft, ChevronRight 
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuthContext } from '@/contexts/AuthContext';
import { useState } from 'react';
import { cn } from '@/lib/utils';

export const DashboardSidebar = () => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuthContext();

  const playerLinks = [
    { label: 'Perfil', href: '/painel', icon: User },
    { label: 'Carteira', href: '/painel/carteira', icon: Wallet },
    { label: 'Catálogo', href: '/catalogo', icon: ShoppingBag },
    { label: 'Biblioteca', href: '/biblioteca', icon: Library },
    { label: 'Crowdfunding', href: '/crowdfunding', icon: Heart },
  ];

  const developerLinks = [
    { label: 'Painel Dev', href: '/painel/desenvolvedor', icon: Code },
    { label: 'Upload de Jogo', href: '/desenvolvedor/upload', icon: Upload },
    { label: 'Criar Campanha', href: '/desenvolvedor/criar-campanha', icon: Megaphone },
  ];

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const NavLink = ({ href, icon: Icon, label }: { href: string; icon: React.ElementType; label: string }) => {
    const isActive = location.pathname === href;
    
    return (
      <Link
        to={href}
        className={cn(
          'flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all',
          isActive 
            ? 'bg-primary/20 text-primary-glow' 
            : 'text-muted-foreground hover:text-foreground hover:bg-muted/50'
        )}
      >
        <Icon className="w-5 h-5 flex-shrink-0" />
        {!collapsed && <span className="text-sm font-medium">{label}</span>}
      </Link>
    );
  };

  return (
    <aside
      className={cn(
        'fixed left-0 top-0 h-full bg-card border-r border-border/50 flex flex-col transition-all duration-300 z-40',
        collapsed ? 'w-16' : 'w-64'
      )}
    >
      {/* Header */}
      <div className="p-4 border-b border-border/50">
        <Link to="/" className="flex items-center gap-2">
          <Gamepad2 className="w-8 h-8 text-primary-glow flex-shrink-0" />
          {!collapsed && (
            <span className="text-xl font-bold bg-gradient-primary bg-clip-text text-transparent">
              Ludum
            </span>
          )}
        </Link>
      </div>

      {/* User Info */}
      {!collapsed && user && (
        <div className="p-4 border-b border-border/50">
          <p className="font-medium text-sm truncate">{user.name}</p>
          <p className="text-xs text-muted-foreground truncate">{user.email}</p>
          <span className="inline-block mt-2 text-xs px-2 py-1 rounded-full bg-primary/20 text-primary">
            {user.accountType === 'developer' ? 'Desenvolvedor' : 'Jogador'}
          </span>
        </div>
      )}

      {/* Navigation */}
      <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
        {playerLinks.map((link) => (
          <NavLink key={link.href} {...link} />
        ))}

        {user?.accountType === 'developer' && (
          <>
            <div className={cn('my-4 border-t border-border/50', collapsed && 'mx-2')} />
            {!collapsed && (
              <p className="px-3 text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-2">
                Desenvolvedor
              </p>
            )}
            {developerLinks.map((link) => (
              <NavLink key={link.href} {...link} />
            ))}
          </>
        )}
      </nav>

      {/* Footer */}
      <div className="p-3 border-t border-border/50 space-y-2">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setCollapsed(!collapsed)}
          className="w-full justify-center"
        >
          {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
        </Button>
        <Button
          variant="ghost"
          size="sm"
          onClick={handleLogout}
          className={cn('w-full text-destructive hover:text-destructive hover:bg-destructive/10', collapsed ? 'justify-center' : 'justify-start')}
        >
          <LogOut className="w-4 h-4" />
          {!collapsed && <span className="ml-2">Sair</span>}
        </Button>
      </div>
    </aside>
  );
};
