package org.ludum.comunidade.post.repositorios;

import org.ludum.comunidade.post.entidades.Post;
import org.ludum.comunidade.post.entidades.PostId;
import org.ludum.comunidade.post.enums.PostStatus;
import org.ludum.identidade.conta.entidades.ContaId;

import java.util.List;

public interface PostRepository {

    void salvar(Post post);

    Post obterPorId(PostId id);
    
    void remover(Post post);

    List<Post> obterTodosPosts();

    List<Post> obterPorAutor(ContaId autorId);

    List<Post> buscarPorTag(String tag);

    List<Post> obterPorStatus(PostStatus status);

}
