import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import Index from "./pages/Index";
import Catalog from "./pages/Catalog";
import GameDetail from "./pages/GameDetail";
import Library from "./pages/Library";
import Crowdfunding from "./pages/Crowdfunding";
import CampaignDetail from "./pages/CampaignDetail";
import UploadGame from "./pages/UploadGame";
import PublishGame from "./pages/PublishGame";
import CreateCampaign from "./pages/CreateCampaign";
import DeveloperDashboard from "./pages/DeveloperDashboard";
import DeveloperPanel from "./pages/DeveloperPanel";
import Mods from "./pages/Mods";
import Community from "./pages/Community";
import Wallet from "./pages/Wallet";
import Auth from "./pages/Auth";
import Profile from "./pages/Profile";
import WalletSettings from "./pages/WalletSettings";
import Payment from "./pages/Payment";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <AuthProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/auth" element={<Auth />} />
            <Route path="/catalogo" element={<Catalog />} />
            <Route path="/jogo/:slug" element={<GameDetail />} />
            <Route path="/biblioteca" element={<Library />} />
            <Route path="/crowdfunding" element={<Crowdfunding />} />
            <Route path="/campanha/:id" element={<CampaignDetail />} />
            <Route path="/desenvolvedor" element={<DeveloperDashboard />} />
            <Route path="/desenvolvedor/upload" element={<UploadGame />} />
            <Route path="/desenvolvedor/publicar/:id" element={<PublishGame />} />
            <Route path="/desenvolvedor/criar-campanha" element={<CreateCampaign />} />
            <Route path="/mods" element={<Mods />} />
            <Route path="/comunidade" element={<Community />} />
            <Route path="/carteira" element={<Wallet />} />
            {/* Dashboard routes (authenticated) */}
            <Route path="/painel" element={<Profile />} />
            <Route path="/painel/carteira" element={<WalletSettings />} />
            <Route path="/painel/pagamento" element={<Payment />} />
            <Route path="/painel/desenvolvedor" element={<DeveloperPanel />} />
            {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
