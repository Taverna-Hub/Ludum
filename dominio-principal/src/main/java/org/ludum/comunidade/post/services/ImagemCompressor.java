package org.ludum.comunidade.post.services;

import java.net.URL;

/**
 * Interface para compactação de imagens.
 * Permite diferentes implementações (ex: TinyPNG, ImageMagick, etc).
 */
public interface ImagemCompressor {

    boolean excedeLimit(URL imagem);

    URL compactar(URL imagem);
}
