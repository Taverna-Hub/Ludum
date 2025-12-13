export interface Game {
  id: string;
  title: string;
  slug: string;
  description: string;
  price: number;
  originalPrice?: number;
  coverImage: string;
  screenshots: string[];
  tags: string[];
  developerId: string;
  developerName: string;
  rating: number;
  reviewCount: number;
  releaseDate: string;
  isEarlyAccess: boolean;
  hasAdultContent: boolean;
  modsEnabled: boolean;
  downloadCount: number;
}