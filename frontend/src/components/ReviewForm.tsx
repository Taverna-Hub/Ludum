import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { Star, ThumbsUp, ThumbsDown } from "lucide-react";
import { toast } from "sonner";

interface ReviewFormProps {
  gameId: string;
  existingReview?: {
    id: string;
    rating: number;
    comment: string;
    recommended: boolean;
  };
  onSubmit: (review: {
    rating: number;
    comment: string;
    recommended: boolean;
  }) => void;
  onCancel?: () => void;
}

export const ReviewForm = ({ gameId, existingReview, onSubmit, onCancel }: ReviewFormProps) => {
  const [rating, setRating] = useState(existingReview?.rating || 0);
  const [hoveredRating, setHoveredRating] = useState(0);
  const [comment, setComment] = useState(existingReview?.comment || "");
  const [recommended, setRecommended] = useState(existingReview?.recommended ?? true);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (rating === 0) {
      toast.error("Por favor, selecione uma nota para o jogo");
      return;
    }
    
    if (comment.trim().length < 10) {
      toast.error("O comentário deve ter pelo menos 10 caracteres");
      return;
    }

    onSubmit({ rating, comment, recommended });
  };

  return (
    <Card className="p-6 bg-card/50 backdrop-blur-sm">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-2">
            Nota do jogo *
          </label>
          <div className="flex gap-2">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                onClick={() => setRating(star)}
                onMouseEnter={() => setHoveredRating(star)}
                onMouseLeave={() => setHoveredRating(0)}
                className="transition-transform hover:scale-110"
              >
                <Star
                  className={`w-8 h-8 ${
                    star <= (hoveredRating || rating)
                      ? "fill-primary text-primary"
                      : "text-muted"
                  }`}
                />
              </button>
            ))}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">
            Você recomenda este jogo? *
          </label>
          <div className="flex gap-3">
            <Button
              type="button"
              variant={recommended ? "default" : "outline"}
              onClick={() => setRecommended(true)}
              className="flex-1"
            >
              <ThumbsUp className="w-4 h-4 mr-2" />
              Sim, recomendo
            </Button>
            <Button
              type="button"
              variant={!recommended ? "destructive" : "outline"}
              onClick={() => setRecommended(false)}
              className="flex-1"
            >
              <ThumbsDown className="w-4 h-4 mr-2" />
              Não recomendo
            </Button>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">
            Comentário * (mínimo 10 caracteres)
          </label>
          <Textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Compartilhe sua experiência com o jogo..."
            className="min-h-[120px]"
            maxLength={1000}
          />
          <p className="text-xs text-muted-foreground mt-1">
            {comment.length}/1000 caracteres
          </p>
        </div>

        <div className="flex gap-3 pt-2">
          <Button type="submit" className="flex-1">
            {existingReview ? "Atualizar Review" : "Publicar Review"}
          </Button>
          {onCancel && (
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancelar
            </Button>
          )}
        </div>
      </form>
    </Card>
  );
};
