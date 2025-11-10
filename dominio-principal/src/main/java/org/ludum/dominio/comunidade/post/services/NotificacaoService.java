package org.ludum.dominio.comunidade.post.services;

import org.ludum.dominio.comunidade.post.entidades.PostId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

/**
 * Interface para envio de notificações aos usuários.
 */
public interface NotificacaoService {

    void notificarFalhaAgendamento(ContaId autorId, PostId postId, String motivoErro);

    void notificarImagemBloqueada(ContaId autorId, PostId postId);
}
