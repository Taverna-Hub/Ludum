import { Hero } from "@/components/Hero";
import { ForPlayers } from "@/components/ForPlayers";
import { ForDevelopers } from "@/components/ForDevelopers";
import { Features } from "@/components/Features";
import { CTA } from "@/components/CTA";
import { Footer } from "@/components/Footer";
import { Navigation } from "@/components/Navigation";

const Index = () => {
  return (
    <div className="min-h-screen">
      <Navigation />
      <div className="pt-16">
        <Hero />
        <ForPlayers />
        <ForDevelopers />
        <Features />
        <CTA />
        <Footer />
      </div>
    </div>
  );
};

export default Index;
