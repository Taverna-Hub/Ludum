export interface Review {
  id: string;
  gameId: string;
  userId: string;
  userName: string;
  rating: number;
  comment: string;
  recommended: boolean;
  date: string;
  updatedAt?: string;
  helpful: number;
  deleted?: boolean;
}
