import { Gamepad2 } from "lucide-react";

export const Footer = () => {
  const links = {
    platform: [
      { label: "Catálogo", href: "#" },
      { label: "Crowdfunding", href: "#" },
      { label: "Oficina de Mods", href: "#" },
      { label: "Comunidade", href: "#" },
    ],
    developers: [
      { label: "Publicar Jogo", href: "#" },
      { label: "Documentação", href: "#" },
      { label: "Políticas", href: "#" },
      { label: "Suporte", href: "#" },
    ],
    company: [
      { label: "Sobre", href: "#" },
      { label: "Blog", href: "#" },
      { label: "Carreiras", href: "#" },
      { label: "Contato", href: "#" },
    ],
  };

  return (
    <footer className="border-t border-border/50 bg-card/30 backdrop-blur-sm">
      <div className="container mx-auto px-4 py-12">
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 mb-8">
          {/* Brand */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Gamepad2 className="w-8 h-8 text-primary-glow" />
              <span className="text-2xl font-bold bg-gradient-primary bg-clip-text text-transparent">
                Ludum
              </span>
            </div>
            <p className="text-muted-foreground text-sm">
              A plataforma completa que conecta jogadores e desenvolvedores em um ecossistema vibrante de jogos digitais.
            </p>
          </div>

          {/* Links */}
          <div>
            <h3 className="font-semibold mb-4">Plataforma</h3>
            <ul className="space-y-2">
              {links.platform.map((link, index) => (
                <li key={index}>
                  <a href={link.href} className="text-muted-foreground hover:text-primary transition-smooth text-sm">
                    {link.label}
                  </a>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h3 className="font-semibold mb-4">Desenvolvedores</h3>
            <ul className="space-y-2">
              {links.developers.map((link, index) => (
                <li key={index}>
                  <a href={link.href} className="text-muted-foreground hover:text-primary transition-smooth text-sm">
                    {link.label}
                  </a>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h3 className="font-semibold mb-4">Empresa</h3>
            <ul className="space-y-2">
              {links.company.map((link, index) => (
                <li key={index}>
                  <a href={link.href} className="text-muted-foreground hover:text-primary transition-smooth text-sm">
                    {link.label}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom */}
        <div className="pt-8 border-t border-border/50 flex flex-col sm:flex-row justify-between items-center gap-4">
          <p className="text-muted-foreground text-sm">
            © 2025 Ludum. Todos os direitos reservados.
          </p>
          <div className="flex gap-6">
            <a href="#" className="text-muted-foreground hover:text-primary transition-smooth text-sm">
              Termos de Uso
            </a>
            <a href="#" className="text-muted-foreground hover:text-primary transition-smooth text-sm">
              Privacidade
            </a>
            <a href="#" className="text-muted-foreground hover:text-primary transition-smooth text-sm">
              Cookies
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};
