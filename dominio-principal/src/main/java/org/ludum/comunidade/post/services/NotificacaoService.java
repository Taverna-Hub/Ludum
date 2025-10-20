package org.ludum.comunidade.post.services;

import org.ludum.comunidade.post.entidades.PostId;
import org.ludum.identidade.conta.entities.ContaId;

/**
 * Interface para envio de notificações aos usuários.
 */
public interface NotificacaoService {

    void notificarFalhaAgendamento(ContaId autorId, PostId postId, String motivoErro);

    void notificarImagemBloqueada(ContaId autorId, PostId postId);
}
