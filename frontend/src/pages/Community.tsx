import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Heart, MessageSquare, Share2, Image as ImageIcon, UserPlus, UserMinus, Ban } from "lucide-react";
import { mockPosts, mockGames } from "@/data/mockData";
import { toast } from "sonner";

const Community = () => {
  const [newPost, setNewPost] = useState("");
  const [likedPosts, setLikedPosts] = useState<string[]>([]);
  const [followedUsers, setFollowedUsers] = useState<string[]>([]);
  const [blockedUsers, setBlockedUsers] = useState<string[]>([]);
  const [followedTags, setFollowedTags] = useState<string[]>([]);
  const [followedDevelopers, setFollowedDevelopers] = useState<string[]>([]);

  const handleLike = (postId: string) => {
    if (likedPosts.includes(postId)) {
      setLikedPosts(likedPosts.filter(id => id !== postId));
    } else {
      setLikedPosts([...likedPosts, postId]);
    }
  };

  const handlePost = () => {
    if (newPost.trim()) {
      toast.success("Post publicado com sucesso!");
      setNewPost("");
    }
  };

  const handleFollowUser = (userId: string, userName: string) => {
    if (followedUsers.includes(userId)) {
      setFollowedUsers(followedUsers.filter(id => id !== userId));
      toast.success(`Você deixou de seguir ${userName}`);
    } else {
      setFollowedUsers([...followedUsers, userId]);
      toast.success(`Você está seguindo ${userName}`);
    }
  };

  const handleBlockUser = (userId: string, userName: string) => {
    if (blockedUsers.includes(userId)) {
      setBlockedUsers(blockedUsers.filter(id => id !== userId));
      toast.success(`${userName} foi desbloqueado`);
    } else {
      setBlockedUsers([...blockedUsers, userId]);
      toast.success(`${userName} foi bloqueado`);
    }
  };

  const handleFollowTag = (tag: string) => {
    if (followedTags.includes(tag)) {
      setFollowedTags(followedTags.filter(t => t !== tag));
      toast.success(`Você deixou de seguir #${tag}`);
    } else {
      setFollowedTags([...followedTags, tag]);
      toast.success(`Você está seguindo #${tag}`);
    }
  };

  const handleFollowDeveloper = (devId: string, devName: string) => {
    if (followedDevelopers.includes(devId)) {
      setFollowedDevelopers(followedDevelopers.filter(id => id !== devId));
      toast.success(`Você deixou de seguir ${devName}`);
    } else {
      setFollowedDevelopers([...followedDevelopers, devId]);
      toast.success(`Você está seguindo ${devName}`);
    }
  };

  // Get popular tags from games
  const allTags = mockGames.flatMap(game => game.tags);
  const popularTags = [...new Set(allTags)].slice(0, 10);

  // Get developers from games
  const developers = [...new Set(mockGames.map(game => ({ 
    id: game.developerId, 
    name: game.developerName 
  })))].slice(0, 5);

  // Filter blocked users' posts
  const visiblePosts = mockPosts.filter(post => !blockedUsers.includes(post.userId));

  return (
    <div className="min-h-screen pt-16">
      {/* Header */}
      <section className="bg-gradient-hero border-b border-border/50 py-12 px-4">
        <div className="container mx-auto">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            <span className="bg-gradient-secondary bg-clip-text text-transparent">
              Comunidade
            </span>
          </h1>
          <p className="text-lg text-muted-foreground">
            Compartilhe suas experiências, conquistas e conecte-se com outros jogadores
          </p>
        </div>
      </section>

      <div className="container mx-auto px-4 py-12">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Sidebar */}
          <div className="lg:col-span-1 space-y-6">
            {/* Tags Populares */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
              <h3 className="font-bold text-lg mb-4">Tags Populares</h3>
              <div className="flex flex-wrap gap-2">
                {popularTags.map((tag) => {
                  const isFollowed = followedTags.includes(tag);
                  return (
                    <button
                      key={tag}
                      onClick={() => handleFollowTag(tag)}
                      className={`px-3 py-1 rounded-full text-sm transition-smooth ${
                        isFollowed
                          ? 'bg-gradient-primary text-white'
                          : 'bg-muted text-muted-foreground hover:bg-muted/80'
                      }`}
                    >
                      #{tag}
                    </button>
                  );
                })}
              </div>
            </Card>

            {/* Desenvolvedoras */}
            <Card className="p-6 bg-card/50 backdrop-blur-sm">
              <h3 className="font-bold text-lg mb-4">Desenvolvedoras</h3>
              <div className="space-y-3">
                {developers.map((dev) => {
                  const isFollowed = followedDevelopers.includes(dev.id);
                  return (
                    <div key={dev.id} className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-full bg-gradient-secondary" />
                        <span className="text-sm font-medium">{dev.name}</span>
                      </div>
                      <Button
                        size="sm"
                        variant={isFollowed ? "outline" : "hero"}
                        onClick={() => handleFollowDeveloper(dev.id, dev.name)}
                      >
                        {isFollowed ? (
                          <>
                            <UserMinus className="w-3 h-3 mr-1" />
                            Seguindo
                          </>
                        ) : (
                          <>
                            <UserPlus className="w-3 h-3 mr-1" />
                            Seguir
                          </>
                        )}
                      </Button>
                    </div>
                  );
                })}
              </div>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Create Post */}
          <Card className="p-6 bg-card/50 backdrop-blur-sm mb-8">
            <h2 className="font-bold text-lg mb-4">Criar novo post</h2>
            <Textarea
              placeholder="Compartilhe algo com a comunidade... (máx. 500 caracteres)"
              value={newPost}
              onChange={(e) => setNewPost(e.target.value)}
              maxLength={500}
              className="mb-3 min-h-[100px]"
            />
            <div className="flex items-center justify-between">
              <div className="flex gap-2">
                <Button variant="outline" size="sm">
                  <ImageIcon className="w-4 h-4 mr-2" />
                  Adicionar Imagem
                </Button>
              </div>
              <div className="flex items-center gap-3">
                <span className="text-sm text-muted-foreground">
                  {newPost.length}/500
                </span>
                <Button
                  variant="hero"
                  onClick={handlePost}
                  disabled={!newPost.trim()}
                >
                  Publicar
                </Button>
              </div>
            </div>
          </Card>

            {/* Posts Feed */}
            <div className="space-y-6">
              {visiblePosts.map((post) => {
              const isLiked = likedPosts.includes(post.id);
              
              return (
                <Card
                  key={post.id}
                  className="overflow-hidden bg-card/50 backdrop-blur-sm border-border/50 hover:border-border transition-smooth"
                >
                  {/* Post Header */}
                  <div className="p-6 pb-4">
                    <div className="flex items-start justify-between mb-3">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-gradient-primary" />
                        <div>
                          <p className="font-semibold">{post.userName}</p>
                          <p className="text-sm text-muted-foreground">
                            {new Date(post.date).toLocaleDateString('pt-BR')}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Badge variant="outline">{post.gameName}</Badge>
                        <Button
                          size="sm"
                          variant={followedUsers.includes(post.userId) ? "outline" : "secondary"}
                          onClick={() => handleFollowUser(post.userId, post.userName)}
                        >
                          {followedUsers.includes(post.userId) ? (
                            <UserMinus className="w-4 h-4" />
                          ) : (
                            <UserPlus className="w-4 h-4" />
                          )}
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          onClick={() => handleBlockUser(post.userId, post.userName)}
                        >
                          <Ban className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>

                    <p className="text-foreground mb-4">{post.content}</p>

                    {/* Post Image */}
                    {post.image && (
                      <div className="rounded-lg overflow-hidden mb-4">
                        <img
                          src={post.image}
                          alt="Post"
                          className="w-full h-auto"
                        />
                      </div>
                    )}

                    {/* Actions */}
                    <div className="flex items-center gap-6 pt-4 border-t border-border/50">
                      <button
                        className={`flex items-center gap-2 transition-smooth ${
                          isLiked
                            ? 'text-red-500'
                            : 'text-muted-foreground hover:text-foreground'
                        }`}
                        onClick={() => handleLike(post.id)}
                      >
                        <Heart
                          className={`w-5 h-5 ${isLiked ? 'fill-current' : ''}`}
                        />
                        <span className="text-sm font-medium">
                          {post.likes + (isLiked ? 1 : 0)}
                        </span>
                      </button>

                      <button className="flex items-center gap-2 text-muted-foreground hover:text-foreground transition-smooth">
                        <MessageSquare className="w-5 h-5" />
                        <span className="text-sm font-medium">{post.comments}</span>
                      </button>

                      <button className="flex items-center gap-2 text-muted-foreground hover:text-foreground transition-smooth ml-auto">
                        <Share2 className="w-5 h-5" />
                        <span className="text-sm font-medium">Compartilhar</span>
                      </button>
                    </div>
                  </div>
                </Card>
              );
            })}
            </div>

            {/* Load More */}
            <div className="text-center mt-8">
              <Button variant="outline" size="lg">
                Carregar mais posts
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Community;
